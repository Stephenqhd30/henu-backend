package com.henu.registration.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * 审核状态枚举类
 * 审核状态：0-待审核, 1-通过, 2-拒绝
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum UserGenderEnum {
	
	MALE("男", 0),
	FEMALE("女", 1);
	private final String text;
	
	private final Integer value;
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return {@link UserGenderEnum}
	 */
	public static UserGenderEnum getEnumByValue(Integer value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (UserGenderEnum reviewStatusEnum : UserGenderEnum.values()) {
			if (Objects.equals(reviewStatusEnum.value, value)) {
				return reviewStatusEnum;
			}
		}
		return null;
	}
}
