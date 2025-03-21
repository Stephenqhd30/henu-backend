package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.schoolType.SchoolTypeQueryRequest;
import com.henu.registration.model.entity.SchoolType;
import com.henu.registration.model.vo.schoolType.SchoolTypeVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 高校类型服务
 *
 * @author stephen qiu
 */
public interface SchoolTypeService extends IService<SchoolType> {

    /**
     * 校验数据
     *
     * @param schoolType schoolType
     * @param add 对创建的数据进行校验
     */
    void validSchoolType(SchoolType schoolType, boolean add);

    /**
     * 获取查询条件
     *
     * @param schoolTypeQueryRequest schoolTypeQueryRequest
     * @return {@link QueryWrapper<SchoolType>}
     */
    QueryWrapper<SchoolType> getQueryWrapper(SchoolTypeQueryRequest schoolTypeQueryRequest);

    /**
     * 获取高校类型封装
     *
     * @param schoolType schoolType
     * @param request request
     * @return {@link SchoolTypeVO}
     */
    SchoolTypeVO getSchoolTypeVO(SchoolType schoolType, HttpServletRequest request);

    /**
     * 分页获取高校类型封装
     *
     * @param schoolTypePage schoolTypePage
     * @param request request
     * @return {@link Page<SchoolTypeVO>}
     */
    Page<SchoolTypeVO> getSchoolTypeVOPage(Page<SchoolType> schoolTypePage, HttpServletRequest request);
}