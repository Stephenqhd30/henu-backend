package com.henu.registration.utils.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式校验工具类
 *
 * @author: stephenqiu
 * @create: 2024-08-03 10:15
 **/
public class RegexUtils {
	
	
	/**
	 * 验证Email
	 *
	 * @param email email地址，格式：zhangsan@sina.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkEmail(String email) {
		String regex = "^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$";
		return Pattern.matches(regex, email);
	}
	
	/**
	 * 验证身份证号码
	 * 不能以0开头
	 * 年份不能以17开头
	 * 月份不能为13
	 * 日期不能为32
	 * 不能以a结尾
	 *
	 * @param idCard 居民身份证号码18位，第一位不能为0，最后一位可能是数字或字母，中间16位为数字 \d同[0-9]
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkIdCard(String idCard) {
		String regex = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
		return Pattern.matches(regex, idCard);
	}
	
	/**
	 * 验证手机号码（支持国际格式）
	 *
	 * @param mobile 移动、联通、电信运营商的号码段
	 * 【移动（China Mobile）】
	 * 	•	134-139、147、150-152、157-159、182-184、187、188、195、197、198
	 * 	•	虚商段：165、1703、1705、1706
	 * 【联通（China Unicom）】
	 * 	•	130-132、145、155、156、166、175、176、185、186、196
	 * 	•	虚商段：1704、1707、1708、1709、171
	 * 【电信（China Telecom）】
	 * 	•	133、149、153、173、174、177、180、181、189、190、191、193、199
	 * 	•	虚商段：1700、1701、1702
	 * 【广电（China Broadcasting Network）】
	 * 	•	192（新号段，国家广播电视总局）
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkMobile(String mobile) {
		// 包含常见的中国电信、移动、联通和虚商号段
		String regex = "^(\\+?86)?1(3[0-9]|4[5-9]|5[0-35-9]|6[5-7]|7[0-8]|8[0-9]|9[0-9]|92)\\d{8}$";
		return Pattern.matches(regex, mobile);
	}
	
	/**
	 * 验证固定电话号码
	 *
	 * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
	 *              <p><b>国家（地区） 代码 ：</b>标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9 的一位或多位数字，
	 *              数字之后是空格分隔的国家（地区）代码。</p>
	 *              <p><b>区号（城市代码）：</b>这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
	 *              对不使用地区或城市代码的国家（地区），则省略该组件。</p>
	 *              <p><b>电话号码：</b>这包含从 0 到 9 的一个或多个数字 </p>
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkPhone(String phone) {
		String regex = "(\\+\\d+)?(\\d{3,4}-?)?\\d{7,8}$";
		return Pattern.matches(regex, phone);
	}
	
	/**
	 * 验证整数（正整数和负整数）
	 *
	 * @param digit 一位或多位0-9之间的整数
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkDigit(String digit) {
		String regex = "\\-?[1-9]\\d+";
		return Pattern.matches(regex, digit);
	}
	
	/**
	 * 验证整数和浮点数（正负整数和正负浮点数）
	 *
	 * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkDecimals(String decimals) {
		String regex = "\\-?[1-9]\\d+(\\.\\d+)?";
		return Pattern.matches(regex, decimals);
	}
	
	/**
	 * 验证空白字符
	 *
	 * @param blankSpace 空白字符，包括：空格、\t、\n、\r、\f、\x0B
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkBlankSpace(String blankSpace) {
		String regex = "\\s+";
		return Pattern.matches(regex, blankSpace);
	}
	
	/**
	 * 验证中文
	 *
	 * @param chinese 中文字符
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkChinese(String chinese) {
		String regex = "^[\u4E00-\u9FA5]+$";
		return Pattern.matches(regex, chinese);
	}
	
	/**
	 * 验证日期（年月日）
	 *
	 * @param birthday 日期，格式：1992-09-03，或1992.09.03
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkBirthday(String birthday) {
		String regex = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
		return Pattern.matches(regex, birthday);
	}
	
	/**
	 * 验证URL地址
	 *
	 * @param url 格式：<a href="http://blog.csdn.net:80/xyang81/article/details/7705960">...</a>?
	 *               或 <a href="http://www.csdn.net:80">...</a>
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkURL(String url) {
		String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
		return Pattern.matches(regex, url);
	}
	
	/**
	 * <pre>
	 * 获取网址 URL 的一级域名
	 * <a href="http://detail.tmall.com/item.htm?spm=a230r.1.10.44.1xpDSH&id=15453106243&_u=f4ve1uq1092">...</a> ->> tmall.com
	 * </pre>
	 *
	 * @param url
	 * @return
	 */
	public static String getDomain(String url) {
		Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
		// 获取完整的域名
		// Pattern p=Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(url);
		matcher.find();
		return matcher.group();
	}
	
	/**
	 * 匹配中国邮政编码
	 *
	 * @param postcode 邮政编码
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkPostcode(String postcode) {
		String regex = "[1-9]\\d{5}";
		return Pattern.matches(regex, postcode);
	}
	
	/**
	 * 匹配IP地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
	 *
	 * @param ipAddress IPv4标准地址
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkIpAddress(String ipAddress) {
		String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
		return Pattern.matches(regex, ipAddress);
	}
	
	/**
	 * 只包含数字和英文的正则表达式
	 *
	 * @param text 传入的字符串
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkIncludeDigitOrEnglish(String text) {
		String regex = "^[0-9a-zA-Z]+$";
		return Pattern.matches(regex, text);
	}
	
	
	
}
