package com.henu.registration.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * 系统消息类型枚举类
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum SystemMessageTypeEnum {
	
	/**
	 * 系统维护通知
	 */
	SYSTEM_MAINTAIN_NOTICE("系统维护通知", "system_maintain_notice"),
	/**
	 * 系统公告
	 */
	SYSTEM_NOTICE("系统公告", "system_notice");
	
	private final String text;
	
	private final String value;
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return {@link SystemMessageTypeEnum}
	 */
	public static SystemMessageTypeEnum getEnumByValue(String value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (SystemMessageTypeEnum reviewStatusEnum : SystemMessageTypeEnum.values()) {
			if (Objects.equals(reviewStatusEnum.value, value)) {
				return reviewStatusEnum;
			}
		}
		return null;
	}
}
