package com.henu.registration.rabbitmq.consumer.model;

import com.henu.registration.model.entity.MessagePush;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息队列消息基类
 *
 * @author stephen qiu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RabbitMessage implements Serializable {
	
	/**
	 * 消息id
	 */
	private String msgId;
	
	/**
	 * 消息内容
	 */
	private String msgText;
	
	@Serial
	private static final long serialVersionUID = -5714803262626105938L;
	
}