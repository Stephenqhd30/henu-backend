package com.henu.registration.model.vo.fileLog;

import com.henu.registration.model.entity.FileLog;
import com.henu.registration.model.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件上传日志表视图
 *
 * @author stephen
 */
@Data
public class FileLogVO implements Serializable {
	
	private static final long serialVersionUID = -5502227404138966496L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 附件类型编号
	 */
	private String fileType;
	
	/**
	 * 附件名称
	 */
	private String fileName;
	
	/**
	 * 附件存储路径
	 */
	private String filePath;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 用户
	 */
	private UserVO userVO;
	
	
	/**
	 * 封装类转对象
	 *
	 * @param fileLogVO fileLogVO
	 * @return {@link FileLog}
	 */
	public static FileLog voToObj(FileLogVO fileLogVO) {
		if (fileLogVO == null) {
			return null;
		}
		FileLog fileLog = new FileLog();
		BeanUtils.copyProperties(fileLogVO, fileLog);
		return fileLog;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param fileLog fileLog
	 * @return {@link FileLogVO}
	 */
	public static FileLogVO objToVo(FileLog fileLog) {
		if (fileLog == null) {
			return null;
		}
		FileLogVO fileLogVO = new FileLogVO();
		BeanUtils.copyProperties(fileLog, fileLogVO);
		return fileLogVO;
	}
}
