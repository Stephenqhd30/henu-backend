package com.henu.registration.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * 消息类型枚举类
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {
	
	WEBSOCKET("websocket", "websocket"),
	EMAIL("邮件", "email"),
	SMS("短信", "sms");
	
	private final String text;
	
	private final String value;
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return {@link MessageTypeEnum}
	 */
	public static MessageTypeEnum getEnumByValue(String value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (MessageTypeEnum reviewStatusEnum : MessageTypeEnum.values()) {
			if (Objects.equals(reviewStatusEnum.value, value)) {
				return reviewStatusEnum;
			}
		}
		return null;
	}
}
