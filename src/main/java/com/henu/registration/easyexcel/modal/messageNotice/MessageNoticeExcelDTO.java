package com.henu.registration.easyexcel.modal.messageNotice;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.henu.registration.model.entity.Admin;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 管理员 Excel 封装类
 *
 * @author stephen
 */
@Data
public class MessageNoticeExcelDTO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -475210145722229525L;
	
	/**
	 * 用户姓名
	 */
	@ExcelProperty("用户姓名")
	private String userName;
	
	/**
	 * 联系电话
	 */
	@ExcelProperty("联系电话")
	private String userPhone;
	
	/**
	 * 面试内容
	 */
	@ExcelProperty("面试内容")
	private String content;
	
	/**
	 * 封装类转对象
	 *
	 * @param adminExcelDTO adminExcelDTO
	 * @return {@link Admin}
	 */
	public static Admin dtoToObj(MessageNoticeExcelDTO adminExcelDTO) {
		if (adminExcelDTO == null) {
			return null;
		}
		Admin admin = new Admin();
		BeanUtils.copyProperties(adminExcelDTO, admin);
		return admin;
	}
	
	
	
}
