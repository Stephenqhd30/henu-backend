package com.henu.registration.utils.sms;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.henu.registration.config.sms.properties.SmsProperties;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 短信发送工具类
 *
 * @author stephenqiu
 */
@Slf4j
@Component
public class SMSUtils {
	
	@Resource
	private IAcsClient smsClient;
	
	@Resource
	private SmsProperties smsProperties;
	
	/**
	 * 发送短信
	 *
	 * @param phoneNumbers 接收短信的手机号
	 * @param param        短信模板中的参数，JSON格式
	 */
	public void sendMessage(String phoneNumbers, String param) {
		SendSmsRequest request = new SendSmsRequest();
		request.setSysRegionId(smsProperties.getRegionId());
		request.setPhoneNumbers(phoneNumbers);
		request.setTemplateParam(param);
		
		try {
			SendSmsResponse response = smsClient.getAcsResponse(request);
			if (!"OK".equals(response.getCode())) {
				log.error("短信发送失败，错误码：{}，错误信息：{}", response.getCode(), response.getMessage());
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "短信发送失败：" + response.getMessage());
			}
			log.info("短信发送成功，手机号：{}", phoneNumbers);
		} catch (ClientException e) {
			log.error("短信发送异常：{}", e.getMessage(), e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "短信发送异常：" + e.getMessage());
		}
	}
}