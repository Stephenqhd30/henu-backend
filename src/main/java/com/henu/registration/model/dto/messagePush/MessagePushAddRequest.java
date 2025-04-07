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
     * 消息通知id
     */
    private Long messageNoticeId;
    
    /**
     * 消息通知列表
     */
    private List<Long> messageNoticeIds;
    
    /**
     * 推送方式(websocket/email/sms/other)
     */
    private String pushType;

    @Serial
    private static final long serialVersionUID = 1L;
}