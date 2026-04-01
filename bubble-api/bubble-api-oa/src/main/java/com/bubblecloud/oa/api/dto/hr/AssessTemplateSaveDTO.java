package com.bubblecloud.oa.api.dto.hr;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核模板保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效考核模板保存")
public class AssessTemplateSaveDTO {

	@Schema(description = "模板名称")
	private String name;

	@Schema(description = "模板类型：kpi/okr")
	private String types;

	@Schema(description = "封面图")
	private String cover;

	@Schema(description = "指标ID列表")
	private List<Long> targetIds;

	@Schema(description = "状态 1启用 0停用")
	private Integer status;

}
