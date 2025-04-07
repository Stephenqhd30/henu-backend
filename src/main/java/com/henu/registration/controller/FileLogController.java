package com.henu.registration.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.fileLog.DownloadFileRequest;
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
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
	 * 文件下载（将用户所有上传文件打包为 ZIP，通过 MinIO 获取）
	 *
	 * @param downloadFileRequest 请求体，包含 userId
	 * @param response            HttpServletResponse
	 */
	@PostMapping("/download")
	public void downloadFile(@RequestBody DownloadFileRequest downloadFileRequest, HttpServletResponse response) {
		// 1. 参数校验
		if (downloadFileRequest == null || downloadFileRequest.getUserId() == null || downloadFileRequest.getUserId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
		}
		Long userId = downloadFileRequest.getUserId();
		// 2. 获取用户信息
		User user = userService.getById(userId);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
		
		// 3. 获取用户的文件上传记录
		List<FileLog> fileLogList = fileLogService.list(Wrappers.lambdaQuery(FileLog.class)
				.eq(FileLog::getUserId, userId));
		ThrowUtils.throwIf(fileLogList == null || fileLogList.isEmpty(), ErrorCode.NOT_FOUND_ERROR);
		// 4. 设置响应头（ZIP 文件下载）
		response.setContentType("application/zip");
		String zipFileName = URLEncoder.encode(user.getUserName() + ".zip", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFileName);
		// 5. 创建 Zip 输出流并写入文件
		try (OutputStream outputStream = response.getOutputStream();
		     ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
			for (FileLog fileLog : fileLogList) {
				Long fileTypeId = fileLog.getFileTypeId();
				FileType fileType = fileTypeService.getById(fileTypeId);
				String fileName = user.getUserName() + "-" + fileType.getTypeName();
				fileName += fileLog.getFileName().substring(fileLog.getFileName().lastIndexOf("."));
				String filePath = fileLog.getFilePath();
				// 从 MinIO 获取文件流
				try (InputStream fileInputStream = MinioUtils.getFileStream(filePath)) {
					if (fileInputStream == null) {
						continue;
					}
					// 创建 ZIP 条目并写入文件内容
					zipOutputStream.putNextEntry(new ZipEntry(fileName));
					byte[] buffer = new byte[4096];
					int length;
					while ((length = fileInputStream.read(buffer)) > 0) {
						zipOutputStream.write(buffer, 0, length);
					}
					zipOutputStream.closeEntry();
				} catch (IOException e) {
					// 单个文件下载失败，不中断整体流程
					log.warn("下载文件失败: {}", filePath, e);
				}
			}
			zipOutputStream.finish();
			outputStream.flush();
		} catch (IOException e) {
			log.error("压缩文件输出失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件下载失败");
		}
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
