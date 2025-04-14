package com.henu.registration.model.vo.message;

import com.henu.registration.model.entity.Message;
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
public class MessageVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 2728792759694919784L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 通知主题
	 */
	private String title;
	
	/**
	 * 通知内容
	 */
	private String content;
	
	/**
	 * 创建人id
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
	 * @param messageVO messageVO
	 * @return {@link Message}
	 */
	public static Message voToObj(MessageVO messageVO) {
		if (messageVO == null) {
			return null;
		}
		Message message = new Message();
		BeanUtils.copyProperties(messageVO, message);
		return message;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param message message
	 * @return {@link MessageVO}
	 */
	public static MessageVO objToVo(Message message) {
		if (message == null) {
			return null;
		}
		MessageVO messageVO = new MessageVO();
		BeanUtils.copyProperties(message, messageVO);
		return messageVO;
	}
}
