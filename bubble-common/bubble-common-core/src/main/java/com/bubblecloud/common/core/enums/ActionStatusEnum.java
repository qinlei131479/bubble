package com.bubblecloud.common.core.enums;

import lombok.Getter;

/**
 * 接口返回对象Res枚举类型
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
@Getter
public enum ActionStatusEnum {
	/**
	 * 初始化
	 */
	INIT("初始化"),
	/**
	 * 校验
	 */
	CHECK("校验");

	ActionStatusEnum(String name) {
		this.code = this.name().toLowerCase();
		this.name = name;
	}

	private String code;
	private String name;
}
