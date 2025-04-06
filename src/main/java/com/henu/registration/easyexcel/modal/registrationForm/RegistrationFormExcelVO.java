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
	 * 证件照
	 */
	@ColumnWidth(40)
	@ExcelProperty("证件照")
	private String userAvatar;
	
	/**
	 * 生活照
	 */
	@ColumnWidth(40)
	@ExcelProperty("生活照")
	private String userLifePhoto;
	
	/**
	 * 报名登记表文件
	 */
	@ColumnWidth(40)
	@ExcelProperty("报名登记表文件")
	private String registrationForm;
	
	/**
	 * 民族
	 */
	@ColumnWidth(20)
	@ExcelProperty("民族")
	private String ethnic;
	
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
	 * 岗位名
	 */
	@ColumnWidth(20)
	@ExcelProperty("岗位名")
	private String jobName;
}