package com.henu.registration.utils.encrypt;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.henu.registration.constants.SaltConstant;
import com.henu.registration.constants.UserConstant;

/**
 * @author stephen qiu
 */
public class EncryptionUtils {
	
	// AES 对象
	private static final AES AES;
	
	static {
		// 直接使用字符串作为密钥
		AES = SecureUtil.aes(SaltConstant.USER_ID_CARD_KEY.getBytes());
	}
	
	/**
	 * 加密方法
	 *
	 * @param data 待加密的数据
	 * @return 加密后的十六进制字符串
	 */
	public static String encrypt(String data) {
		return AES.encryptHex(data);
	}
	
	/**
	 * 解密方法
	 *
	 * @param encryptedData 加密后的十六进制字符串
	 * @return 解密后的原始数据
	 */
	public static String decrypt(String encryptedData) {
		return AES.decryptStr(encryptedData);
	}
}
