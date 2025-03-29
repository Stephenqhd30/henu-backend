package com.henu.registration.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.easyexcel.core.impl.DefaultExcelListener;
import com.henu.registration.easyexcel.modal.school.SchoolExcelDTO;
import com.henu.registration.easyexcel.modal.schoolType.SchoolTypeExcelDTO;
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
public class SchoolTypeExcelListener extends DefaultExcelListener<SchoolTypeExcelDTO> {
	
	/**
	 * 记录已出现的高校类别，避免重复导入
	 */
	private final Set<String> schoolNameSet = new HashSet<>();
	
	/**
	 * 当读取到表格数据时的操作
	 */
	@Override
	public void invoke(SchoolTypeExcelDTO data, AnalysisContext context) {
		// 校验高校名称是否为空
		if (data.getTypeName() == null || data.getTypeName().trim().isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "高校类别不能为空！");
		}
		// 避免重复导入
		if (schoolNameSet.contains(data.getTypeName())) {
			log.warn("高校类别重复：{}", data.getTypeName());
			return;
		}
		schoolNameSet.add(data.getTypeName());
		super.invoke(data, context);
	}
	
	/**
	 * 处理异常
	 */
	@Override
	public void onException(Exception exception, AnalysisContext context) {
		log.error("高校数据解析异常：{}", exception.getMessage());
		super.onException(exception, context);
	}
}