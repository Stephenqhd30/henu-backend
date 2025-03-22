package com.henu.registration.model.dto.deadline;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询截止时间请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeadlineQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;
    
    /**
     * 截止日期
     */
    private Date deadlineTime;
    
    /**
     * 管理员id
     */
    private Long adminId;

    private static final long serialVersionUID = 1L;
}