package com.henu.registration.easyexcel.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.easyexcel.core.ExcelListener;
import com.henu.registration.config.easyexcel.core.ExcelResult;
import com.henu.registration.config.easyexcel.core.impl.DefautExcelResult;
import com.henu.registration.easyexcel.constants.ExcelConstant;
import com.henu.registration.easyexcel.modal.admin.AdminExcelDTO;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.service.AdminService;
import lombok.extern.slf4j.Slf4j;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员 Excel 导入监听器
 *
 * @author stephen
 */
@Slf4j
public class AdminExcelListener extends DefautExcelResult<AdminExcelDTO> implements ExcelListener<AdminExcelDTO> {
	
	private final AdminService adminService;
	private final List<AdminExcelDTO> adminList = new ArrayList<>();
	private final ExcelResult<AdminExcelDTO> excelResult = new DefautExcelResult<>();
	
	public AdminExcelListener(AdminService adminService) {
		this.adminService = adminService;
	}
	
	@Override
	public void invoke(AdminExcelDTO admin, AnalysisContext context) {
		adminList.add(admin);
		excelResult.getList().add(admin);
		if (adminList.size() >= ExcelConstant.BATCH_COUNT) {
			saveData();
			adminList.clear();
		}
	}
	
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		if (!adminList.isEmpty()) {
			saveData();
		}
		log.info("所有管理员数据导入完成，共 {} 条数据", excelResult.getList().size());
	}
	
	@Override
	public void onException(Exception exception, AnalysisContext context) {
		if (exception instanceof ExcelDataConvertException e) {
			int rowIndex = e.getRowIndex() + 1;
			int columnIndex = e.getColumnIndex() + 1;
			String errorMsg = String.format("Excel解析错误：第%d行-第%d列数据格式有误", rowIndex, columnIndex);
			log.error(errorMsg);
			excelResult.getErrorList().add(errorMsg);
			throw new BusinessException(ErrorCode.EXCEL_ERROR, errorMsg);
		}
		throw new BusinessException(ErrorCode.EXCEL_ERROR, "Excel解析异常");
	}
	
	private void saveData() {
		log.info("批量保存 {} 条管理员数据", adminList.size());
		List<Admin> list = adminList.stream().map(AdminExcelDTO::dtoToObj).toList();
		adminService.saveBatch(list);
	}
	
	@Override
	public ExcelResult<AdminExcelDTO> getExcelResult() {
		return excelResult;
	}
}