package com.henu.registration.model.dto.schoolSchoolType;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询高校与高校类型关联信息请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SchoolSchoolTypeQueryRequest extends PageRequest implements Serializable {

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
     * 高校id
     */
    private Long schoolId;
    
    /**
     * 高校类别列表(JSON存储)
     */
    private List<String> schoolTypes;
    
    /**
     * 管理员id
     */
    private Long adminId;

    private static final long serialVersionUID = 1L;
}