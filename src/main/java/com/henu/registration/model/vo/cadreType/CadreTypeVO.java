package com.henu.registration.model.vo.cadreType;

import com.henu.registration.model.entity.CadreType;
import com.henu.registration.model.vo.admin.AdminVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 干部类型视图
 *
 * @author stephen
 */
@Data
public class CadreTypeVO implements Serializable {
	
	private static final long serialVersionUID = -797746281005285141L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 干部类型
	 */
	private String type;
	
	/**
	 * 管理员id
	 */
	private Long adminId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 封装类转对象
	 *
	 * @param cadreTypeVO cadreTypeVO
	 * @return {@link CadreType}
	 */
	public static CadreType voToObj(CadreTypeVO cadreTypeVO) {
		if (cadreTypeVO == null) {
			return null;
		}
		CadreType cadreType = new CadreType();
		BeanUtils.copyProperties(cadreTypeVO, cadreType);
		return cadreType;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param cadreType cadreType
	 * @return {@link CadreTypeVO}
	 */
	public static CadreTypeVO objToVo(CadreType cadreType) {
		if (cadreType == null) {
			return null;
		}
		CadreTypeVO cadreTypeVO = new CadreTypeVO();
		BeanUtils.copyProperties(cadreType, cadreTypeVO);
		return cadreTypeVO;
	}
}
