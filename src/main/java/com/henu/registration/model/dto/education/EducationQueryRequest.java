package com.henu.registration.model.dto.education;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询教育经历表请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EducationQueryRequest extends PageRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * id
	 */
	private Long notId;
	
	/**
	 * 搜索词
	 */
	private String searchText;
	
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
	
	/**
	 * 创建用户 id
	 */
	private Long userId;
	
	private static final long serialVersionUID = 1L;
}