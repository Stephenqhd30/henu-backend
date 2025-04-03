package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.FileLogMapper;
import com.henu.registration.model.dto.fileLog.FileLogQueryRequest;
import com.henu.registration.model.entity.*;
import com.henu.registration.model.vo.fileLog.FileLogVO;
import com.henu.registration.model.vo.fileType.FileTypeVO;
import com.henu.registration.model.vo.user.UserVO;
import com.henu.registration.service.FileLogService;
import com.henu.registration.service.FileTypeService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.sql.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 文件上传日志表服务实现
 *
 * @author stephenqiu
 * @description 针对表【file_log(文件上传日志表)】的数据库操作Service实现
 * @createDate 2025-03-22 00:41:15
 */
@Service
public class FileLogServiceImpl extends ServiceImpl<FileLogMapper, FileLog>
		implements FileLogService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private FileTypeService fileTypeService;
	/**
	 * 校验文件
	 *
	 * @param multipartFile     multipartFile
	 * @param fileType 文件上传类型
	 */
	@Override
	public void validFile(MultipartFile multipartFile, FileType fileType) {
		// 文件大小
		long fileSize = multipartFile.getSize();
		// 文件后缀
		String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
		String typeValues = fileType.getTypeValues();
		Long maxFileSize = fileType.getMaxFileSize();
		List<String> allowedSuffixes = JSONUtil.toList(typeValues, String.class);
		// 文件大小校验
		if (fileSize > maxFileSize) {
			throw new BusinessException(ErrorCode.PARAMS_SIZE_ERROR,
					String.format("文件大小不能超过 %.2fMB", maxFileSize / (1024.0 * 1024)));
		}
		// 文件格式校验
		if (!allowedSuffixes.contains(fileSuffix)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误，仅支持：" + allowedSuffixes);
		}
	}
	
	
	/**
	 * 获取查询条件
	 *
	 * @param fileLogQueryRequest fileLogQueryRequest
	 * @return {@link QueryWrapper<FileLog>}
	 */
	@Override
	public QueryWrapper<FileLog> getQueryWrapper(FileLogQueryRequest fileLogQueryRequest) {
		QueryWrapper<FileLog> queryWrapper = new QueryWrapper<>();
		if (fileLogQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = fileLogQueryRequest.getId();
		Long notId = fileLogQueryRequest.getNotId();
		String searchText = fileLogQueryRequest.getSearchText();
		Long fileTypeId = fileLogQueryRequest.getFileTypeId();
		String fileName = fileLogQueryRequest.getFileName();
		String filePath = fileLogQueryRequest.getFilePath();
		Long userId = fileLogQueryRequest.getUserId();
		String sortField = fileLogQueryRequest.getSortField();
		String sortOrder = fileLogQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 从多字段中搜索
		if (StringUtils.isNotBlank(searchText)) {
			// 需要拼接查询条件
			queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
		}
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(fileName), "file_name", fileName);
		queryWrapper.like(StringUtils.isNotBlank(filePath), "file_path", filePath);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(fileTypeId), "file_type_id", fileTypeId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取文件上传日志表封装
	 *
	 * @param fileLog fileLog
	 * @param request request
	 * @return {@link FileLogVO}
	 */
	@Override
	public FileLogVO getFileLogVO(FileLog fileLog, HttpServletRequest request) {
		// 对象转封装类
		FileLogVO fileLogVO = FileLogVO.objToVo(fileLog);
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = fileLog.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		fileLogVO.setUserVO(userVO);
		// 2. 关联查询文件类型信息
		Long fileTypeId = fileLog.getFileTypeId();
		FileType fileType = null;
		if (fileTypeId != null && fileTypeId > 0) {
			fileType = fileTypeService.getById(fileTypeId);
		}
		FileTypeVO fileTypeVO = fileTypeService.getFileTypeVO(fileType, request);
		fileLogVO.setFileTypeVO(fileTypeVO);
		// endregion
		return fileLogVO;
	}
	
	/**
	 * 分页获取文件上传日志表封装
	 *
	 * @param fileLogPage fileLogPage
	 * @param request     request
	 * @return {@link Page<FileLogVO>}
	 */
	@Override
	public Page<FileLogVO> getFileLogVOPage(Page<FileLog> fileLogPage, HttpServletRequest request) {
		List<FileLog> fileLogList = fileLogPage.getRecords();
		Page<FileLogVO> fileLogVOPage = new Page<>(fileLogPage.getCurrent(), fileLogPage.getSize(), fileLogPage.getTotal());
		if (CollUtil.isEmpty(fileLogList)) {
			return fileLogVOPage;
		}
		// 对象列表 => 封装对象列表
		List<FileLogVO> fileLogVOList = fileLogList.stream()
				.map(FileLogVO::objToVo)
				.collect(Collectors.toList());
		// 1. 关联查询用户信息
		Set<Long> userIdSet = fileLogList.stream().map(FileLog::getUserId).collect(Collectors.toSet());
		// 填充信息
		if (CollUtil.isNotEmpty(userIdSet)) {
			CompletableFuture<Map<Long, List<User>>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> userService.listByIds(userIdSet).stream()
					.collect(Collectors.groupingBy(User::getId)));
			try {
				Map<Long, List<User>> userIdUserListMap = mapCompletableFuture.get();
				// 填充信息
				fileLogVOList.forEach(fileLogVO -> {
					Long userId = fileLogVO.getUserId();
					User user = null;
					if (userIdUserListMap.containsKey(userId)) {
						user = userIdUserListMap.get(userId).get(0);
					}
					fileLogVO.setUserVO(userService.getUserVO(user, request));
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
			}
		}
		// 2. 关联查询文件上传类型信息
		Set<Long> fileTypeIdSet = fileLogList.stream().map(FileLog::getFileTypeId).collect(Collectors.toSet());
		// 填充信息
		if (CollUtil.isNotEmpty(fileTypeIdSet)) {
			CompletableFuture<Map<Long, List<FileType>>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> fileTypeService.listByIds(fileTypeIdSet).stream()
					.collect(Collectors.groupingBy(FileType::getId)));
			try {
				Map<Long, List<FileType>> fileTypeIdListMap = mapCompletableFuture.get();
				// 填充信息
				fileLogVOList.forEach(fileLogVO -> {
					Long fileTypeId = fileLogVO.getFileTypeId();
					FileType fileType = null;
					if (fileTypeIdListMap.containsKey(fileTypeId)) {
						fileType = fileTypeIdListMap.get(fileTypeId).get(0);
					}
					fileLogVO.setFileTypeVO(fileTypeService.getFileTypeVO(fileType, request));
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
			}
		}
		// endregion
		fileLogVOPage.setRecords(fileLogVOList);
		return fileLogVOPage;
	}
}




