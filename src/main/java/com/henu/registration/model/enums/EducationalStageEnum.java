package com.henu.registration.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * 教育阶段枚举类
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum EducationalStageEnum {
	
	UNDERGRADUATE_COURSE("本科", "本科", 1),
	POSTGRADUATE("硕士", "硕士", 2),
	DOCTOR_DEGREE("博士", "博士", 3);
	private final String text;
	
	private final String value;
	
	private final int rank;
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return {@link EducationalStageEnum}
	 */
	public static EducationalStageEnum getEnumByValue(String value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (EducationalStageEnum reviewStatusEnum : EducationalStageEnum.values()) {
			if (Objects.equals(reviewStatusEnum.value, value)) {
				return reviewStatusEnum;
			}
		}
		return null;
	}
}
