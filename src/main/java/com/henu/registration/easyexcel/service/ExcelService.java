package com.henu.registration.easyexcel.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码服务接口
 *
 * @author stephenqiu
 */
public interface ExcelService {
	
	/**
	 * 验证 Excel 文件
	 *
	 * @param file file
	 */
	void validExcel(MultipartFile file);
	
	/**
	 * 处理 Excel 文件导入
	 *
	 * @param file file
	 * @return String
	 */
	String importAdmin(MultipartFile file);
	
	/**
	 * 导出管理员信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportAdmin(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出操作日志信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportOperationLog(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出用户信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportUser(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出干部类型信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportCadreType(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出截止时间信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportDeadline(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出教育经历信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportEducation(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出家庭关系信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportFamily(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出文件上传日志信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportFileLog(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出文件类型信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportFileType(HttpServletResponse response) throws IOException;
	
	
	/**
	 * 导出岗位信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportJob(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出报名登记表信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportRegistrationForm(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出审核日志信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportReviewLog(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出学校信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportSchool(HttpServletResponse response) throws IOException;
	
	/**
	 * 导入高校与高校类型关联信息
	 *
	 * @param file file
	 * @return String
	 */
	String importSchoolSchoolType(MultipartFile file, HttpServletRequest request);
	
	/**
	 * 导出高校与高校类型关联信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportSchoolSchoolType(HttpServletResponse response) throws IOException;
	
	/**
	 * 导入高校类型信息
	 *
	 * @param file file
	 * @return String
	 */
	String importSchoolType(MultipartFile file, HttpServletRequest request);
	
	/**
	 * 导出高校类型信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportSchoolType(HttpServletResponse response) throws IOException;
	
	/**
	 * 导入面试消息通知信息
	 *
	 * @param file    file
	 * @param request request
	 * @return String
	 */
	String importMessageNotice(MultipartFile file, HttpServletRequest request);
	
	/**
	 * 导出面试消息通知到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportMessageNotice(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出消息推送信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportMessagePush(HttpServletResponse response) throws IOException;
	
	/**
	 * 导出系统消息信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	void exportSystemMessages(HttpServletResponse response) throws IOException;
	
	
	/**
	 * 导入学校信息到数据库
	 *
	 * @param file
	 * @return
	 */
	String importSchool(MultipartFile file, HttpServletRequest request);
}