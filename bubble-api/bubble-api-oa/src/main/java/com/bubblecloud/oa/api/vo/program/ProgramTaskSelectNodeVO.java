package com.bubblecloud.oa.api.vo.program;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务下拉树节点（value/label，对齐 PHP select）。
 *
 * @author qinlei
 * @date 2026/4/8 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "任务下拉节点")
public class ProgramTaskSelectNodeVO {

	@Schema(description = "值=任务ID")
	private Long value;

	@Schema(description = "标签=任务名")
	private String label;

	@Schema(description = "层级")
	private Integer level;

	@Schema(description = "父ID")
	private Long pid;

	@Schema(description = "四级任务不可选")
	private Boolean disabled;

	@Schema(description = "子节点")
	private List<ProgramTaskSelectNodeVO> children = new ArrayList<>();

}
