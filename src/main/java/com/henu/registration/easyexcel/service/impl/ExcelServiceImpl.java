package com.henu.registration.easyexcel.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.easyexcel.core.ExcelResult;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.easyexcel.constants.ExcelConstant;
import com.henu.registration.easyexcel.listener.*;
import com.henu.registration.easyexcel.modal.admin.AdminExcelDTO;
import com.henu.registration.easyexcel.modal.admin.AdminExcelVO;
import com.henu.registration.easyexcel.modal.cadreType.CadreTypeExcelVO;
import com.henu.registration.easyexcel.modal.deadline.DeadlineExcelVO;
import com.henu.registration.easyexcel.modal.education.EducationExcelVO;
import com.henu.registration.easyexcel.modal.family.FamilyExcelVO;
import com.henu.registration.easyexcel.modal.fileLog.FileLogExcelVO;
import com.henu.registration.easyexcel.modal.fileType.FileTypeExcelVO;
import com.henu.registration.easyexcel.modal.job.JobExcelVO;
import com.henu.registration.easyexcel.modal.messageNotice.MessageNoticeExcelDTO;
import com.henu.registration.easyexcel.modal.messageNotice.MessageNoticeExcelVO;
import com.henu.registration.easyexcel.modal.messagePush.MessagePushExcelVO;
import com.henu.registration.easyexcel.modal.operationLog.OperationLogExcelVO;
import com.henu.registration.easyexcel.modal.registrationForm.RegistrationFormExcelVO;
import com.henu.registration.easyexcel.modal.reviewLog.ReviewLogExcelVO;
import com.henu.registration.easyexcel.modal.school.SchoolExcelDTO;
import com.henu.registration.easyexcel.modal.school.SchoolExcelVO;
import com.henu.registration.easyexcel.modal.schoolSchoolType.SchoolSchoolTypeExcelDTO;
import com.henu.registration.easyexcel.modal.schoolSchoolType.SchoolSchoolTypeExcelVO;
import com.henu.registration.easyexcel.modal.schoolType.SchoolTypeExcelDTO;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Excel服务实现类
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
	public String importAdmin(MultipartFile file) {
		try (InputStream inputStream = file.getInputStream()) {
			// 使用 Admin 解析 Excel
			AdminExcelListener listener = new AdminExcelListener();
			ExcelResult<AdminExcelDTO> excelResult = ExcelUtils.importStreamAndCloseWithListener(inputStream, AdminExcelDTO.class, listener);
			// 将 DTO 转换为实体对象
			List<Admin> adminList = excelResult.getList().stream()
					.map(adminExcelDTO -> {
						Admin admin = new Admin();
						BeanUtils.copyProperties(adminExcelDTO, admin);
						admin.setAdminPassword(adminService.getEncryptPassword(admin.getAdminPassword()));
						admin.setAdminType(AdminConstant.ADMIN);
						return admin;
					})
					.toList();
			// 存入数据库
			adminService.saveBatch(adminList);
			return "管理员信息导入成功，导入数量：" + adminList.size();
		} catch (IOException e) {
			log.error("导入管理员信息异常", e);
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "导入管理员信息异常");
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
		})).toList();
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
		})).toList();
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
		})).toList();
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
		})).toList();
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
		})).toList();
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
		})).toList();
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
		})).toList();
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
		})).toList();
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
		})).toList();
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
		})).toList();
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
			Job job = jobService.getById(registrationForm.getJobId());
			registrationFormExcelVO.setJobName(job.getJobName());
			registrationFormExcelVO.setRegistrationStatus(Objects.requireNonNull(RegistrationStatueEnum.getEnumByValue(registrationForm.getRegistrationStatus())).getText());
			registrationFormExcelVO.setPoliticalStatus(Objects.requireNonNull(PoliticalStatusEnum.getEnumByValue(registrationForm.getPoliticalStatus())).getText());
			return registrationFormExcelVO;
		})).toList();
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
	 * 导出报名登记表信息到 Excel
	 *
	 * @param response HttpServletResponse
	 */
	/**
	 * 导出报名登记表信息到 Excel
	 *
	 * @param userIds  userIds
	 * @param response response
	 */
	@Override
	public void exportRegistrationFormByUserId(List<Long> userIds, HttpServletResponse response) throws IOException {
		List<CompletableFuture<RegistrationFormExcelVO>> futures = registrationFormService
				.list(Wrappers.lambdaQuery(RegistrationForm.class).in(RegistrationForm::getUserId, userIds))
				.stream()
				.map(registrationForm -> CompletableFuture.supplyAsync(() -> {
					RegistrationFormExcelVO registrationFormExcelVO = new RegistrationFormExcelVO();
					BeanUtils.copyProperties(registrationForm, registrationFormExcelVO);
					registrationFormExcelVO.setUserGender(Objects.requireNonNull(UserGenderEnum.getEnumByValue(registrationForm.getUserGender())).getText());
					registrationFormExcelVO.setMarryStatus(Objects.requireNonNull(MarryStatueEnum.getEnumByValue(registrationForm.getMarryStatus())).getText());
					User user = userService.getById(registrationForm.getUserId());
					registrationFormExcelVO.setUserIdCard(userService.getDecryptIdCard(user.getUserIdCard()));
					registrationFormExcelVO.setSubmitter(user.getUserName());
					Job job = jobService.getById(registrationForm.getJobId());
					registrationFormExcelVO.setJobName(job.getJobName());
					registrationFormExcelVO.setRegistrationStatus(Objects.requireNonNull(RegistrationStatueEnum.getEnumByValue(registrationForm.getRegistrationStatus())).getText());
					registrationFormExcelVO.setPoliticalStatus(Objects.requireNonNull(PoliticalStatusEnum.getEnumByValue(registrationForm.getPoliticalStatus())).getText());
					return registrationFormExcelVO;
				})).toList();
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
			return reviewLogExcelVO;
		})).toList();
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
	 * 导入学校信息
	 *
	 * @param file file
	 * @return String
	 */
	@Override
	public String importSchool(MultipartFile file, HttpServletRequest request) {
		// 获取当前登录的admin
		Admin admin = adminService.getLoginAdmin(request);
		try (InputStream inputStream = file.getInputStream()) {
			// 使用 SchoolExcelListener 解析 Excel
			SchoolExcelListener listener = new SchoolExcelListener();
			ExcelResult<SchoolExcelDTO> excelResult = ExcelUtils.importStreamAndCloseWithListener(inputStream, SchoolExcelDTO.class, listener);
			// 将 DTO 转换为实体对象
			List<School> schoolList = excelResult.getList().stream()
					.map(schoolExcelDTO -> {
						School school = new School();
						BeanUtils.copyProperties(schoolExcelDTO, school);
						school.setAdminId(admin.getId());
						return school;
					})
					.toList();
			// 存入数据库
			schoolService.saveBatch(schoolList);
			return "高校信息导入成功，导入数量：" + schoolList.size();
		} catch (IOException e) {
			log.error("导入高校信息异常", e);
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "导入高校信息异常");
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
		})).toList();
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
	 * 导入高校与高校类型关联信息
	 *
	 * @param file file
	 * @return String
	 */
	@Override
	public String importSchoolSchoolType(MultipartFile file, HttpServletRequest request) {
		// 获取当前登录的admin
		Admin admin = adminService.getLoginAdmin(request);
		try (InputStream inputStream = file.getInputStream()) {
			SchoolSchoolTypeExcelListener listener = new SchoolSchoolTypeExcelListener();
			ExcelResult<SchoolSchoolTypeExcelDTO> excelResult =
					ExcelUtils.importStreamAndCloseWithListener(inputStream, SchoolSchoolTypeExcelDTO.class, listener);
			
			// 1. 批量查询所有学校，避免 N+1 查询
			List<School> schoolList = schoolService.list();
			Map<String, Long> schoolNameToIdMap = schoolList.stream()
					.collect(Collectors.toMap(School::getSchoolName, School::getId));
			
			// 2. 处理 Excel 数据
			List<SchoolSchoolType> schoolSchoolTypes = excelResult.getList().stream()
					.map(schoolSchoolTypeExcelDTO -> {
						// 获取学校 ID
						Long schoolId = schoolNameToIdMap.get(schoolSchoolTypeExcelDTO.getSchoolName());
						ThrowUtils.throwIf(schoolId == null, ErrorCode.PARAMS_ERROR, "高校不存在：" + schoolSchoolTypeExcelDTO.getSchoolName());
						// 获取高校类别列表的 JSON 字符串
						String schoolTypesJson = schoolSchoolTypeExcelDTO.getSchoolTypes().trim();
						// 校验 JSON 格式
						ThrowUtils.throwIf(!JSONUtil.isTypeJSON(schoolTypesJson), ErrorCode.PARAMS_ERROR, "高校类别格式错误：" + schoolTypesJson);
						SchoolSchoolType schoolSchoolType = new SchoolSchoolType();
						schoolSchoolType.setSchoolId(schoolId);
						schoolSchoolType.setSchoolTypes(schoolTypesJson);
						schoolSchoolType.setAdminId(admin.getId());
						return schoolSchoolType;
					})
					.collect(Collectors.toList());
			
			// 3. 批量保存到数据库
			schoolSchoolTypeService.saveBatch(schoolSchoolTypes);
			return "高校与高校类型关联信息导入成功，导入数量：" + schoolSchoolTypes.size();
		} catch (IOException e) {
			log.error("导入高校与高校类型关联信息异常", e);
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "导入高校与高校类型关联信息异常");
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
		})).toList();
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
	 * 导入高校类型信息
	 *
	 * @param file file
	 * @return String
	 */
	@Override
	public String importSchoolType(MultipartFile file, HttpServletRequest request) {
		// 获取当前登录的admin
		Admin admin = adminService.getLoginAdmin(request);
		try (InputStream inputStream = file.getInputStream()) {
			// 使用 SchoolExcelListener 解析 Excel
			SchoolTypeExcelListener listener = new SchoolTypeExcelListener();
			ExcelResult<SchoolTypeExcelDTO> excelResult = ExcelUtils.importStreamAndCloseWithListener(inputStream, SchoolTypeExcelDTO.class, listener);
			// 将 DTO 转换为实体对象
			List<SchoolType> schoolTypeList = excelResult.getList().stream()
					.map(schoolTypeExcelDTO -> {
						SchoolType schoolType = new SchoolType();
						BeanUtils.copyProperties(schoolTypeExcelDTO, schoolType);
						schoolType.setAdminId(admin.getId());
						return schoolType;
					})
					.toList();
			// 存入数据库
			schoolTypeService.saveBatch(schoolTypeList);
			return "高校类型信息导入成功，导入数量：" + schoolTypeList.size();
		} catch (IOException e) {
			log.error("导入高校类型信息异常", e);
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "导入高校类型信息异常");
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
		})).toList();
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
	 * 导入面试消息通知信息
	 *
	 * @param file file
	 * @return String
	 */
	@Override
	public String importMessageNotice(MultipartFile file, HttpServletRequest request) {
		// 获取当前登录的admin
		Admin admin = adminService.getLoginAdmin(request);
		try (InputStream inputStream = file.getInputStream()) {
			MessageNoticeExcelListener listener = new MessageNoticeExcelListener(registrationFormService);
			ExcelResult<MessageNoticeExcelDTO> excelResult = ExcelUtils.importStreamAndCloseWithListener(inputStream, MessageNoticeExcelDTO.class, listener);
			// 将 DTO 转换为实体对象
			List<MessageNotice> messageNoticeList = excelResult.getList().stream()
					.map(messageNoticeExcelDTO -> {
						MessageNotice messageNotice = new MessageNotice();
						BeanUtils.copyProperties(messageNoticeExcelDTO, messageNotice);
						LambdaQueryWrapper<RegistrationForm> eq = Wrappers.lambdaQuery(RegistrationForm.class)
								.eq(RegistrationForm::getUserName, messageNotice.getUserName())
								.eq(RegistrationForm::getUserPhone, messageNoticeExcelDTO.getUserPhone());
						RegistrationForm registrationForm = registrationFormService.getOne(eq);
						if (registrationForm == null) {
							log.error("导入面试消息通知信息异常，用户不存在，用户名：{}，手机号：{}", messageNotice.getUserName(), messageNoticeExcelDTO.getUserPhone());
							throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "报名登记表信息不存在");
						}
						messageNotice.setRegistrationId(registrationForm.getId());
						messageNotice.setAdminId(admin.getId());
						// 校验信息是否已经存在
						LambdaQueryWrapper<MessageNotice> messageNoticeLambdaQueryWrapper = Wrappers.lambdaQuery(MessageNotice.class)
								.eq(MessageNotice::getUserName, messageNotice.getUserName())
								.eq(MessageNotice::getRegistrationId, registrationForm.getId());
						MessageNotice oldMessageNotice = messageNoticeService.getOne(messageNoticeLambdaQueryWrapper);
						if (oldMessageNotice != null) {
							// 如果消息已经存在则更新消息
							messageNotice.setId(oldMessageNotice.getId());
						}
						return messageNotice;
					})
					.toList();
			// 存入数据库
			messageNoticeService.saveOrUpdateBatch(messageNoticeList);
			return "面试消息通知信息导入成功，导入数量：" + messageNoticeList.size();
		} catch (IOException e) {
			log.error("导入面试消息通知信息异常", e);
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "导入面试消息通知信息异常");
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
			messageNoticeExcelVO.setPushStatus(Objects.requireNonNull(PushStatusEnum.getEnumByValue(messageNotice.getPushStatus())).getText());
			return messageNoticeExcelVO;
		})).toList();
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
		})).toList();
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
		})).toList();
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