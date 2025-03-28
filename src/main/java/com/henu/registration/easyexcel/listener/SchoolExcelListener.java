package com.henu.registration.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.config.easyexcel.core.impl.DefaultExcelListener;
import com.henu.registration.easyexcel.modal.school.SchoolExcelDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * 高校信息导入监听器
 * 适用于批量导入高校数据
 * 继承 DefaultExcelListener 以复用已有的解析逻辑
 *
 * @author stephenqiu
 */
@Slf4j
public class SchoolExcelListener extends DefaultExcelListener<SchoolExcelDTO> {
	
	/**
	 * 记录已出现的高校名称，避免重复导入
	 */
	private final Set<String> schoolNameSet = new HashSet<>();
	
	/**
	 * 当读取到表格数据时的操作
	 */
	@Override
	public void invoke(SchoolExcelDTO data, AnalysisContext context) {
		// 校验高校名称是否为空
		if (data.getSchoolName() == null || data.getSchoolName().trim().isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "高校名称不能为空！");
		}
		// 避免重复导入
		if (schoolNameSet.contains(data.getSchoolName())) {
			log.warn("高校名称重复：{}", data.getSchoolName());
			return;
		}
		schoolNameSet.add(data.getSchoolName());
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