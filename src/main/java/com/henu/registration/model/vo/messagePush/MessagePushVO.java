package com.henu.registration.model.vo.messagePush;

import com.henu.registration.model.entity.MessagePush;
import com.henu.registration.model.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 消息推送视图
 *
 * @author stephen
 */
@Data
public class MessagePushVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 2664132887873087037L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 消息通知id
	 */
	private Long messageNoticeId;
	
	/**
	 * 推送方式(websocket/email/sms/other)
	 */
	private String pushType;
	
	/**
	 * 推送状态(0-未推送,1-成功,2-失败,3-重试中)
	 */
	private Integer pushStatus;
	
	/**
	 * 推送消息内容
	 */
	private String pushMessage;
	
	/**
	 * 推送时间
	 */
	private Date pushTime;
	
	/**
	 * 失败重试次数
	 */
	private Integer retryCount;
	
	/**
	 * 失败原因
	 */
	private String errorMessage;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 用户信息
	 */
	private UserVO userVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param messagePushVO messagePushVO
	 * @return {@link MessagePush}
	 */
	public static MessagePush voToObj(MessagePushVO messagePushVO) {
		if (messagePushVO == null) {
			return null;
		}
		MessagePush messagePush = new MessagePush();
		BeanUtils.copyProperties(messagePushVO, messagePush);
		return messagePush;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param messagePush messagePush
	 * @return {@link MessagePushVO}
	 */
	public static MessagePushVO objToVo(MessagePush messagePush) {
		if (messagePush == null) {
			return null;
		}
		MessagePushVO messagePushVO = new MessagePushVO();
		BeanUtils.copyProperties(messagePush, messagePushVO);
		return messagePushVO;
	}
}
