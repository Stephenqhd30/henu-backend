package com.henu.registration.model.dto.schoolType;

import lombok.Data;

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
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}