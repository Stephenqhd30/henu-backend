package com.henu.registration.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.model.dto.fileLog.FileLogQueryRequest;
import com.henu.registration.model.dto.fileLog.UploadFileRequest;
import com.henu.registration.model.entity.FileLog;
import com.henu.registration.model.entity.FileType;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.fileLog.FileLogVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.FileLogService;
import com.henu.registration.service.FileTypeService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.encrypt.MD5Utils;
import com.henu.registration.utils.oss.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 文件接口
 *
 * @author stephenqiu
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileLogController {
	
	@Resource
	private FileLogService fileLogService;
	
	@Resource
	private FileTypeService fileTypeService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private AdminService adminService;
	
	/**
	 * 文件上传(使用Minio对象存储)
	 *
	 * @param multipartFile     multipartFile
	 * @param uploadFileRequest uploadFileRequest
	 * @param request           request
	 * @return BaseResponse<String>
	 */
	@PostMapping("/upload")
	public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
	                                       UploadFileRequest uploadFileRequest, HttpServletRequest request) {
		String biz = uploadFileRequest.getBiz();
		LambdaQueryWrapper<FileType> eq = Wrappers.lambdaQuery(FileType.class)
				.eq(FileType::getTypeName, biz);
		FileType fileType = fileTypeService.getOne(eq);
		ThrowUtils.throwIf(fileType == null, ErrorCode.PARAMS_ERROR, "文件上传有误");
		// 校验文件类型
		fileLogService.validFile(multipartFile, fileType);
		// 生成 UUID 作为文件夹名称
		String uniqueDir = MD5Utils.encrypt(fileType.getTypeName());
		// 获取当前登录用户信息
		User loginUser = userService.getLoginUser(request);
		// 文件目录：根据业务、用户来划分
		String path = String.format("/%s/%s", uniqueDir, loginUser.getId());
		LambdaQueryWrapper<FileLog> fileLogLambdaQueryWrapper = Wrappers.lambdaQuery(FileLog.class)
				.eq(FileLog::getUserId, loginUser.getId())
				.eq(FileLog::getFileTypeId, fileType.getId());
		FileLog oldFileLog = fileLogService.getOne(fileLogLambdaQueryWrapper);
		// 如果改文件已经存在则替换该文件
		String fileUrl = MinioUtils.uploadFile(multipartFile, path);
		// 如果文件不存在，则直接插入新记录
		FileLog fileLog = new FileLog();
		fileLog.setFileTypeId(fileType.getId());
		fileLog.setFileName(multipartFile.getOriginalFilename());
		fileLog.setFilePath(fileUrl);
		fileLog.setUserId(loginUser.getId());
		if (oldFileLog != null) {
			fileLog.setId(oldFileLog.getId());
		}
		boolean save = fileLogService.saveOrUpdate(fileLog);
		ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "文件保存失败");
		// 返回可访问地址
		return ResultUtils.success(fileUrl);
		
	}
	
	/**
	 * 删除文件上传日志表
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteFileLog(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		FileLog oldFileLog = fileLogService.getById(id);
		ThrowUtils.throwIf(oldFileLog == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldFileLog.getUserId().equals(user.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = fileLogService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	
	/**
	 * 根据 id 获取文件上传日志表（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<FileLogVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<FileLogVO> getFileLogVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		FileLog fileLog = fileLogService.getById(id);
		ThrowUtils.throwIf(fileLog == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(fileLogService.getFileLogVO(fileLog, request));
	}
	
	/**
	 * 分页获取文件上传日志表列表
	 *
	 * @param fileLogQueryRequest fileLogQueryRequest
	 * @return {@link BaseResponse<Page<FileLog>>}
	 */
	@PostMapping("/list/page")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Page<FileLog>> listFileLogByPage(@RequestBody FileLogQueryRequest fileLogQueryRequest) {
		long current = fileLogQueryRequest.getCurrent();
		long size = fileLogQueryRequest.getPageSize();
		// 查询数据库
		Page<FileLog> fileLogPage = fileLogService.page(new Page<>(current, size),
				fileLogService.getQueryWrapper(fileLogQueryRequest));
		return ResultUtils.success(fileLogPage);
	}
	
	/**
	 * 分页获取文件上传日志表列表（封装类）
	 *
	 * @param fileLogQueryRequest fileLogQueryRequest
	 * @param request request
	 * @return {@link BaseResponse<Page<FileLogVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<FileLogVO>> listFileLogVOByPage(@RequestBody FileLogQueryRequest fileLogQueryRequest,
	                                                         HttpServletRequest request) {
		long current = fileLogQueryRequest.getCurrent();
		long size = fileLogQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<FileLog> fileLogPage = fileLogService.page(new Page<>(current, size),
				fileLogService.getQueryWrapper(fileLogQueryRequest));
		// 获取封装类
		return ResultUtils.success(fileLogService.getFileLogVOPage(fileLogPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的文件上传日志表列表
	 *
	 * @param fileLogQueryRequest fileLogQueryRequest
	 * @param request             request
	 * @return {@link BaseResponse<Page<FileLogVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<FileLogVO>> listMyFileLogVOByPage(@RequestBody FileLogQueryRequest fileLogQueryRequest,
	                                                           HttpServletRequest request) {
		ThrowUtils.throwIf(fileLogQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		fileLogQueryRequest.setUserId(loginUser.getId());
		long current = fileLogQueryRequest.getCurrent();
		long size = fileLogQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<FileLog> fileLogPage = fileLogService.page(new Page<>(current, size),
				fileLogService.getQueryWrapper(fileLogQueryRequest));
		// 获取封装类
		return ResultUtils.success(fileLogService.getFileLogVOPage(fileLogPage, request));
	}
	
	// endregion
}
