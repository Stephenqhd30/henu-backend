package com.henu.registration.model.vo.user;

import com.henu.registration.model.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 *
 * @author stephen qiu
 */
@Data
public class UserVO implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 姓名
	 */
	private String userName;
	
	/**
	 * 邮箱地址
	 */
	private String userEmail;
	
	/**
	 * 联系电话
	 */
	private String userPhone;
	
	/**
	 * 性别(0-男,1-女)
	 */
	private Integer userGender;
	
	/**
	 * 用户头像
	 */
	private String userAvatar;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 封装类转对象
	 *
	 * @param userVO userVO
	 * @return User
	 */
	public static User voToObj(UserVO userVO) {
		if (userVO == null) {
			return null;
		}
		// todo 需要进行转换
		User user = new User();
		BeanUtils.copyProperties(userVO, user);
		return user;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param user user
	 * @return UserVO
	 */
	public static UserVO objToVo(User user) {
		if (user == null) {
			return null;
		}
		// todo 需要进行转换
		UserVO userVO = new UserVO();
		BeanUtils.copyProperties(user, userVO);
		return userVO;
	}
}