package com.henu.registration.rabbitmq.consumer;

import cn.hutool.json.JSONUtil;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.model.entity.MessageNotice;
import com.henu.registration.model.entity.MessagePush;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.enums.PushStatusEnum;
import com.henu.registration.rabbitmq.consumer.model.RabbitMessage;
import com.henu.registration.rabbitmq.defaultMq.DefaultRabbitMq;
import com.henu.registration.rabbitmq.defaultMq.DefaultRabbitMqWithDelay;
import com.henu.registration.rabbitmq.defaultMq.DefaultRabbitMqWithDlx;
import com.henu.registration.service.MessageNoticeService;
import com.henu.registration.service.MessagePushService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.sms.SMSUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 消息队列 RabbitMQ 消费者
 *
 * @author stephen qiu
 */
@Slf4j
@Component
public class RabbitMqConsumer {
	
	@Resource
	private SMSUtils smsUtils;
	
	@Resource
	private UserService userService;
	
	@Resource
	private MessagePushService messagePushService;
	
	@Resource
	private MessageNoticeService messageNoticeService;
	
	/**
	 * 处理普通队列消息
	 *
	 * @param message 消息内容
	 * @param channel RabbitMQ 通道
	 * @param tag     当前消息的 Delivery Tag
	 * @throws IOException 抛出异常
	 */
	@RabbitHandler
	@RabbitListener(queues = DefaultRabbitMq.QUEUE_NAME)
	public void consumeStandardQueueMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
		processMessage(message, channel, tag, "普通队列");
	}
	
	/**
	 * 处理带有死信的队列消息
	 *
	 * @param message 消息内容
	 * @param channel RabbitMQ 通道
	 * @param tag     当前消息的 Delivery Tag
	 * @throws IOException 抛出异常
	 */
	@RabbitHandler
	@RabbitListener(queues = DefaultRabbitMqWithDlx.QUEUE_WITH_DLX_NAME)
	public void consumeDlxQueueMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
		processMessage(message, channel, tag, "死信队列");
	}
	
	/**
	 * 处理死信队列中的消息
	 *
	 * @param message 消息内容
	 * @param channel RabbitMQ 通道
	 * @param tag     当前消息的 Delivery Tag
	 * @throws IOException 抛出异常
	 */
	@RabbitHandler
	@RabbitListener(queues = DefaultRabbitMqWithDlx.DLX_QUEUE_WITH_DLX_NAME)
	public void consumeDeadLetterQueueMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
		processMessage(message, channel, tag, "死信队列消息");
	}
	
	/**
	 * 处理延时队列消息
	 *
	 * @param message 消息内容
	 * @param channel RabbitMQ 通道
	 * @param tag     当前消息的 Delivery Tag
	 * @throws IOException 抛出异常
	 */
	@RabbitHandler
	@RabbitListener(queues = DefaultRabbitMqWithDelay.DLX_QUEUE_WITH_DELAY_NAME)
	public void consumeDelayedQueueMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
		processMessage(message, channel, tag, "延时队列");
	}
	
	/**
	 * 公用的消息处理逻辑
	 *
	 * @param message   消息内容
	 * @param channel   RabbitMQ 通道
	 * @param tag       当前消息的 Delivery Tag
	 * @param queueType 队列类型描述（如 "普通队列"、"死信队列" 等）
	 * @throws IOException 抛出异常
	 */
	private void processMessage(Message message, Channel channel, long tag, String queueType) throws IOException {
		try {
			// 解析 RabbitMessage
			RabbitMessage rabbitMessage = JSONUtil.toBean(new String(message.getBody()), RabbitMessage.class);
			log.info("收到{}消息: {}", queueType, rabbitMessage);
			MessagePush messagePush = JSONUtil.toBean(rabbitMessage.getMsgText(), MessagePush.class);
			if (messagePush == null) {
				log.warn("消息内容为空，丢弃: msgId={}", rabbitMessage.getMsgId());
				channel.basicNack(tag, false, false);
				return;
			}
			// 生成短信参数
			String params = smsUtils.getParams(messagePush);
			// 获取用户信息
			User user = userService.getById(messagePush.getUserId());
			if (user == null) {
				log.warn("用户不存在，消息进入死信队列: userId={}", messagePush.getUserId());
				channel.basicNack(tag, false, false);
				return;
			}
			// 发送短信
			smsUtils.sendMessage(user.getUserPhone(), params);
			// 更新消息推送状态
			messagePushService.lambdaUpdate()
					.set(MessagePush::getPushMessage, params)
					.set(MessagePush::getPushStatus, PushStatusEnum.SUCCEED.getValue())
					.eq(MessagePush::getId, messagePush.getId())
					.update();
			// 同步更新消息通知表
			messageNoticeService.lambdaUpdate()
					.set(MessageNotice::getPushStatus, PushStatusEnum.SUCCEED.getValue())
					.eq(MessageNotice::getId, messagePush.getMessageNoticeId())
					.update();
			// 确认消息处理成功
			channel.basicAck(tag, false);
		} catch (Exception e) {
			log.error("处理{}消息时发生异常: {}", queueType, e.getMessage(), e);
			channel.basicNack(tag, false, false);
		}
	}
}
