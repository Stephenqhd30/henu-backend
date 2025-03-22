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
	 * 民族
	 */
	private String ethnic;
	
	/**
	 * 入党时间
	 */
	private String partyTime;
	
	/**
	 * 出生日期
	 */
	private String birthDate;
	
	/**
	 * 婚姻状况(0-未婚，1-已婚)
	 */
	private Integer marryStatus;
	
	/**
	 * 紧急联系电话
	 */
	private String emergencyPhone;
	
	/**
	 * 家庭住址
	 */
	private String address;
	
	/**
	 * 工作经历
	 */
	private String workExperience;
	
	/**
	 * 主要学生干部经历及获奖情况
	 */
	private String studentLeaderAwards;
	
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