package com.henu.registration.config.easyexcel.core;

import java.util.List;

/**
 * Excel返回对象
 *
 * @param <T> 泛型T
 * @author stephenqiu
 */
public interface ExcelResult<T> {

    /**
     * 对象列表
     */
    List<T> getList();

    /**
     * 错误列表
     */
    List<String> getErrorList();

    /**
     * 导入响应
     */
    String getResponse();

}
