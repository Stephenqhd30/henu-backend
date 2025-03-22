package com.henu.registration.model.dto.family;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新家庭关系请求
 *
 * @author stephen qiu
 */
@Data
public class FamilyUpdateRequest implements Serializable {

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