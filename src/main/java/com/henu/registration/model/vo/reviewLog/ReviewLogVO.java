package com.henu.registration.model.vo.reviewLog;

import cn.hutool.json.JSONUtil;
import com.henu.registration.model.entity.ReviewLog;
import com.henu.registration.model.vo.admin.AdminVO;
import com.henu.registration.model.vo.user.UserVO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 审核记录视图
 *
 * @author stephen
 */
@Data
public class ReviewLogVO implements Serializable {
    
    private static final long serialVersionUID = 8741670101284551406L;
    /**
     * id
     */
    private Long id;
    
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
    
    /**
     * 审核时间
     */
    private Date reviewTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private AdminVO adminVO;

    /**
     * 封装类转对象
     *
     * @param reviewLogVO reviewLogVO
     * @return {@link ReviewLog}
     */
    public static ReviewLog voToObj(ReviewLogVO reviewLogVO) {
        if (reviewLogVO == null) {
            return null;
        }
        ReviewLog reviewLog = new ReviewLog();
        BeanUtils.copyProperties(reviewLogVO, reviewLog);
        return reviewLog;
    }

    /**
     * 对象转封装类
     *
     * @param reviewLog reviewLog
     * @return {@link ReviewLogVO}
     */
    public static ReviewLogVO objToVo(ReviewLog reviewLog) {
        if (reviewLog == null) {
            return null;
        }
        ReviewLogVO reviewLogVO = new ReviewLogVO();
        BeanUtils.copyProperties(reviewLog, reviewLogVO);
        return reviewLogVO;
    }
}
