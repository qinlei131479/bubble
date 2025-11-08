package com.bubblecloud.common.core.enums;

import lombok.Getter;

/**
 * 枚举类型 - 查询方式
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
@Getter
public enum ListTypeEnum {
	/** 分页 */
	PAGE("分页"),
	/** 数组 */
	ARRAY("数组"),
	/** 单条 */
	ONE("单条");

	ListTypeEnum(String name) {
		this.code = this.name().toLowerCase();
		this.name = name;
	}

	private String code;
	private String name;

}
