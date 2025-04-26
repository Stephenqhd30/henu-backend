package com.henu.registration.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.easyexcel.core.impl.DefaultExcelListener;
import com.henu.registration.easyexcel.modal.fileType.FileTypeExcelDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * 文件上传类型导入监听器
 * 适用于批量导入文件上传数据
 * 继承 DefaultExcelListener 以复用已有的解析逻辑
 *
 * @author stephenqiu
 */
@Slf4j
public class FileTypeExcelListener extends DefaultExcelListener<FileTypeExcelDTO> {
	
	/**
	 * 记录已出现的文件上传名称，避免重复导入
	 */
	private final Set<String> schoolNameSet = new HashSet<>();
	
	/**
	 * 当读取到表格数据时的操作
	 */
	@Override
	public void invoke(FileTypeExcelDTO data, AnalysisContext context) {
		// 校验文件上传名称是否为空
		if (data.getTypeName() == null || data.getTypeName().trim().isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件上传名称不能为空！");
		}
		// 校验文件上传类别列表是否为空
		if (data.getTypeValues() == null || data.getTypeValues().isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件上传类别不能为空！");
		}
		schoolNameSet.add(data.getTypeName());
		super.invoke(data, context);
	}
	
	/**
	 * 处理异常
	 */
	@Override
	public void onException(Exception exception, AnalysisContext context) {
		log.error("文件上传数据解析异常", exception);
		super.onException(exception, context);
	}
	
	/**
	 * 解析完成后执行
	 */
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		log.info("文件上传类型数据解析完成，共解析 {} 条数据", schoolNameSet.size());
	}
}