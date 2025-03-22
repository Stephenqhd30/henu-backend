package com.henu.registration.model.dto.reviewLog;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新审核记录请求
 *
 * @author stephen qiu
 */
@Data
public class ReviewLogUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;
    
    /**
     * 报名登记表id
     */
    private Long registrationId;
    
    /**
     * 审核状态(0-待审核,1-审核通过,2-审核不通过)
     */
    private Integer reviewStatus;
    
    /**
     * 审核意见
     */
    private String reviewComments;

    private static final long serialVersionUID = 1L;
}