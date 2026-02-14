package com.bubblecloud.common.core.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Rampart
 * @date 2026-02-16
 * <p>
 * 字典类型
 */
@Getter
@RequiredArgsConstructor
public enum FlagEnum {

	/**
	 * 否
	 */
	NO("0", "否"),

	/**
	 * 是
	 */
	YES("1", "是");

	/**
	 * 类型
	 */
	private final String code;

	/**
	 * 描述
	 */
	private final String description;

}
