package com.henu.registration.easyexcel.modal.registrationForm;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 导入报名表请求
 *
 * @author: stephen qiu
 * @create: 2025-04-12 15:58
 **/
@Data
public class ExportRegistrationFormRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = 4071583990310491974L;
	
	/**
	 * 用户id列表
	 */
	List<Long> userIds;
}
