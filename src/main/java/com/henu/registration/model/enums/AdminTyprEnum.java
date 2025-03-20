package com.henu.registration.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员类型枚举类
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum AdminTyprEnum {
	
	/**
	 * 系统管理员
	 */
	SYSTEM_ADMIN("系统管理员", "system_admin"),
	
	/**
	 * 普通管理员
	 */
	ADMIN("普通管理员", "admin");
	
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
	 * @return {@link AdminTyprEnum}
	 */
	public static AdminTyprEnum getEnumByValue(String value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (AdminTyprEnum anEnum : AdminTyprEnum.values()) {
			if (anEnum.value.equals(value)) {
				return anEnum;
			}
		}
		return null;
	}
	
}
