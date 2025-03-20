package com.henu.registration.config.websocket.modal;

import lombok.Data;

/**
 * Description: ws的基本返回信息体
 *
 * @author stephenqiu
 */
@Data
public class WSBaseResp<T> {
	/**
	 * ws推送给前端的消息
	 *
	 * @see WSRespTypeEnum
	 */
	private Integer type;
	private T data;
}