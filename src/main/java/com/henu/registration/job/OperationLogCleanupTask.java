package com.henu.registration.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.henu.registration.model.entity.OperationLog;
import com.henu.registration.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 定时清理操作日志任务
 *
 * @author stephenqiu
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperationLogCleanupTask {
	
	private final OperationLogService operationLogService;
	
	/**
	 * 每天凌晨 3 点清理 7 天前的操作日志
	 */
	@Scheduled(cron = "0 0 3 * * ?")
	public void cleanOldOperationLogs() {
		log.info("开始执行操作日志清理任务...");
		
		// 计算 7 天前的时间
		Date thresholdDate = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7));
		// 删除 7 天前的日志记录
		boolean success = operationLogService.remove(
				new LambdaQueryWrapper<OperationLog>().lt(OperationLog::getCreateTime, thresholdDate)
		);
		if (success) {
			log.info("操作日志清理任务完成：已删除 7 天前的日志");
		} else {
			log.warn("操作日志清理任务未找到符合条件的日志");
		}
	}
}