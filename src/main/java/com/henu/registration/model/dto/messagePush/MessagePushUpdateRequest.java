package com.henu.registration.model.dto.messagePush;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 更新消息推送请求
 *
 * @author stephen qiu
 */
@Data
public class MessagePushUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;
    
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

    @Serial
    private static final long serialVersionUID = 1L;
}