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
import com.henu.registration.easyexcel.modal.messageNotice.MessageNoticeExcelVO;
import com.henu.registration.easyexcel.modal.messagePush.MessagePushExcelVO;
import com.henu.registration.easyexcel.modal.operationLog.OperationLogExcelVO;
import com.henu.registration.easyexcel.modal.registrationForm.RegistrationFormExcelVO;
import com.henu.registration.easyexcel.modal.reviewLog.ReviewLogExcelVO;
import com.henu.registration.easyexcel.modal.school.SchoolExcelVO;
import com.henu.registration.easyexcel.modal.schoolSchoolType.SchoolSchoolTypeExcelVO;
import com.henu.registration.easyexcel.modal.schoolType.SchoolTypeExcelVO;
import com.henu.registration.easyexcel.modal.systemMessages.SystemMessagesExcelVO;
import com.henu.registration.easyexcel.modal.user.UserExcelVO;
import com.henu.registration.easyexcel.service.ExcelService;
import com.henu.registration.model.entity.*;
import com.henu.registration.model.enums.*;
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
import java.util.Date;
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
	private RegistrationFormService registrationFormService;
	
	@Resource
	private ReviewLogService reviewLogService;
	
	@Resource
	private SchoolSchoolTypeService schoolSchoolTypeService;
	
	@Resource
	private SchoolTypeService schoolTypeService;
	
	@Resource
	private MessageNoticeService messageNoticeService;
	
	@Resource
	private MessagePushService messagePushService;
	
	@Resource
	private SystemMessagesService systemMessagesService;
	
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
	
	/**
	 * 导出报名登记表信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportRegistrationForm(HttpServletResponse response) throws IOException {
		List<CompletableFuture<RegistrationFormExcelVO>> futures = registrationFormService.list().stream().map(registrationForm -> CompletableFuture.supplyAsync(() -> {
			RegistrationFormExcelVO registrationFormExcelVO = new RegistrationFormExcelVO();
			BeanUtils.copyProperties(registrationForm, registrationFormExcelVO);
			registrationFormExcelVO.setUserGender(Objects.requireNonNull(UserGenderEnum.getEnumByValue(registrationForm.getUserGender())).getText());
			registrationFormExcelVO.setMarryStatus(Objects.requireNonNull(MarryStatueEnum.getEnumByValue(registrationForm.getMarryStatus())).getText());
			User user = userService.getById(registrationForm.getUserId());
			registrationFormExcelVO.setUserIdCard(userService.getDecryptIdCard(user.getUserIdCard()));
			registrationFormExcelVO.setSubmitter(user.getUserName());
			registrationFormExcelVO.setUserCertificate(user.getUserAvatar());
			Job job = jobService.getById(registrationForm.getJobId());
			registrationFormExcelVO.setJobName(job.getJobName());
			return registrationFormExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<RegistrationFormExcelVO> jobExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(jobExcelVOList, ExcelConstant.REGISTRATION_FROM, RegistrationFormExcelVO.class, response);
			log.info("报名登记表信息导出成功，导出数量：{}", jobExcelVOList.size());
		} catch (Exception e) {
			log.error("报名登记表信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "报名登记表信息导出失败");
		}
		
	}
	
	/**
	 * 导出审核日志信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportReviewLog(HttpServletResponse response) throws IOException {
		List<CompletableFuture<ReviewLogExcelVO>> futures = reviewLogService.list().stream().map(reviewLog -> CompletableFuture.supplyAsync(() -> {
			ReviewLogExcelVO reviewLogExcelVO = new ReviewLogExcelVO();
			BeanUtils.copyProperties(reviewLog, reviewLogExcelVO);
			reviewLogExcelVO.setReviewStatus(Objects.requireNonNull(ReviewStatusEnum.getEnumByValue(reviewLog.getReviewStatus())).getText());
			Admin admin = adminService.getById(reviewLog.getReviewerId());
			reviewLogExcelVO.setReviewerName(admin.getAdminName());
			return reviewLogExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<ReviewLogExcelVO> reviewLogExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(reviewLogExcelVOList, ExcelConstant.REVIEW_LOG, ReviewLogExcelVO.class, response);
			log.info("审核日志信息导出成功，导出数量：{}", reviewLogExcelVOList.size());
		} catch (Exception e) {
			log.error("审核日志信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "审核日志信息导出失败");
		}
	}
	
	/**
	 * 导出学校信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportSchool(HttpServletResponse response) throws IOException {
		List<CompletableFuture<SchoolExcelVO>> futures = schoolService.list().stream().map(school -> CompletableFuture.supplyAsync(() -> {
			SchoolExcelVO schoolExcelVO = new SchoolExcelVO();
			BeanUtils.copyProperties(school, schoolExcelVO);
			return schoolExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<SchoolExcelVO> schoolExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(schoolExcelVOList, ExcelConstant.SCHOOL, SchoolExcelVO.class, response);
			log.info("学校信息导出成功，导出数量：{}", schoolExcelVOList.size());
		} catch (Exception e) {
			log.error("学校信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "学校信息导出失败");
		}
	}
	
	/**
	 * 导出高校与高校类型关联信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportSchoolSchoolType(HttpServletResponse response) throws IOException {
		List<CompletableFuture<SchoolSchoolTypeExcelVO>> futures = schoolSchoolTypeService.list().stream().map(schoolSchoolType -> CompletableFuture.supplyAsync(() -> {
			SchoolSchoolTypeExcelVO schoolSchoolTypeExcelVO = new SchoolSchoolTypeExcelVO();
			BeanUtils.copyProperties(schoolSchoolType, schoolSchoolTypeExcelVO);
			School school = schoolService.getById(schoolSchoolType.getSchoolId());
			schoolSchoolTypeExcelVO.setSchoolName(school.getSchoolName());
			return schoolSchoolTypeExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<SchoolSchoolTypeExcelVO> schoolSchoolTypeExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(schoolSchoolTypeExcelVOList, ExcelConstant.SCHOOL_SCHOOL_TYPE, SchoolSchoolTypeExcelVO.class, response);
			log.info("高校与高校类型关联信息导出成功，导出数量：{}", schoolSchoolTypeExcelVOList.size());
		} catch (Exception e) {
			log.error("高校与高校类型关联信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "高校与高校类型关联信息导出失败");
		}
	}
	
	/**
	 * 导出高校类型信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportSchoolType(HttpServletResponse response) throws IOException {
		List<CompletableFuture<SchoolTypeExcelVO>> futures = schoolTypeService.list().stream().map(schoolType -> CompletableFuture.supplyAsync(() -> {
			SchoolTypeExcelVO schoolTypeExcelVO = new SchoolTypeExcelVO();
			BeanUtils.copyProperties(schoolType, schoolTypeExcelVO);
			return schoolTypeExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<SchoolTypeExcelVO> schoolSchoolTypeExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(schoolSchoolTypeExcelVOList, ExcelConstant.SCHOOL_TYPE, SchoolTypeExcelVO.class, response);
			log.info("高校类型信息导出成功，导出数量：{}", schoolSchoolTypeExcelVOList.size());
		} catch (Exception e) {
			log.error("高校类型信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "高校类型信息导出失败");
		}
	}
	
	/**
	 * 导出面试消息通知到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportMessageNotice(HttpServletResponse response) throws IOException {
		List<CompletableFuture<MessageNoticeExcelVO>> futures = messageNoticeService.list().stream().map(messageNotice -> CompletableFuture.supplyAsync(() -> {
			MessageNoticeExcelVO messageNoticeExcelVO = new MessageNoticeExcelVO();
			BeanUtils.copyProperties(messageNotice, messageNoticeExcelVO);
			Long registrationId = messageNotice.getRegistrationId();
			RegistrationForm registrationForm = registrationFormService.getById(registrationId);
			messageNoticeExcelVO.setUserName(registrationForm.getUserName());
			messageNoticeExcelVO.setUserPhone(registrationForm.getUserPhone());
			return messageNoticeExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<MessageNoticeExcelVO> schoolSchoolTypeExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(schoolSchoolTypeExcelVOList, ExcelConstant.MESSAGE_NOTICE, MessageNoticeExcelVO.class, response);
			log.info("面试消息通知导出成功，导出数量：{}", schoolSchoolTypeExcelVOList.size());
		} catch (Exception e) {
			log.error("面试消息通知导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "面试消息通知导出失败");
		}
	}
	
	/**
	 * 导出消息推送信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportMessagePush(HttpServletResponse response) throws IOException {
		List<CompletableFuture<MessagePushExcelVO>> futures = messagePushService.list().stream().map(messagePush -> CompletableFuture.supplyAsync(() -> {
			MessagePushExcelVO messagePushExcelVO = new MessagePushExcelVO();
			BeanUtils.copyProperties(messagePush, messagePushExcelVO);
			Long userId = messagePush.getUserId();
			String pushType = messagePush.getPushType();
			Integer pushStatus = messagePush.getPushStatus();
			User user = userService.getById(userId);
			messagePushExcelVO.setUserName(user.getUserName());
			messagePushExcelVO.setPushType(Objects.requireNonNull(PushTyprEnum.getEnumByValue(pushType)).getText());
			messagePushExcelVO.setPushStatus(Objects.requireNonNull(PushStatusEnum.getEnumByValue(pushStatus)).getText());
			return messagePushExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<MessagePushExcelVO> schoolSchoolTypeExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(schoolSchoolTypeExcelVOList, ExcelConstant.MESSAGE_PUSH, MessagePushExcelVO.class, response);
			log.info("消息推送信息导出成功，导出数量：{}", schoolSchoolTypeExcelVOList.size());
		} catch (Exception e) {
			log.error("消息推送信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "消息推送信息导出失败");
		}
	}
	
	/**
	 * 导出系统消息信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	@Override
	public void exportSystemMessages(HttpServletResponse response) throws IOException {
		List<CompletableFuture<SystemMessagesExcelVO>> futures = systemMessagesService.list().stream().map(systemMessages -> CompletableFuture.supplyAsync(() -> {
			SystemMessagesExcelVO systemMessagesExcelVO = new SystemMessagesExcelVO();
			BeanUtils.copyProperties(systemMessages, systemMessagesExcelVO);
			Integer pushStatus = systemMessages.getPushStatus();
			String type = systemMessages.getType();
			systemMessagesExcelVO.setType(Objects.requireNonNull(SystemMessageTypeEnum.getEnumByValue(type)).getText());
			systemMessagesExcelVO.setPushStatus(Objects.requireNonNull(PushStatusEnum.getEnumByValue(pushStatus)).getText());
			return systemMessagesExcelVO;
		}, threadPoolExecutor)).toList();
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<SystemMessagesExcelVO> schoolSchoolTypeExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 写入 Excel 文件
		try {
			ExcelUtils.exportHttpServletResponse(schoolSchoolTypeExcelVOList, ExcelConstant.SYSTEM_MESSAGE, SystemMessagesExcelVO.class, response);
			log.info("系统消息信息导出成功，导出数量：{}", schoolSchoolTypeExcelVOList.size());
		} catch (Exception e) {
			log.error("系统消息信息导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "系统消息信息导出失败");
		}
	}
}