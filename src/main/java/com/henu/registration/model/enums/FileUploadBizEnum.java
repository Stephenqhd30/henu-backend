package com.henu.registration.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum FileUploadBizEnum {
	
	USER_AVATAR("用户头像", "user_avatar"),
	PERSONAL_RESUME("个人简历", "personal_resume"),
	ID_CARD("身份证", "id_card"),
	BACHELOR_EDUCATION_CERTIFICATE("本科学历证书", "bachelor_education_certificate"),
	MASTER_EDUCATION_CERTIFICATE("硕士学历证书", "master_education_certificate"),
	PARTY_MEMBER_CERTIFICATE("党员身份证明", "party_member_certificate"),
	THOUGHT_QUALITY_ASSESSMENT_OPINION("思想品德鉴定意见", "thought_quality_assessment_opinion"),
	MAJOR_STUDENT_CADRE_CERTIFICATE("主要学生干部证明", "major_student_cadre_certificate"),
	PROFESSIONAL_EXPERIENCE("职业经历", "professional_experience"),
	OTHER_MATERIALS("其他证明材料", "other_materials"),;
	
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
	 * @return {@link FileUploadBizEnum}
	 */
	public static FileUploadBizEnum getEnumByValue(String value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (FileUploadBizEnum anEnum : FileUploadBizEnum.values()) {
			if (anEnum.value.equals(value)) {
				return anEnum;
			}
		}
		return null;
	}
	
}
