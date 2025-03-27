package com.henu.registration.model.dto.messagePush;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建消息推送请求
 *
 * @author stephen qiu
 */
@Data
public class MessagePushAddRequest implements Serializable {
    
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
     * 推送时间
     */
    private Date pushTime;

    @Serial
    private static final long serialVersionUID = 1L;
}