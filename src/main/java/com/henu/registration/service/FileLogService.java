package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.fileLog.FileLogQueryRequest;
import com.henu.registration.model.entity.FileLog;
import com.henu.registration.model.entity.FileType;
import com.henu.registration.model.vo.fileLog.FileLogVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author stephenqiu
 * @description 针对表【file_log(文件上传日志表)】的数据库操作Service
 * @createDate 2025-03-22 00:41:15
 */
public interface FileLogService extends IService<FileLog> {
	
	/**
	 * 校验文件
	 *
	 * @param multipartFile     multipartFile
	 * @param fileType 文件类型
	 */
	void validFile(MultipartFile multipartFile, FileType fileType);
	
	/**
	 * 获取查询条件
	 *
	 * @param fileLogQueryRequest fileLogQueryRequest
	 * @return {@link QueryWrapper<FileLog>}
	 */
	QueryWrapper<FileLog> getQueryWrapper(FileLogQueryRequest fileLogQueryRequest);
	
	/**
	 * 获取文件上传日志表封装
	 *
	 * @param fileLog fileLog
	 * @param request request
	 * @return {@link FileLogVO}
	 */
	FileLogVO getFileLogVO(FileLog fileLog, HttpServletRequest request);
	
	/**
	 * 分页获取文件上传日志表封装
	 *
	 * @param fileLogPage fileLogPage
	 * @param request     request
	 * @return {@link Page<FileLogVO>}
	 */
	Page<FileLogVO> getFileLogVOPage(Page<FileLog> fileLogPage, HttpServletRequest request);
}
