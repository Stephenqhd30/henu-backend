package com.henu.registration.easyexcel.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.easyexcel.service.ExcelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
	 * 导出用户信息到 Excel
	 */
	@GetMapping("/export/admin")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public void exportAdmin(HttpServletResponse response) throws IOException {
		excelService.exportAdmin(response);
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
}