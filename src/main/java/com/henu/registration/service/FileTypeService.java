package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.fileType.FileTypeQueryRequest;
import com.henu.registration.model.entity.FileType;
import com.henu.registration.model.vo.fileType.FileTypeVO;

import javax.servlet.http.HttpServletRequest;


/**
 * 文件上传类型服务
 *
 * @author stephenqiu
 * @description 针对表【file_type(文件上传类型表)】的数据库操作Service
 * @createDate 2025-03-22 14:17:12
 */
public interface FileTypeService extends IService<FileType> {
	
	/**
	 * 校验数据
	 *
	 * @param fileType fileType
	 * @param add      对创建的数据进行校验
	 */
	void validFileType(FileType fileType, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param fileTypeQueryRequest fileTypeQueryRequest
	 * @return {@link QueryWrapper<FileType>}
	 */
	QueryWrapper<FileType> getQueryWrapper(FileTypeQueryRequest fileTypeQueryRequest);
	
	/**
	 * 获取文件上传类型封装
	 *
	 * @param fileType fileType
	 * @param request  request
	 * @return {@link FileTypeVO}
	 */
	FileTypeVO getFileTypeVO(FileType fileType, HttpServletRequest request);
	
	/**
	 * 分页获取文件上传类型封装
	 *
	 * @param fileTypePage fileTypePage
	 * @param request      request
	 * @return {@link Page<FileTypeVO>}
	 */
	Page<FileTypeVO> getFileTypeVOPage(Page<FileType> fileTypePage, HttpServletRequest request);
}