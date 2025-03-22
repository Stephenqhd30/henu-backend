package com.henu.registration.model.dto.reviewLog;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询审核记录请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReviewLogQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;
    
    /**
     * 报名登记表id
     */
    private Long registrationId;
    
    /**
     * 审核人id
     */
    private Long reviewerId;
    
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