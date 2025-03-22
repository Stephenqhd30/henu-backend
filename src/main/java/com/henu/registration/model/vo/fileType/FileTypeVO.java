package com.henu.registration.model.vo.fileType;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.henu.registration.model.entity.FileType;
import com.henu.registration.model.vo.admin.AdminVO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文件上传类型视图
 *
 * @author stephen
 */
@Data
public class FileTypeVO implements Serializable {
	
	private static final long serialVersionUID = -6462552847172165110L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 文件上传类型名称
	 */
	private String typeName;
	
	/**
	 * 文件上传类型值(JSON如['jpg','png'])
	 */
	private List<String> typeValues;
	
	/**
	 * 最大可上传文件大小（字节）
	 */
	private Long maxFileSize;
	
	/**
	 * 管理员id
	 */
	private Long adminId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 封装类转对象
	 *
	 * @param fileTypeVO fileTypeVO
	 * @return {@link FileType}
	 */
	public static FileType voToObj(FileTypeVO fileTypeVO) {
		if (fileTypeVO == null) {
			return null;
		}
		FileType fileType = new FileType();
		BeanUtils.copyProperties(fileTypeVO, fileType);
		List<String> typeValues = fileTypeVO.getTypeValues();
		if (CollUtil.isNotEmpty(typeValues)) {
			fileType.setTypeValues(JSONUtil.toJsonStr(typeValues));
		}
		return fileType;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param fileType fileType
	 * @return {@link FileTypeVO}
	 */
	public static FileTypeVO objToVo(FileType fileType) {
		if (fileType == null) {
			return null;
		}
		FileTypeVO fileTypeVO = new FileTypeVO();
		BeanUtils.copyProperties(fileType, fileTypeVO);
		if (StringUtils.isNotBlank(fileType.getTypeValues())) {
			fileTypeVO.setTypeValues(JSONUtil.toList(fileType.getTypeValues(), String.class));
		}
		return fileTypeVO;
	}
}
