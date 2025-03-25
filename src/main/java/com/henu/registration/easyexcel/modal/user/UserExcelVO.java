package com.henu.registration.easyexcel.modal.user;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 *
 * @author stephen qiu
 */
@Data
public class UserExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 7145437502939204157L;
	/**
	 * id
	 */
	@ColumnWidth(40)
	@ExcelProperty("id")
	private Long id;
	
	/**
	 * 姓名
	 */
	@ColumnWidth(40)
	@ExcelProperty("姓名")
	private String userName;
	
	/**
	 * 身份证号码
	 */
	@ColumnWidth(40)
	@ExcelProperty("身份证号码")
	private String userIdCard;
	
	/**
	 * 邮箱地址
	 */
	@ColumnWidth(40)
	@ExcelProperty("邮箱地址")
	private String userEmail;
	
	/**
	 * 联系电话
	 */
	@ColumnWidth(40)
	@ExcelProperty("联系电话")
	private String userPhone;
	
	/**
	 * 性别(0-男,1-女)
	 */
	@ColumnWidth(40)
	@ExcelProperty("性别")
	private String userGender;
	
	/**
	 * 用户头像
	 */
	@ColumnWidth(40)
	@ExcelProperty("用户头像")
	private String userAvatar;
	
}