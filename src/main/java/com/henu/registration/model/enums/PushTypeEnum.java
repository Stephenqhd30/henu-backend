package com.henu.registration.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推送类型枚举
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum PushTypeEnum {
	
	WEBSOCKET("websocket", "websocket"),
	SMS("短信", "sms");
	
	private final String text;
	
	private final String value;
	
	/**
	 * 获取值列表
	 *
	 * @return {@link List<String>}
	 */
	public static List<String> getValues() {
		return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
	}
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return {@link PushTypeEnum}
	 */
	public static PushTypeEnum getEnumByValue(String value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (PushTypeEnum anEnum : PushTypeEnum.values()) {
			if (anEnum.value.equals(value)) {
				return anEnum;
			}
		}
		return null;
	}
	
}
