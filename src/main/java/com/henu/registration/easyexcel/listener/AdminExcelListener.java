package com.henu.registration.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.easyexcel.core.impl.DefaultExcelListener;
import com.henu.registration.easyexcel.modal.admin.AdminExcelDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * 管理员导入监听器
 * 继承 DefaultExcelListener 以复用解析逻辑
 *
 * @author stephenqiu
 */
@Slf4j
public class AdminExcelListener extends DefaultExcelListener<AdminExcelDTO> {
	
	/**
	 * 记录已出现的管理员编号，避免重复导入
	 */
	private final Set<String> adminNumberSet = new HashSet<>();
	
	/**
	 * 解析数据时的操作
	 */
	@Override
	public void invoke(AdminExcelDTO data, AnalysisContext context) {
		// 校验管理员编号、姓名、密码是否为空
		if (data.getAdminNumber() == null || data.getAdminNumber().trim().isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "管理员编号不能为空！");
		}
		if (data.getAdminName() == null || data.getAdminName().trim().isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "管理员姓名不能为空！");
		}
		if (data.getAdminPassword() == null || data.getAdminPassword().trim().isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "管理员密码不能为空！");
		}
		
		// 避免重复导入
		if (adminNumberSet.contains(data.getAdminNumber())) {
			log.warn("管理员编号重复：{}", data.getAdminNumber());
			return;
		}
		adminNumberSet.add(data.getAdminNumber());
		
		// 调用父类方法存储数据
		super.invoke(data, context);
	}
	
	/**
	 * 处理异常
	 */
	@Override
	public void onException(Exception exception, AnalysisContext context) {
		log.error("管理员数据解析异常：{}", exception.getMessage());
		super.onException(exception, context);
	}
}