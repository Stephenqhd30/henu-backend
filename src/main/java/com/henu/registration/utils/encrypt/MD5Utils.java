package com.henu.registration.utils.encrypt;

import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 *
 * @author stephenqiu
 */
@Slf4j
public class MD5Utils {
	
	/**
	 * 摘要加密
	 *
	 * @param plainInputStream 明文输入流
	 */
	public static String encrypt(InputStream plainInputStream) {
		try {
			return encrypt(plainInputStream.readAllBytes(), null);
		} catch (IOException e) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取加密内容流数据失败");
		}
	}
	
	/**
	 * 摘要加密
	 *
	 * @param plainInputStream 明文字符串
	 * @param saltInputStream  盐字符串
	 */
	public static String encrypt(InputStream plainInputStream, InputStream saltInputStream) {
		try {
			return encrypt(plainInputStream.readAllBytes(), saltInputStream.readAllBytes());
		} catch (IOException e) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取加密内容流数据失败");
		}
	}
	
	/**
	 * 摘要加密
	 *
	 * @param plainStr 明文字符串
	 */
	public static String encrypt(String plainStr) {
		return encrypt(plainStr.getBytes(), null);
	}
	
	/**
	 * 摘要加密
	 *
	 * @param plainStr 明文字符串
	 * @param saltStr  盐字符串
	 */
	public static String encrypt(String plainStr, String saltStr) {
		return encrypt(plainStr.getBytes(), saltStr.getBytes());
	}
	
	/**
	 * 摘要加密
	 *
	 * @param plainByteArray 明文数组
	 */
	public static String encrypt(byte[] plainByteArray) {
		return encrypt(plainByteArray, null);
	}
	
	/**
	 * 摘要加密
	 *
	 * @param plainByteArray 明文数组
	 * @param saltByteArray  盐数组
	 */
	public static String encrypt(byte[] plainByteArray, byte[] saltByteArray) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(ArrayUtils.isEmpty(saltByteArray) ? plainByteArray : ArrayUtils.addAll(plainByteArray, saltByteArray));
			StringBuilder hexString = new StringBuilder();
			for (byte b : digest) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "摘要加密出错" + e.getMessage());
		}
	}
	
}
