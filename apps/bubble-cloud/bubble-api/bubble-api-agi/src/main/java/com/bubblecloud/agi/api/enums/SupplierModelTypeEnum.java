package com.bubblecloud.agi.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * AI 供应商模型类型（与 {@code supplier_model.model_type} 存库值一致）
 *
 * @author qinlei
 * @date 2026/4/17
 */
@Getter
@RequiredArgsConstructor
public enum SupplierModelTypeEnum {

	/**
	 * 聊天
	 */
	CHAT("1", "聊天"),

	/**
	 * 推理
	 */
	REASONING("2", "推理"),

	/**
	 * 向量
	 */
	VECTOR("3", "向量"),

	/**
	 * 排序
	 */
	RANKING("4", "排序"),

	/**
	 * 图片
	 */
	IMAGE("5", "图片"),

	/**
	 * 视觉
	 */
	VISION("6", "视觉"),

	/**
	 * 解析 OCR
	 */
	PARSING_OCR("7", "解析OCR"),

	/**
	 * 视觉 OCR
	 */
	VISION_OCR("8", "视觉OCR"),

	/**
	 * 音频
	 */
	AUDIO("9", "音频");

	/**
	 * 存库 / 接口传递的字典码
	 */
	private final String code;

	/**
	 * 说明
	 */
	private final String description;

}
