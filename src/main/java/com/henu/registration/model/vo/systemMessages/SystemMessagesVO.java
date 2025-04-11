package com.henu.registration.model.vo.systemMessages;

import com.henu.registration.model.entity.SystemMessages;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统消息视图
 *
 * @author stephen
 */
@Data
public class SystemMessagesVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -451875547520767350L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 通知标题
	 */
	private String title;
	
	/**
	 * 消息内容
	 */
	private String content;
	
	/**
	 * 推送状态(0-未推送,1-成功,2-失败,3-重试中)
	 */
	private Integer pushStatus;
	
	/**
	 * 消息类型
	 */
	private String type;
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
	 * @param systemMessagesVO systemMessagesVO
	 * @return {@link SystemMessages}
	 */
	public static SystemMessages voToObj(SystemMessagesVO systemMessagesVO) {
		if (systemMessagesVO == null) {
			return null;
		}
		SystemMessages systemMessages = new SystemMessages();
		BeanUtils.copyProperties(systemMessagesVO, systemMessages);
		return systemMessages;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param systemMessages systemMessages
	 * @return {@link SystemMessagesVO}
	 */
	public static SystemMessagesVO objToVo(SystemMessages systemMessages) {
		if (systemMessages == null) {
			return null;
		}
		SystemMessagesVO systemMessagesVO = new SystemMessagesVO();
		BeanUtils.copyProperties(systemMessages, systemMessagesVO);
		return systemMessagesVO;
	}
}
