package com.henu.registration.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.easyexcel.core.impl.DefaultExcelListener;
import com.henu.registration.easyexcel.modal.admin.AdminExcelDTO;
import com.henu.registration.easyexcel.modal.messageNotice.MessageNoticeExcelDTO;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.service.RegistrationFormService;
import com.henu.registration.utils.regex.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 管理员导入监听器
 * 继承 DefaultExcelListener 以复用解析逻辑
 *
 * @author stephenqiu
 */
@Slf4j
public class MessageNoticeExcelListener extends DefaultExcelListener<MessageNoticeExcelDTO> {
	
	
	private final RegistrationFormService registrationFormService;
	/**
	 * 记录已出现的报名登记表id，避免重复导入
	 */
	private final Set<Long> registrationFormIdList = new HashSet<>();
	
	public MessageNoticeExcelListener(RegistrationFormService registrationFormService) {
		this.registrationFormService = registrationFormService;
	}
	
	/**
	 * 解析数据时的操作
	 */
	@Override
	public void invoke(MessageNoticeExcelDTO data, AnalysisContext context) {
		String userName = data.getUserName();
		String userPhone = data.getUserPhone();
		Date interviewTime = data.getInterviewTime();
		String interviewLocation = data.getInterviewLocation();
		
		ThrowUtils.throwIf(StringUtils.isBlank(userName), ErrorCode.PARAMS_ERROR, "用户姓名不能为空");
		ThrowUtils.throwIf(StringUtils.isBlank(userPhone), ErrorCode.PARAMS_ERROR, "用户手机号不能为空");
		if (StringUtils.isNotBlank(userPhone)) {
			ThrowUtils.throwIf(!RegexUtils.checkPhone(userPhone), ErrorCode.PARAMS_ERROR, "用户手机号格式不正确");
		}
		ThrowUtils.throwIf(interviewTime == null, ErrorCode.PARAMS_ERROR, "面试时间不能为空");
		ThrowUtils.throwIf(StringUtils.isBlank(interviewLocation), ErrorCode.PARAMS_ERROR, "面试地点不能为空");
		if (interviewTime.before(new Date())) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "面试时间不能早于当前时间");
		}
		
		RegistrationForm registrationForm = registrationFormService.getOne(Wrappers.lambdaQuery(RegistrationForm.class)
				.eq(RegistrationForm::getUserName, userName)
				.eq(RegistrationForm::getUserPhone, userPhone));
		ThrowUtils.throwIf(registrationForm == null, ErrorCode.PARAMS_ERROR, "该用户不存在");
		if (registrationFormIdList.contains(registrationForm.getId())) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "该用户已经导入过，请勿重复导入");
		}
		registrationFormIdList.add(registrationForm.getId());
		// 调用父类方法存储数据
		super.invoke(data, context);
	}
	
	/**
	 * 处理异常
	 */
	@Override
	public void onException(Exception exception, AnalysisContext context) {
		log.error("面试通知数据解析异常：{}", exception.getMessage());
		super.onException(exception, context);
	}
}