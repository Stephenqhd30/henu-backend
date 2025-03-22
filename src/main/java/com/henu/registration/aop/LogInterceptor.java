package com.henu.registration.aop;

import com.henu.registration.model.entity.OperationLog;
import com.henu.registration.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 请求响应日志 AOP
 *
 * @author stephenqiu
 **/
@Aspect
@Component
@Slf4j
public class LogInterceptor {
	@Resource
	private OperationLogService operationLogService;
	/**
	 * 执行拦截
	 */
	@Around("execution(* com.henu.registration.controller.*.*(..))")
	public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
		// 计时
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		// 获取请求路径
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
		// 生成请求唯一 id
		String requestId = UUID.randomUUID().toString();
		String url = httpServletRequest.getRequestURI();
		String method = httpServletRequest.getMethod();
		// 获取请求参数
		Object[] args = point.getArgs();
		String reqParam = "[" + StringUtils.join(args, ", ") + "]";
		String userAgent = httpServletRequest.getHeader("User-Agent");
		String ip = httpServletRequest.getRemoteHost();
		// 输出请求日志
		log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
				httpServletRequest.getRemoteHost(), reqParam);
		// 执行原方法
		Object result = point.proceed();
		// 输出响应日志
		stopWatch.stop();
		long totalTimeMillis = stopWatch.getTotalTimeMillis();
		
		// 创建并保存操作日志对象
		OperationLog logEntry = new OperationLog();
		logEntry.setRequestId(requestId);
		logEntry.setRequestPath(url);
		logEntry.setRequestMethod(method);
		logEntry.setRequestIp(ip);
		logEntry.setRequestParams(reqParam);
		logEntry.setResponseTime(totalTimeMillis);
		logEntry.setUserAgent(userAgent);
		operationLogService.save(logEntry);
		
		log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
		return result;
	}
}

