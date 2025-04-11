package com.henu.registration.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * 政治面貌枚举类
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum PoliticalStatusEnum {
	
	COMMUNIST_PARTY_MEMBER("党员", "communist_party_member"),
	PROBATIONARY_PARTY_MEMBER("预备党员", "probationary_party_member"),
	LEAGUE_MEMBER("团员", "league_member"),
	PUBLIC_PEOPLE("群众", "public_people");
	
	private final String text;
	
	private final String value;
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return {@link PoliticalStatusEnum}
	 */
	public static PoliticalStatusEnum getEnumByValue(String value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (PoliticalStatusEnum reviewStatusEnum : PoliticalStatusEnum.values()) {
			if (Objects.equals(reviewStatusEnum.value, value)) {
				return reviewStatusEnum;
			}
		}
		return null;
	}
}
