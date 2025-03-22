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
public enum EducationalStageEnum {
	
	SPECIALTY("专科", "专科"),
	UNDERGRADUATE_COURSE("本科", "本科"),
	POSTGRADUATE("硕士", "硕士"),
	DOCTOR_DEGREE("博士", "博士"),
	;
	private final String text;
	
	private final String value;
	
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
