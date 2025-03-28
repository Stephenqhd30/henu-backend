package com.henu.registration.model.vo.messageNotice;

import com.henu.registration.model.entity.MessageNotice;
import com.henu.registration.model.vo.registrationForm.RegistrationFormVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 消息通知视图
 *
 * @author stephen
 */
@Data
public class MessageNoticeVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -2054040827014586206L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 面试时间
	 */
	private Date interviewTime;
	
	/**
	 * 面试地点
	 */
	private String interviewLocation;
	
	/**
	 * 管理员id
	 */
	private Long adminId;
	
	/**
	 * 报名登记表id
	 */
	private Long registrationId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 报名登记信息
	 */
	private RegistrationFormVO registrationFormVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param messageNoticeVO messageNoticeVO
	 * @return {@link MessageNotice}
	 */
	public static MessageNotice voToObj(MessageNoticeVO messageNoticeVO) {
		if (messageNoticeVO == null) {
			return null;
		}
		MessageNotice messageNotice = new MessageNotice();
		BeanUtils.copyProperties(messageNoticeVO, messageNotice);
		return messageNotice;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param messageNotice messageNotice
	 * @return {@link MessageNoticeVO}
	 */
	public static MessageNoticeVO objToVo(MessageNotice messageNotice) {
		if (messageNotice == null) {
			return null;
		}
		MessageNoticeVO messageNoticeVO = new MessageNoticeVO();
		BeanUtils.copyProperties(messageNotice, messageNoticeVO);
		return messageNoticeVO;
	}
}
