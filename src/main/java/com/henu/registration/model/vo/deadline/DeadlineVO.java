package com.henu.registration.model.vo.deadline;

import com.henu.registration.model.entity.Deadline;
import com.henu.registration.model.vo.admin.AdminVO;
import com.henu.registration.model.vo.job.JobVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 截止时间视图
 *
 * @author stephenqiu
 */
@Data
public class DeadlineVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 7816078208695245470L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 截止日期
	 */
	private Date deadlineTime;
	
	/**
	 * 岗位信息id
	 */
	private Long jobId;
	
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
	 * 岗位信息
	 */
	private JobVO jobVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param deadlineVO deadlineVO
	 * @return {@link Deadline}
	 */
	public static Deadline voToObj(DeadlineVO deadlineVO) {
		if (deadlineVO == null) {
			return null;
		}
		Deadline deadline = new Deadline();
		BeanUtils.copyProperties(deadlineVO, deadline);
		return deadline;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param deadline deadline
	 * @return {@link DeadlineVO}
	 */
	public static DeadlineVO objToVo(Deadline deadline) {
		if (deadline == null) {
			return null;
		}
		DeadlineVO deadlineVO = new DeadlineVO();
		BeanUtils.copyProperties(deadline, deadlineVO);
		return deadlineVO;
	}
}
