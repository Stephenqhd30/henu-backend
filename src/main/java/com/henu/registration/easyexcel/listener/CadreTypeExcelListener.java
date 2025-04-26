package com.henu.registration.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.easyexcel.core.impl.DefaultExcelListener;
import com.henu.registration.easyexcel.modal.cadreType.CadreTypeExcelDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * 干部类型导入监听器
 * 适用于批量导入干部数据
 * 继承 DefaultExcelListener 以复用已有的解析逻辑
 *
 * @author stephenqiu
 */
@Slf4j
public class CadreTypeExcelListener extends DefaultExcelListener<CadreTypeExcelDTO> {
	
	/**
	 * 记录已出现的干部类别，避免重复导入
	 */
	private final Set<String> cadreTypeSet = new HashSet<>();
	
	/**
	 * 当读取到表格数据时的操作
	 */
	@Override
	public void invoke(CadreTypeExcelDTO data, AnalysisContext context) {
		// 校验干部名称是否为空
		if (data.getType() == null || data.getType().trim().isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "干部类别不能为空！");
		}
		// 避免重复导入
		if (cadreTypeSet.contains(data.getType())) {
			log.warn("干部类别重复：{}", data.getType());
			return;
		}
		cadreTypeSet.add(data.getType());
		super.invoke(data, context);
	}
	
	/**
	 * 处理异常
	 */
	@Override
	public void onException(Exception exception, AnalysisContext context) {
		log.error("干部数据解析异常：{}", exception.getMessage());
		super.onException(exception, context);
	}
}