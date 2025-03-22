package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.FileLogMapper;
import com.henu.registration.model.dto.fileLog.FileLogQueryRequest;
import com.henu.registration.model.entity.FileLog;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.enums.FileUploadBizEnum;
import com.henu.registration.model.vo.fileLog.FileLogVO;
import com.henu.registration.model.vo.user.UserVO;
import com.henu.registration.service.FileLogService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.sql.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
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
	/**
	 * 校验文件
	 *
	 * @param multipartFile     multipartFile
	 * @param fileUploadBizEnum 业务类型
	 */
	@Override
	public void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
		// 文件大小
		long fileSize = multipartFile.getSize();
		// 文件后缀
		String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
		if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
			long FIVE_M = 5 * 1024 * 1024L;
			if (fileSize > FIVE_M) {
				throw new BusinessException(ErrorCode.PARAMS_SIZE_ERROR, "文件大小不能超过 5M");
			}
			if (!Arrays.asList("jpeg", "jpg", "png", "webp").contains(fileSuffix)) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
			}
		} else {
			// 除用户头像外，其他业务类型只支持 PDF 格式
			if (!"pdf".equals(fileSuffix)) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误，仅支持 PDF 格式");
			}
			// 限制为最大10MB
			long TEN_M = 10 * 1024 * 1024L;
			if (fileSize > TEN_M) {
				throw new BusinessException(ErrorCode.PARAMS_SIZE_ERROR, "文件大小不能超过 10M");
			}
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
		String fileType = fileLogQueryRequest.getFileType();
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
		queryWrapper.eq(StringUtils.isNotBlank(fileType), "file_type", fileType);
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
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		fileLogVOPage.setRecords(fileLogVOList);
		return fileLogVOPage;
	}
}




