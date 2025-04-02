package com.henu.registration.utils.sms;

import cn.hutool.json.JSONUtil;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.SmsConstant;
import com.henu.registration.manager.redis.RedisLimiterManager;
import com.henu.registration.model.entity.MessageNotice;
import com.henu.registration.model.entity.MessagePush;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.service.MessageNoticeService;
import com.henu.registration.service.RegistrationFormService;
import com.henu.registration.utils.redisson.KeyPrefixConstants;
import com.henu.registration.utils.redisson.cache.CacheUtils;
import com.henu.registration.utils.redisson.rateLimit.model.TimeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
	private RegistrationFormService registrationFormService;
	
	@Resource
	private MessageNoticeService messageNoticeService;
	
	// 最大重试次数
	private static final int MAX_RETRY_COUNT = 3;
	private static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yyyy");
	private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM");
	private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd");
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
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
		// redisLimiterManager.doRateLimit(phoneNumbers, new TimeModel(1L, TimeUnit.MINUTES), 2L, 1L);
		try {
			// 调用短信服务发送验证码
			SendSmsResponse response = smsClient.getAcsResponse(request);
			if (!"OK".equals(response.getCode())) {
				log.error("验证码发送失败，错误码：{}，错误信息：{}", response.getCode(), response.getMessage());
				throw new BusinessException(ErrorCode.SMS_ERROR, "验证码发送失败：" + response.getMessage());
			}
			// 将验证码存入 Redis，设置过期时间为 5 分钟
			// 使用手机号作为 key
			String redisKey = KeyPrefixConstants.CAPTCHA_PREFIX + phoneNumbers;
			// 存入 Redis，并设置过期时间为 5 分钟
			CacheUtils.putString(redisKey, verifyCode);
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
		String redisKey = KeyPrefixConstants.CAPTCHA_PREFIX + phoneNumbers;
		String storedCode = CacheUtils.getString(redisKey);
		
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
		// 一次性查询所需数据，避免N+1查询问题
		MessageNotice messageNotice = messageNoticeService.getById(messageNoticeId);
		Long registrationId = messageNotice.getRegistrationId();
		RegistrationForm registrationForm = registrationFormService.getById(registrationId);
		// 转换面试时间为 LocalDateTime
		LocalDateTime interviewTime = messageNotice.getInterviewTime().toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		
		// 直接格式化时间
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("userName", registrationForm.getUserName());
		paramMap.put("year", YEAR_FORMAT.format(interviewTime));
		paramMap.put("mouth", MONTH_FORMAT.format(interviewTime));
		paramMap.put("day", DAY_FORMAT.format(interviewTime));
		paramMap.put("time", TIME_FORMAT.format(interviewTime));
		paramMap.put("location", messageNotice.getInterviewLocation());
		
		return JSONUtil.toJsonStr(paramMap);
	}
	
}