package com.henu.registration.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * 报名状态枚举类
 * 报名状态(0-待报名,1-已报名,2-待面试,3-拟录取)
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum RegistrationStatueEnum {
	
	NO("待报名", 0),
	YES("已报名", 1),
	INTERVIEW("待发送面试通知", 2),
	ADMIT("已发送面试通知", 3);
	
	private final String text;
	
	private final Integer value;
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return {@link RegistrationStatueEnum}
	 */
	public static RegistrationStatueEnum getEnumByValue(Integer value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (RegistrationStatueEnum reviewStatusEnum : RegistrationStatueEnum.values()) {
			if (Objects.equals(reviewStatusEnum.value, value)) {
				return reviewStatusEnum;
			}
		}
		return null;
	}
}
