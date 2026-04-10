package com.bubblecloud.oa.api.dto.hr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效指标保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效指标保存")
public class AssessTargetSaveDTO {

	@Schema(description = "指标分类ID")
	private Long cateId;

	@Schema(description = "指标名称")
	private String name;

	@Schema(description = "指标说明")
	private String content;

	@Schema(description = "指标类型：kpi/okr")
	private String types;

	@Schema(description = "权重(%)")
	private Integer weight;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "状态 1启用 0停用")
	private Integer status;

}
