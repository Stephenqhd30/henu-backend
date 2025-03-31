package com.henu.registration.model.dto.schoolType;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 更新高校类型请求
 *
 * @author stephen qiu
 */
@Data
public class SchoolTypeUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;
    
    /**
     * 高校类别名称
     */
    private String typeName;
    
    
    @Serial
    private static final long serialVersionUID = 1L;
}