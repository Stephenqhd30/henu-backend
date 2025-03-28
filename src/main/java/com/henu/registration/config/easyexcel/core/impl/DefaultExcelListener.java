package com.henu.registration.config.easyexcel.core.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.fastjson2.JSON;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.easyexcel.core.ExcelListener;
import com.henu.registration.config.easyexcel.core.ExcelResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Excel默认导入监听
 *
 * @param <T> 泛型T
 * @author stephenqiu
 */
@Slf4j
public class DefaultExcelListener<T> extends AnalysisEventListener<T> implements ExcelListener<T> {
	
	/**
	 * Excel表头数据
	 */
	private Map<Integer, String> headMap;
	
	/**
	 * 导入回执
	 */
	private final ExcelResult<T> excelResult = new DefautExcelResult<T>();
	
	/**
	 * 获取导入回执
	 */
	@Override
	public ExcelResult<T> getExcelResult() {
		return excelResult;
	}
	
	/**
	 * 当读取到表格数据时的操作
	 */
	@Override
	public void invoke(T data, AnalysisContext context) {
		excelResult.getList().add(data);
	}
	
	/**
	 * 当读取到表头时的操作
	 */
	@Override
	public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
		this.headMap = headMap;
		log.info("解析到表头数据：{}", JSON.toJSONString(headMap));
	}
	
	/**
	 * 当所有数据解析完的操作
	 */
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		log.info("所有数据解析完毕，一共{}条数据！", excelResult.getList().size());
	}
	
	/**
	 * 处理异常
	 */
	@Override
	public void onException(Exception exception, AnalysisContext context) {
		String errorMsg = null;
		// 抓到ExcelDataConvertException进行相关日志记录
		if (exception instanceof ExcelDataConvertException excelDataConvertException) {
			Integer rowIndex = excelDataConvertException.getRowIndex();
			Integer columnIndex = excelDataConvertException.getColumnIndex();
			errorMsg = String.format("第%d行-第%d列-表头[%s]：解析异常！", rowIndex + 1, columnIndex + 1, headMap.get(columnIndex));
			log.error(errorMsg);
		}
		excelResult.getErrorList().add(errorMsg);
		throw new BusinessException(ErrorCode.EXCEL_ERROR, errorMsg);
	}
}
