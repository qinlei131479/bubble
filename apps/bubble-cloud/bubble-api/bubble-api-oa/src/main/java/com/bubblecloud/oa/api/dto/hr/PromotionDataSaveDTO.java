package com.bubblecloud.oa.api.dto.hr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 晋升数据项保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "晋升数据项保存")
public class PromotionDataSaveDTO {

	@Schema(description = "晋升配置ID")
	private Long pid;

	@Schema(description = "条件名称")
	private String name;

	@Schema(description = "条件说明")
	private String content;

	@Schema(description = "标准值")
	private String standard;

	@Schema(description = "排序")
	private Integer sort;

}
