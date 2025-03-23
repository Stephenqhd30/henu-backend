package com.henu.registration.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * 阅读状态枚举类
 * 审核状态：0-未读,1-已读
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum ReadStatusEnum {
	
	READ("已读", 0),
	UNREAD("未读", 1);
	private final String text;
	
	private final Integer value;
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return {@link ReadStatusEnum}
	 */
	public static ReadStatusEnum getEnumByValue(Integer value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (ReadStatusEnum reviewStatusEnum : ReadStatusEnum.values()) {
			if (Objects.equals(reviewStatusEnum.value, value)) {
				return reviewStatusEnum;
			}
		}
		return null;
	}
}
