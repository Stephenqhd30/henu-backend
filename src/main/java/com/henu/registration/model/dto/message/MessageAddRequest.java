package com.henu.registration.model.dto.message;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建消息通知请求
 *
 * @author stephen qiu
 */
@Data
public class MessageAddRequest implements Serializable {
    
    /**
     * 通知主题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;

    @Serial
    private static final long serialVersionUID = 1L;
}