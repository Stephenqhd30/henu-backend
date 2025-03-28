package com.henu.registration.easyexcel.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.henu.registration.common.BaseResponse;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ResultUtils;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.easyexcel.constants.ExcelConstant;
import com.henu.registration.easyexcel.service.ExcelService;
import com.henu.registration.utils.excel.ExcelUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Excel控制器
 *
 * @author stephenqiu
 */
@RestController()
@RequestMapping("/excel")
public class ExcelController {
	
	@Resource
	private ExcelService excelService;
	
	/**
	 * 从 Excel 中导入高校信息
	 *
	 * @param file file
	 * @return BaseResponse<String>
	 */
	@PostMapping("/import/admin")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<String> importAdminExcel(@RequestParam("file") MultipartFile file) {
		ThrowUtils.throwIf(file.isEmpty(), ErrorCode.EXCEL_ERROR, "文件为空");
		try {
			// 校验 Excel 文件格式
			excelService.validExcel(file);
			// 导入高校信息
			String result = excelService.importAdmin(file);
			return ResultUtils.success(result);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "导入失败：" + e.getMessage());
		}
	}
	
	/**
	 * 导出用户信息到 Excel
	 */
	@GetMapping("/export/admin")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportAdmin(HttpServletResponse response) throws IOException {
		excelService.exportAdmin(response);
	}
	
	/**
	 * 导出导入用户信息模版
	 */
	@GetMapping("/export/template/admin")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportAdminTemplate(HttpServletResponse response) {
		ExcelUtils.exportTemplateHttpServletResponse(ExcelConstant.ADMIN, response);
	}
	
	/**
	 * 导出操作日志信息到 Excel
	 */
	@GetMapping("/export/operation/log")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportOperationLog(HttpServletResponse response) throws IOException {
		excelService.exportOperationLog(response);
	}
	
	/**
	 * 导出用户信息到 Excel
	 */
	@GetMapping("/export/user")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportUser(HttpServletResponse response) throws IOException {
		excelService.exportUser(response);
	}
	
	/**
	 * 导出干部类型到 Excel
	 */
	@GetMapping("/export/cadre/type")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportCadreType(HttpServletResponse response) throws IOException {
		excelService.exportCadreType(response);
	}
	
	/**
	 * 导出截止时间到 Excel
	 */
	@GetMapping("/export/deadline")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportDeadline(HttpServletResponse response) throws IOException {
		excelService.exportDeadline(response);
	}
	
	/**
	 * 导出教育经历表到 Excel
	 */
	@GetMapping("/export/education")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportEducation(HttpServletResponse response) throws IOException {
		excelService.exportEducation(response);
	}
	
	/**
	 * 导出家庭关系信息到 Excel
	 */
	@GetMapping("/export/family")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportFamily(HttpServletResponse response) throws IOException {
		excelService.exportFamily(response);
	}
	
	/**
	 * 导出文件上传日志信息到 Excel
	 */
	@GetMapping("/export/file/log")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportFileLog(HttpServletResponse response) throws IOException {
		excelService.exportFileLog(response);
	}
	
	/**
	 * 导出文件类型信息到 Excel
	 */
	@GetMapping("/export/file/type")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportFileType(HttpServletResponse response) throws IOException {
		excelService.exportFileType(response);
	}
	
	/**
	 * 导出文件类型信息到 Excel
	 */
	@GetMapping("/export/job")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportJob(HttpServletResponse response) throws IOException {
		excelService.exportJob(response);
	}
	
	/**
	 * 导出报名登记表信息到 Excel
	 */
	@GetMapping("/export/registration/form")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportRegistrationForm(HttpServletResponse response) throws IOException {
		excelService.exportRegistrationForm(response);
	}
	
	/**
	 * 导出审核日志信息到 Excel
	 */
	@GetMapping("/export/review/log")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportReviewLog(HttpServletResponse response) throws IOException {
		excelService.exportReviewLog(response);
	}
	
	/**
	 * 从 Excel 中导入学校信息
	 *
	 * @param file file
	 * @return BaseResponse<String>
	 */
	@PostMapping("/import/school")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<String> importSchoolExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		ThrowUtils.throwIf(file.isEmpty(), ErrorCode.EXCEL_ERROR, "文件为空");
		try {
			// 校验 Excel 文件格式
			excelService.validExcel(file);
			// 导入高校信息
			String result = excelService.importSchool(file, request);
			return ResultUtils.success(result);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "导入失败：" + e.getMessage());
		}
	}
	
	/**
	 * 导出学校信息到 Excel
	 */
	@GetMapping("/export/school")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportSchool(HttpServletResponse response) throws IOException {
		excelService.exportSchool(response);
	}
	
	/**
	 * 导出学校信息信息模版
	 */
	@GetMapping("/export/template/school")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportSchoolTemplate(HttpServletResponse response) {
		ExcelUtils.exportTemplateHttpServletResponse(ExcelConstant.SCHOOL, response);
	}
	
	/**
	 * 从 Excel 中导入高校与高校类型关联信息
	 *
	 * @param file file
	 * @return BaseResponse<String>
	 */
	@PostMapping("/import/school/school/type")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<String> importSchoolSchoolTypeExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		ThrowUtils.throwIf(file.isEmpty(), ErrorCode.EXCEL_ERROR, "文件为空");
		try {
			// 校验 Excel 文件格式
			excelService.validExcel(file);
			// 导入高校类型信息
			String result = excelService.importSchoolSchoolType(file, request);
			return ResultUtils.success(result);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "导入失败：" + e.getMessage());
		}
	}
	
	/**
	 * 导出高校与高校类型关联信息到 Excel
	 */
	@GetMapping("/export/school/school/type")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportSchoolSchoolType(HttpServletResponse response) throws IOException {
		excelService.exportSchoolSchoolType(response);
	}
	
	/**
	 * 导出高校与高校类型关联信息模版
	 */
	@GetMapping("/export/template/school/school/type")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportSchoolSchoolTypeTemplate(HttpServletResponse response) {
		ExcelUtils.exportTemplateHttpServletResponse(ExcelConstant.SCHOOL_SCHOOL_TYPE, response);
	}
	
	
	/**
	 * 从 Excel 中导入高校类型信息
	 *
	 * @param file file
	 * @return BaseResponse<String>
	 */
	@PostMapping("/import/school/type")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<String> importSchoolTypeExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		ThrowUtils.throwIf(file.isEmpty(), ErrorCode.EXCEL_ERROR, "文件为空");
		try {
			// 校验 Excel 文件格式
			excelService.validExcel(file);
			// 导入高校类型信息
			String result = excelService.importSchoolType(file, request);
			return ResultUtils.success(result);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "导入失败：" + e.getMessage());
		}
	}
	
	/**
	 * 导出高校类型信息到 Excel
	 */
	@GetMapping("/export/school/type")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportSchoolType(HttpServletResponse response) throws IOException {
		excelService.exportSchoolType(response);
	}
	
	/**
	 * 导出高校类型信息模版
	 */
	@GetMapping("/export/template/school/type")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportSchoolTypeTemplate(HttpServletResponse response) {
		ExcelUtils.exportTemplateHttpServletResponse(ExcelConstant.SCHOOL_TYPE, response);
	}
	
	/**
	 * 导出面试消息通知到 Excel
	 */
	@GetMapping("/export/message/notice")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportMessageNotice(HttpServletResponse response) throws IOException {
		excelService.exportMessageNotice(response);
	}
	
	/**
	 * 导出消息推送信息到 Excel
	 */
	@GetMapping("/export/message/push")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportMessagePush(HttpServletResponse response) throws IOException {
		excelService.exportMessagePush(response);
		
	}
	
	/**
	 * 导出系统消息信息到 Excel
	 */
	@GetMapping("/export/system/message")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportSystemMessages(HttpServletResponse response) throws IOException {
		excelService.exportSystemMessages(response);
		
	}
}