package com.henu.registration.utils.sms;

import cn.hutool.json.JSONUtil;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.constants.SmsConstant;
import com.henu.registration.manager.redis.RedisLimiterManager;
import com.henu.registration.model.entity.MessageNotice;
import com.henu.registration.model.entity.MessagePush;
import com.henu.registration.model.entity.User;
import com.henu.registration.service.MessageNoticeService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.redisson.rateLimit.model.TimeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 短信发送工具类
 *
 * @author stephenqiu
 */
@Slf4j
@Component
public class SMSUtils {
	
	@Resource
	private IAcsClient smsClient;
	
	@Resource
	private RedisLimiterManager redisLimiterManager;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	@Resource
	private UserService userService;
	
	@Resource
	private MessageNoticeService messageNoticeService;
	
	// 最大重试次数
	private static final int MAX_RETRY_COUNT = 3;
	private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM");
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd");
	private static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("HH");
	
	/**
	 * 发送验证码短信用于密码重置
	 *
	 * @param phoneNumbers 接收验证码的手机号
	 * @param verifyCode   生成的验证码
	 */
	public void sendRecoveryCode(String phoneNumbers, String verifyCode) {
		// 设置短信请求
		SendSmsRequest request = new SendSmsRequest();
		request.setPhoneNumbers(phoneNumbers);
		request.setTemplateCode(SmsConstant.PASSWORD);
		request.setSignName(SmsConstant.SIGN_NAME);
		request.setTemplateParam("{\"code\":\"" + verifyCode + "\"}");
		redisLimiterManager.doRateLimit(phoneNumbers, new TimeModel(1L, TimeUnit.MINUTES), 2L, 1L);
		try {
			// 调用短信服务发送验证码
			SendSmsResponse response = smsClient.getAcsResponse(request);
			if (!"OK".equals(response.getCode())) {
				log.error("验证码发送失败，错误码：{}，错误信息：{}", response.getCode(), response.getMessage());
				throw new BusinessException(ErrorCode.SMS_ERROR, "验证码发送失败：" + response.getMessage());
			}
			// 将验证码存入 Redis，设置过期时间为 5 分钟
			// 使用手机号作为 key
			String redisKey = "verify_code:" + phoneNumbers;
			// 存入 Redis，并设置过期时间为 5 分钟
			redisTemplate.opsForValue().set(redisKey, verifyCode, 5, TimeUnit.MINUTES);
			log.info("密码重置验证码已发送，手机号：{}", phoneNumbers);
		} catch (ClientException e) {
			log.error("验证码发送异常：{}", e.getMessage(), e);
			throw new BusinessException(ErrorCode.SMS_ERROR, "验证码发送异常：" + e.getMessage());
		}
	}
	
	/**
	 * 校验验证码是否正确
	 *
	 * @param phoneNumbers 用户的手机号
	 * @param inputCode    用户输入的验证码
	 */
	public void verifyRecoveryCode(String phoneNumbers, String inputCode) {
		// 从 Redis 获取存储的验证码
		String redisKey = "verify_code:" + phoneNumbers;
		String storedCode = redisTemplate.opsForValue().get(redisKey);
		
		if (storedCode == null) {
			// 如果 Redis 中没有存储该验证码，表示验证码已过期或从未发送
			log.warn("验证码已过期或不存在，手机号：{}", phoneNumbers);
			throw new BusinessException(ErrorCode.SMS_ERROR, "验证码已过期或不存在，请重新发送");
		}
		// 校验用户输入的验证码与存储的验证码是否匹配
		if (!storedCode.equals(inputCode)) {
			// 如果不匹配，返回验证码错误
			log.warn("验证码错误，手机号：{}", phoneNumbers);
			throw new BusinessException(ErrorCode.SMS_ERROR, "验证码错误，请重新输入");
		}
		// 如果验证码正确，可以进行后续操作
		log.info("验证码正确，手机号：{}", phoneNumbers);
	}
	
	/**
	 * 发送短信，失败自动重试
	 *
	 * @param phoneNumber 目标手机号
	 * @param params      短信参数
	 */
	public void sendWithRetry(String phoneNumber, String params) {
		int retryCount = 0;
		while (retryCount < MAX_RETRY_COUNT) {
			try {
				sendMessage(phoneNumber, params);
				log.info("面试通知短信发送成功 -> 手机号：{}，内容：{}", phoneNumber, params);
				return;
			} catch (Exception e) {
				retryCount++;
				log.warn("面试通知短信发送失败 -> 手机号：{}，错误信息：{}，重试次数：{}", phoneNumber, e.getMessage(), retryCount);
			}
		}
		log.error("面试通知短信最终发送失败 -> 手机号：{}，已达最大重试次数", phoneNumber);
	}
	
	/**
	 * 发送短信
	 *
	 * @param phoneNumber 接收短信的手机号
	 * @param param       短信模板中的参数，JSON格式
	 */
	public void sendMessage(String phoneNumber, String param) {
		SendSmsRequest request = new SendSmsRequest();
		request.setSignName(SmsConstant.SIGN_NAME);
		request.setTemplateCode(SmsConstant.INTERVIEW);
		request.setPhoneNumbers(phoneNumber);
		request.setTemplateParam(param);
		
		try {
			SendSmsResponse response = smsClient.getAcsResponse(request);
			if (!"OK".equals(response.getCode())) {
				log.error("短信发送失败 -> 手机号：{}，错误码：{}，错误信息：{}", phoneNumber, response.getCode(), response.getMessage());
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "短信发送失败：" + response.getMessage());
			}
			log.info("短信发送成功 -> 手机号：{}，内容：{}", phoneNumber, param);
		} catch (ClientException e) {
			log.error("短信发送异常 -> 手机号：{}，错误信息：{}", phoneNumber, e.getMessage(), e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "短信发送异常：" + e.getMessage());
		}
	}
	
	/**
	 * 组装短信模板参数
	 */
	public String getParams(MessagePush messagePush) {
		Long messageNoticeId = messagePush.getMessageNoticeId();
		// 查询面试通知信息（避免重复查询）
		MessageNotice messageNotice = messageNoticeService.getById(messageNoticeId);
		Date interviewTime = messageNotice.getInterviewTime();
		String interviewLocation = messageNotice.getInterviewLocation();
		// 查询用户信息（避免重复查询）
		Long userId = messagePush.getUserId();
		String userName = Optional.ofNullable(userService.getById(userId))
				.map(User::getUserName)
				.orElse("未知用户");
		// 解析日期
		Map<String, String> paramMap = Map.of(
				"userName", userName,
				"year", YEAR_FORMAT.format(interviewTime),
				"month", MONTH_FORMAT.format(interviewTime),
				"day", DAY_FORMAT.format(interviewTime),
				"time", HOUR_FORMAT.format(interviewTime),
				"location", interviewLocation
		);
		return JSONUtil.toJsonStr(paramMap);
	}
	
}