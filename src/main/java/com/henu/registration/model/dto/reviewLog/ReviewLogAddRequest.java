package com.henu.registration.model.dto.reviewLog;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建审核记录请求
 *
 * @author stephen qiu
 */
@Data
public class ReviewLogAddRequest implements Serializable {
    
    /**
     * 报名登记表id
     */
    private Long registrationId;
    
    /**
     * 报名登记表id
     */
    private List<Long> registrationIds;
    
    /**
     * 审核状态(0-待审核,1-审核通过,2-审核不通过)
     */
    private Integer reviewStatus;
    
    /**
     * 审核意见
     */
    private String reviewComments;

    @Serial
    private static final long serialVersionUID = 1L;
}