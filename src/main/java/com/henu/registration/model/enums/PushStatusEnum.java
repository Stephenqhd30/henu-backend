package com.henu.registration.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推送状态枚举
 * (0-未推送,1-成功,2-失败,3-重试中)
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum PushStatusEnum {
	
	NOT_PUSHED("未推送", 0),
	SUCCEED("成功", 1),
	FAILED("失败", 2),
	RETRYING("重试中", 3);
	
	private final String text;
	
	private final Integer value;
	
	/**
	 * 获取值列表
	 *
	 * @return {@link List<String>}
	 */
	public static List<Integer> getValues() {
		return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
	}
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return {@link PushStatusEnum}
	 */
	public static PushStatusEnum getEnumByValue(Integer value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (PushStatusEnum anEnum : PushStatusEnum.values()) {
			if (anEnum.value.equals(value)) {
				return anEnum;
			}
		}
		return null;
	}
	
}
