package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.FileTypeMapper;
import com.henu.registration.model.dto.fileType.FileTypeQueryRequest;
import com.henu.registration.model.entity.FileType;
import com.henu.registration.model.vo.fileType.FileTypeVO;
import com.henu.registration.service.FileTypeService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传类型服务实现
 *
 * @author stephenqiu
 * @description 针对表【file_type(文件上传类型表)】的数据库操作Service实现
 * @createDate 2025-03-22 14:17:12
 */
@Service
@Slf4j
public class FileTypeServiceImpl extends ServiceImpl<FileTypeMapper, FileType> implements FileTypeService {
	
	/**
	 * 校验数据
	 *
	 * @param fileType fileType
	 * @param add      对创建的数据进行校验
	 */
	@Override
	public void validFileType(FileType fileType, boolean add) {
		ThrowUtils.throwIf(fileType == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String typeName = fileType.getTypeName();
		String typeValue = fileType.getTypeValues();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(typeName), ErrorCode.PARAMS_ERROR, "类型名称不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(typeValue), ErrorCode.PARAMS_ERROR, "类型值不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(typeName)) {
			ThrowUtils.throwIf(typeName.length() > 80, ErrorCode.PARAMS_ERROR, "类型名称过长");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param fileTypeQueryRequest fileTypeQueryRequest
	 * @return {@link QueryWrapper<FileType>}
	 */
	@Override
	public QueryWrapper<FileType> getQueryWrapper(FileTypeQueryRequest fileTypeQueryRequest) {
		QueryWrapper<FileType> queryWrapper = new QueryWrapper<>();
		if (fileTypeQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = fileTypeQueryRequest.getId();
		Long notId = fileTypeQueryRequest.getNotId();
		String typeName = fileTypeQueryRequest.getTypeName();
		List<String> typeValues = fileTypeQueryRequest.getTypeValues();
		Long adminId = fileTypeQueryRequest.getAdminId();
		String sortField = fileTypeQueryRequest.getSortField();
		String sortOrder = fileTypeQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 遍历查询
		if (CollUtil.isNotEmpty(typeValues)) {
			for (String typeValue : typeValues) {
				queryWrapper.like("type_values", "\"" + typeValue + "\"");
			}
		}
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(typeName), "type_name", typeName);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(adminId), "admin_id", adminId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取文件上传类型封装
	 *
	 * @param fileType fileType
	 * @param request  request
	 * @return {@link FileTypeVO}
	 */
	@Override
	public FileTypeVO getFileTypeVO(FileType fileType, HttpServletRequest request) {
		// 对象转封装类
		return FileTypeVO.objToVo(fileType);
	}
	
	/**
	 * 分页获取文件上传类型封装
	 *
	 * @param fileTypePage fileTypePage
	 * @param request      request
	 * @return {@link Page<FileTypeVO>}
	 */
	@Override
	public Page<FileTypeVO> getFileTypeVOPage(Page<FileType> fileTypePage, HttpServletRequest request) {
		List<FileType> fileTypeList = fileTypePage.getRecords();
		Page<FileTypeVO> fileTypeVOPage = new Page<>(fileTypePage.getCurrent(), fileTypePage.getSize(), fileTypePage.getTotal());
		if (CollUtil.isEmpty(fileTypeList)) {
			return fileTypeVOPage;
		}
		// 对象列表 => 封装对象列表
		List<FileTypeVO> fileTypeVOList = fileTypeList.stream()
				.map(FileTypeVO::objToVo)
				.collect(Collectors.toList());
		fileTypeVOPage.setRecords(fileTypeVOList);
		return fileTypeVOPage;
	}
	
}
