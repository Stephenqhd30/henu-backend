package com.henu.registration.model.vo.family;

import cn.hutool.json.JSONUtil;
import com.henu.registration.model.entity.Family;
import com.henu.registration.model.vo.user.UserVO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 家庭关系视图
 *
 * @author stephen
 */
@Data
public class FamilyVO implements Serializable {
	
	private static final long serialVersionUID = -1764919810208416171L;
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
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 创建用户信息
	 */
	private UserVO userVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param familyVO familyVO
	 * @return {@link Family}
	 */
	public static Family voToObj(FamilyVO familyVO) {
		if (familyVO == null) {
			return null;
		}
		Family family = new Family();
		BeanUtils.copyProperties(familyVO, family);
		return family;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param family family
	 * @return {@link FamilyVO}
	 */
	public static FamilyVO objToVo(Family family) {
		if (family == null) {
			return null;
		}
		FamilyVO familyVO = new FamilyVO();
		BeanUtils.copyProperties(family, familyVO);
		return familyVO;
	}
}
