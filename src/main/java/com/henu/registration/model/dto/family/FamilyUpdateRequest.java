package com.henu.registration.model.dto.family;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新家庭关系请求
 *
 * @author stephen qiu
 */
@Data
public class FamilyUpdateRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 称谓
	 */
	private String appellation;
	
	/**
	 * 姓名
	 */
	private String familyName;
	
	/**
	 * 工作单位及职务
	 */
	private String workDetail;
	
	/**
	 * 创建用户id
	 */
	private Long userId;
	
	private static final long serialVersionUID = 1L;
}