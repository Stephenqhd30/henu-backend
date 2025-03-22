package com.henu.registration.model.dto.education;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑教育经历表请求
 *
 * @author stephen qiu
 */
@Data
public class EducationEditRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 高校编号
	 */
	private Long schoolId;
	
	/**
	 * 教育阶段
	 */
	private String educationalStage;
	
	/**
	 * 专业
	 */
	private String major;
	
	/**
	 * 学习起止年月
	 */
	private String studyTime;
	
	/**
	 * 证明人
	 */
	private String certifier;
	
	/**
	 * 证明人联系电话
	 */
	private String certifierPhone;
	
	private static final long serialVersionUID = 1L;
}