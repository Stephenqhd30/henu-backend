package com.henu.registration.model.dto.school;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑高校信息请求
 *
 * @author stephen qiu
 */
@Data
public class SchoolEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;
    
    /**
     * 高校名称
     */
    private String schoolName;

    private static final long serialVersionUID = 1L;
}