package com.henu.registration.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.easyexcel.core.impl.DefaultExcelListener;
import com.henu.registration.easyexcel.modal.schoolSchoolType.SchoolSchoolTypeExcelDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * 高校类型导入监听器
 * 适用于批量导入高校数据
 * 继承 DefaultExcelListener 以复用已有的解析逻辑
 *
 * @author stephenqiu
 */
@Slf4j
public class SchoolSchoolTypeExcelListener extends DefaultExcelListener<SchoolSchoolTypeExcelDTO> {
	
	/**
	 * 记录已出现的高校名称，避免重复导入
	 */
	private final Set<String> schoolNameSet = new HashSet<>();
	
	/**
	 * 当读取到表格数据时的操作
	 */
	@Override
	public void invoke(SchoolSchoolTypeExcelDTO data, AnalysisContext context) {
		// 校验高校名称是否为空
		if (data.getSchoolName() == null || data.getSchoolName().trim().isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "高校名称不能为空！");
		}
		// 校验高校类别列表是否为空
		if (data.getSchoolTypes() == null || data.getSchoolTypes().isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "高校类别不能为空！");
		}
		schoolNameSet.add(data.getSchoolName());
		super.invoke(data, context);
	}
	
	/**
	 * 处理异常
	 */
	@Override
	public void onException(Exception exception, AnalysisContext context) {
		log.error("高校数据解析异常", exception);
		super.onException(exception, context);
	}
	
	/**
	 * 解析完成后执行
	 */
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		log.info("高校类型数据解析完成，共解析 {} 条数据", schoolNameSet.size());
	}
}