package com.henu.registration.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.entity.MessagePush;
import com.henu.registration.model.enums.PushStatusEnum;
import com.henu.registration.service.MessagePushService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.sms.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author stephenqiu
 */
@Slf4j
// @Component
public class MessagePushScheduler {
	
	@Resource
	private MessagePushService messagePushService;
	
	@Resource
	private SMSUtils smsUtils;
	
	@Resource
	private UserService userService;
	
	/**
	 * 每30分钟执行一次
	 */
	@Scheduled(fixedRate = 30 * 60 * 1000)
	@Transactional
	public void pushMessages() {
		log.info("开始执行消息推送任务");
		// 查询未推送的消息
		List<MessagePush> messages = messagePushService.list(
				Wrappers.<MessagePush>lambdaQuery()
						.in(MessagePush::getPushStatus, PushStatusEnum.NOT_PUSHED.getValue(), PushStatusEnum.RETRYING.getValue())
						// 失败重试最多3次
						.lt(MessagePush::getRetryCount, 3)
		);
		log.info("查询到 {} 条待推送的消息", messages.size());
		for (MessagePush messagePush : messages) {
			try {
				boolean success = sendMessage(messagePush);
				messagePush.setPushStatus(success ? PushStatusEnum.SUCCEED.getValue() : PushStatusEnum.FAILED.getValue());
			} catch (Exception e) {
				log.error("消息推送失败: {}", e.getMessage(), e);
				messagePush.setPushStatus(PushStatusEnum.FAILED.getValue());
				messagePush.setErrorMessage(e.getMessage());
				messagePush.setRetryCount(messagePush.getRetryCount() + 1);
			}
			messagePush.setUpdateTime(new Date());
			messagePushService.updateById(messagePush);
		}
		
		log.info("消息推送任务执行完毕，共处理 {} 条消息", messages.size());
	}
	
	
	/**
	 * 发送消息
	 *
	 * @param message message
	 * @return boolean
	 */
	private boolean sendMessage(MessagePush message) {
		return switch (message.getPushType()) {
			case "websocket" -> pushWebSocket(message);
			case "sms" -> pushSms(message);
			default -> {
				log.warn("未知的推送类型: {}", message.getPushType());
				yield false;
			}
		};
	}
	
	
	/**
	 * 推送 WebSocket 消息
	 *
	 * @param message message
	 * @return boolean
	 */
	private boolean pushWebSocket(MessagePush message) {
		log.info("推送 WebSocket 消息: {}", message.getPushMessage());
		// WebSocket 推送逻辑（待实现）
		return true;
	}
	
	/**
	 * 推送 SMS 消息
	 *
	 * @param messagePush messagePush
	 * @return boolean
	 */
	private boolean pushSms(MessagePush messagePush) {
		log.info("推送短信，用户ID: {}, 内容: {}", messagePush.getUserId(), messagePush.getPushMessage());
		
		try {
			// 获取用户手机号
			String userPhone = userService.getById(messagePush.getUserId()).getUserPhone();
			if (userPhone == null || userPhone.isEmpty()) {
				log.warn("用户ID {} 无手机号，跳过短信推送", messagePush.getUserId());
				return false;
			}
			
			smsUtils.sendWithRetry(userPhone, messagePush.getPushMessage());
			log.info("短信发送成功，用户ID: {}", messagePush.getUserId());
			return true;
		} catch (Exception e) {
			log.error("短信推送失败，用户ID: {}, 错误: {}", messagePush.getUserId(), e.getMessage());
			throw new BusinessException(ErrorCode.SMS_ERROR, "短信发送失败: " + e.getMessage());
		}
	}
}