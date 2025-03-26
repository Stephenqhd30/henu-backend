package com.henu.registration.easyexcel.service.impl;

import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.easyexcel.core.ExcelResult;
import com.henu.registration.easyexcel.constants.ExcelConstant;
import com.henu.registration.easyexcel.listener.AdminExcelListener;
import com.henu.registration.easyexcel.modal.admin.AdminExcelDTO;
import com.henu.registration.easyexcel.modal.admin.AdminExcelVO;
import com.henu.registration.easyexcel.modal.cadreType.CadreTypeExcelVO;
import com.henu.registration.easyexcel.modal.deadline.DeadlineExcelVO;
import com.henu.registration.easyexcel.modal.education.EducationExcelVO;
import com.henu.registration.easyexcel.modal.family.FamilyExcelVO;
import com.henu.registration.easyexcel.modal.fileLog.FileLogExcelVO;
import com.henu.registration.easyexcel.modal.fileType.FileTypeExcelVO;
import com.henu.registration.easyexcel.modal.job.JobExcelVO;
import com.henu.registration.easyexcel.modal.operationLog.OperationLogExcelVO;
import com.henu.registration.easyexcel.modal.user.UserExcelVO;
import com.henu.registration.easyexcel.service.ExcelService;
import com.henu.registration.model.entity.FileType;
import com.henu.registration.model.entity.Job;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.enums.AdminTyprEnum;
import com.henu.registration.model.enums.UserGenderEnum;
import com.henu.registration.service.*;
import com.henu.registration.utils.excel.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 验证码服务实现类（基于 Redis 缓存）
 *
 * @author stephenqiu
 */
@Service
@Slf4j
public class ExcelServiceImpl implements ExcelService {
	
	@Resource
	private AdminService adminService;
	
	@Resource
	private OperationLogService operationLogService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private CadreTypeService cadreTypeService;
	
	@Resource
	private DeadlineService deadlineService;
	
	@Resource
	private JobService jobService;
	
	@Resource
	private EducationService educationService;
	
	@Resource
	private SchoolService schoolService;
	
	@Resource
	private FamilyService familyService;
	
	@Resource
	private FileLogService fileLogService;
	
	@Resource
	private FileTypeService fileTypeService;
	
	@Resource
	private ThreadPoolExecutor threadPoolExecutor;
	
