package com.henu.registration.model.dto.family;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询家庭关系请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FamilyQueryRequest extends PageRequest implements Serializable {

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
     * 称谓
     */
    private String appellation;
    
    /**
     * 姓名
     */
    private String familyName;
    
    /**
     * 工作单位及职务
     */
    private String workDetail;
    
    /**
     * 创建用户id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}