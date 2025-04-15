package com.henu.registration.easyexcel.modal.registrationForm;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 报名登记Excel视图
 *
 * @author stephen
 */
@Data
public class RegistrationFormExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -4562133149585501968L;
	
	/**
	 * 岗位名
	 */
	@ColumnWidth(20)
	@ExcelProperty("岗位名")
	private String jobName;
	
	/**
	 * 姓名
	 */
	@ColumnWidth(20)
	@ExcelProperty("姓名")
	private String userName;
	
	/**
	 * 身份证号码
	 */
	@ColumnWidth(40)
	@ExcelProperty("身份证号码")
	private String userIdCard;
	
	/**
	 * 性别(0-男,1-女)
	 */
	@ColumnWidth(20)
	@ExcelProperty("性别")
	private String userGender;
	
	/**
	 * 民族
	 */
	@ColumnWidth(20)
	@ExcelProperty("民族")
	private String ethnic;
	
	/**
	 * 政治面貌
	 */
	@ColumnWidth(20)
	@ExcelProperty("政治面貌")
	private String politicalStatus;
	
	/**
	 * 入党时间
	 */
	@ColumnWidth(20)
	@ExcelProperty("入党时间")
	private String partyTime;
	
	/**
	 * 出生日期
	 */
	@ColumnWidth(20)
	@ExcelProperty("出生日期")
	private String birthDate;
	
	/**
	 * 婚姻状况(0-未婚，1-已婚)
	 */
	@ColumnWidth(20)
	@ExcelProperty("婚姻状况")
	private String marryStatus;
	
	/**
	 * 家庭住址
	 */
	@ColumnWidth(40)
	@ExcelProperty("家庭住址")
	private String address;
	
	/**
	 * 工作经历
	 */
	@ColumnWidth(40)
	@ExcelProperty("工作经历")
	private String workExperience;
	
	/**
	 * 主要学生干部经历
	 */
	@ColumnWidth(40)
	@ExcelProperty("主要学生干部经历")
	private String studentLeaders;
	
	/**
	 * 干部经历描述
	 */
	@ColumnWidth(40)
	@ExcelProperty("干部经历描述")
	private String leaderExperience;
	
	/**
	 * 主要获奖情况
	 */
	@ColumnWidth(40)
	@ExcelProperty("主要获奖情况")
	private String studentAwards;
	
	/**
	 * 提交人
	 */
	@ColumnWidth(20)
	@ExcelProperty("提交人")
	private String submitter;
	
	/**
	 * 报名状态(0-待报名,1-已报名,2-待面试,3-已发送面试通知)
	 */
	@ColumnWidth(20)
	@ExcelProperty("报名状态")
	private String registrationStatus;
	
	/**
	 * 本科教育阶段
	 */
	@ColumnWidth(20)
	@ExcelProperty("本科教育阶段")
	private String undergraduateEducationalStage;
	
	/**
	 * 本科高校名称
	 */
	@ColumnWidth(40)
	@ExcelProperty("本科高校名称")
	private String undergraduateSchoolName;
	
	/**
	 * 本科专业
	 */
	@ColumnWidth(20)
	@ExcelProperty("本科专业")
	private String undergraduateMajor;
	
	/**
	 * 本科学习起止年月
	 */
	@ColumnWidth(40)
	@ExcelProperty("本科学习起止年月")
	private String undergraduateStudyTime;
	
	/**
	 * 本科证明人
	 */
	@ColumnWidth(20)
	@ExcelProperty("本科证明人")
	private String undergraduateCertifier;
	
	/**
	 * 本科证明人联系电话
	 */
	@ColumnWidth(20)
	@ExcelProperty("本科证明人联系电话")
	private String undergraduateCertifierPhone;
	
	/**
	 * 硕士教育阶段
	 */
	@ColumnWidth(20)
	@ExcelProperty("硕士教育阶段")
	private String postgraduateEducationalStage;
	
	/**
	 * 硕士高校名称
	 */
	@ColumnWidth(40)
	@ExcelProperty("硕士高校名称")
	private String postgraduateSchoolName;
	
	/**
	 * 硕士专业
	 */
	@ColumnWidth(20)
	@ExcelProperty("硕士专业")
	private String postgraduateMajor;
	
	/**
	 * 硕士学习起止年月
	 */
	@ColumnWidth(40)
	@ExcelProperty("硕士学习起止年月")
	private String postgraduateStudyTime;
	
	/**
	 * 硕士证明人
	 */
	@ColumnWidth(20)
	@ExcelProperty("硕士证明人")
	private String postgraduateCertifier;
	
	/**
	 * 硕士证明人联系电话
	 */
	@ColumnWidth(20)
	@ExcelProperty("硕士证明人联系电话")
	private String postgraduateCertifierPhone;
	
	/**
	 * 博士教育阶段
	 */
	@ColumnWidth(20)
	@ExcelProperty("博士教育阶段")
	private String doctorEducationalStage;
	
	/**
	 * 博士高校名称
	 */
	@ColumnWidth(40)
	@ExcelProperty("博士高校名称")
	private String doctorSchoolName;
	
	/**
	 * 博士专业
	 */
	@ColumnWidth(20)
	@ExcelProperty("博士专业")
	private String doctorMajor;
	
	/**
	 * 博士学习起止年月
	 */
	@ColumnWidth(40)
	@ExcelProperty("博士学习起止年月")
	private String doctorStudyTime;
	
	/**
	 * 博士证明人
	 */
	@ColumnWidth(20)
	@ExcelProperty("博士证明人")
	private String doctorCertifier;
	
	/**
	 * 博士证明人联系电话
	 */
	@ColumnWidth(20)
	@ExcelProperty("博士证明人联系电话")
	private String doctorCertifierPhone;
}