	/**
	 * 验证 Excel 文件
	 *
	 * @param file file
	 */
	@Override
	public void validExcel(MultipartFile file) {
		// 获取文件名并检查是否为null
		String filename = file.getOriginalFilename();
		ThrowUtils.throwIf(filename == null, ErrorCode.PARAMS_ERROR, "文件名不能为空");
		
		// 检查文件格式是否为Excel格式
		if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "上传文件格式不正确");
		}
	}
	
	/**
	 * 处理 Excel 文件导入
	 *
	 * @param file file
	 * @return String
	 */
	@Override
	public ExcelResult<AdminExcelDTO> importAdmin(MultipartFile file) {
		try (InputStream inputStream = file.getInputStream()) {
			// 使用 AdminExcelListener 解析 Excel
			AdminExcelListener listener = new AdminExcelListener(adminService);
			ExcelResult<AdminExcelDTO> result = ExcelUtils.importStreamAndCloseWithListener(inputStream, AdminExcelDTO.class, listener);
			// 记录导入日志
			log.info("管理员信息导入完成，成功导入 {} 条，失败 {} 条", result.getList().size(), result.getErrorList().size());
			return result;
		} catch (IOException e) {
			log.error("管理员导入失败", e);
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "Excel 导入失败");
		}
	}
	
	/**
	 * 导出管理员信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportAdmin(HttpServletResponse response) throws IOException {
		List<CompletableFuture<AdminExcelVO>> futures = adminService.list().stream().map(admin -> CompletableFuture.supplyAsync(() -> {
			AdminExcelVO adminExcelVO = new AdminExcelVO();
			BeanUtils.copyProperties(admin, adminExcelVO);
			// 假设需要对某些字段进行解密或转换
			adminExcelVO.setAdminType(Objects.requireNonNull(AdminTyprEnum.getEnumByValue(admin.getAdminType())).getText());
			return adminExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<AdminExcelVO> adminExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(adminExcelVOList, ExcelConstant.ADMIN, AdminExcelVO.class, response);
			log.info("管理员信息导出成功，导出数量：{}", adminExcelVOList.size());
		} catch (Exception e) {
			log.error("管理员信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "管理员信息导出失败");
		}
		
	}
	
	/**
	 * 导出操作日志信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportOperationLog(HttpServletResponse response) throws IOException {
		List<CompletableFuture<OperationLogExcelVO>> futures = operationLogService.list().stream().map(operationLog -> CompletableFuture.supplyAsync(() -> {
			OperationLogExcelVO operationLogExcelVO = new OperationLogExcelVO();
			BeanUtils.copyProperties(operationLog, operationLogExcelVO);
			return operationLogExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<OperationLogExcelVO> operationLogExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(operationLogExcelVOList, ExcelConstant.OPERATION_LOG, OperationLogExcelVO.class, response);
			log.info("操作日志信息导出成功，导出数量：{}", operationLogExcelVOList.size());
		} catch (Exception e) {
			log.error("操作日志信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "操作日志信息导出失败");
		}
		
	}
	
	/**
	 * 导出用户信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportUser(HttpServletResponse response) throws IOException {
		List<CompletableFuture<UserExcelVO>> futures = userService.list().stream().map(user -> CompletableFuture.supplyAsync(() -> {
			UserExcelVO userExcelVO = new UserExcelVO();
			BeanUtils.copyProperties(user, userExcelVO);
			userExcelVO.setUserIdCard(userService.getDecryptIdCard(user.getUserIdCard()));
			userExcelVO.setUserGender(Objects.requireNonNull(UserGenderEnum.getEnumByValue(user.getUserGender())).getText());
			return userExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<UserExcelVO> userExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(userExcelVOList, ExcelConstant.USER, UserExcelVO.class, response);
			log.info("用户信息导出成功，导出数量：{}", userExcelVOList.size());
		} catch (Exception e) {
			log.error("用户信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "用户信息导出失败");
		}
		
	}
	
	/**
	 * 导出干部类型信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportCadreType(HttpServletResponse response) throws IOException {
		List<CompletableFuture<CadreTypeExcelVO>> futures = cadreTypeService.list().stream().map(cadreType -> CompletableFuture.supplyAsync(() -> {
			CadreTypeExcelVO cadreTypeExcelVO = new CadreTypeExcelVO();
			BeanUtils.copyProperties(cadreType, cadreTypeExcelVO);
			return cadreTypeExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<CadreTypeExcelVO> cadreTypeExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(cadreTypeExcelVOList, ExcelConstant.CADRE_TYPE, CadreTypeExcelVO.class, response);
			log.info("干部类型信息导出成功，导出数量：{}", cadreTypeExcelVOList.size());
		} catch (Exception e) {
			log.error("干部类型导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "干部类型导出失败");
		}
		
	}
	
	/**
	 * 导出截止时间信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportDeadline(HttpServletResponse response) throws IOException {
		List<CompletableFuture<DeadlineExcelVO>> futures = deadlineService.list().stream().map(deadline -> CompletableFuture.supplyAsync(() -> {
			DeadlineExcelVO deadlineExcelVO = new DeadlineExcelVO();
			BeanUtils.copyProperties(deadline, deadlineExcelVO);
			Job job = jobService.getById(deadline.getJobId());
			deadlineExcelVO.setJobName(job.getJobName());
			return deadlineExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<DeadlineExcelVO> deadlineExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(deadlineExcelVOList, ExcelConstant.DEADLINE, DeadlineExcelVO.class, response);
			log.info("截止时间信息导出成功，导出数量：{}", deadlineExcelVOList.size());
		} catch (Exception e) {
			log.error("截止时间信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "截止时间信息导出失败");
		}
		
	}
	
	/**
	 * 导出教育经历信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportEducation(HttpServletResponse response) throws IOException {
		List<CompletableFuture<EducationExcelVO>> futures = educationService.list().stream().map(education -> CompletableFuture.supplyAsync(() -> {
			EducationExcelVO educationExcelVO = new EducationExcelVO();
			BeanUtils.copyProperties(education, educationExcelVO);
			School school = schoolService.getById(education.getSchoolId());
			educationExcelVO.setSchoolName(school.getSchoolName());
			User user = userService.getById(education.getUserId());
			educationExcelVO.setUserName(user.getUserName());
			return educationExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<EducationExcelVO> deadlineExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(deadlineExcelVOList, ExcelConstant.EDUCATION, EducationExcelVO.class, response);
			log.info("教育经历信息导出成功，导出数量：{}", deadlineExcelVOList.size());
		} catch (Exception e) {
			log.error("教育经历信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "教育经历信息导出失败");
		}
		
	}
	
	/**
	 * 导出家庭关系信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportFamily(HttpServletResponse response) throws IOException {
		List<CompletableFuture<FamilyExcelVO>> futures = familyService.list().stream().map(family -> CompletableFuture.supplyAsync(() -> {
			FamilyExcelVO educationExcelVO = new FamilyExcelVO();
			BeanUtils.copyProperties(family, educationExcelVO);
			User user = userService.getById(family.getUserId());
			educationExcelVO.setUserName(user.getUserName());
			return educationExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<FamilyExcelVO> familyExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(familyExcelVOList, ExcelConstant.FAMILY, FamilyExcelVO.class, response);
			log.info("家庭关系信息导出成功，导出数量：{}", familyExcelVOList.size());
		} catch (Exception e) {
			log.error("家庭关系信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "家庭关系信息导出失败");
		}
		
	}
	
	/**
	 * 导出文件上传日志信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportFileLog(HttpServletResponse response) throws IOException {
		List<CompletableFuture<FileLogExcelVO>> futures = fileLogService.list().stream().map(fileLog -> CompletableFuture.supplyAsync(() -> {
			FileLogExcelVO fileLogExcelVO = new FileLogExcelVO();
			BeanUtils.copyProperties(fileLog, fileLogExcelVO);
			FileType fileType = fileTypeService.getById(fileLog.getFileTypeId());
			fileLogExcelVO.setFileTypeName(fileType.getTypeName());
			User user = userService.getById(fileLog.getUserId());
			fileLogExcelVO.setUserName(user.getUserName());
			return fileLogExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<FileLogExcelVO> fileLogExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(fileLogExcelVOList, ExcelConstant.FILE_LOG, FileLogExcelVO.class, response);
			log.info("文件上传日志信息导出成功，导出数量：{}", fileLogExcelVOList.size());
		} catch (Exception e) {
			log.error("文件上传日志信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "文件上传日志信息导出失败");
		}
		
	}
	
	/**
	 * 导出文件类型信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportFileType(HttpServletResponse response) throws IOException {
		List<CompletableFuture<FileTypeExcelVO>> futures = fileTypeService.list().stream().map(fileType -> CompletableFuture.supplyAsync(() -> {
			FileTypeExcelVO fileTypeExcelVO = new FileTypeExcelVO();
			BeanUtils.copyProperties(fileType, fileTypeExcelVO);
			return fileTypeExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<FileTypeExcelVO> fileTypeExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(fileTypeExcelVOList, ExcelConstant.FILE_TYPE, FileTypeExcelVO.class, response);
			log.info("文件类型信息导出成功，导出数量：{}", fileTypeExcelVOList.size());
		} catch (Exception e) {
			log.error("文件类型信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "文件类型信息导出失败");
		}
		
	}
	
	/**
	 * 导出岗位信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportJob(HttpServletResponse response) throws IOException {
		List<CompletableFuture<JobExcelVO>> futures = jobService.list().stream().map(job -> CompletableFuture.supplyAsync(() -> {
			JobExcelVO jobExcelVO = new JobExcelVO();
			BeanUtils.copyProperties(job, jobExcelVO);
			return jobExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<JobExcelVO> jobExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(jobExcelVOList, ExcelConstant.JOB, JobExcelVO.class, response);
			log.info("岗位信息导出成功，导出数量：{}", jobExcelVOList.size());
		} catch (Exception e) {
			log.error("岗位信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "岗位信息导出失败");
		}
		
	}
}