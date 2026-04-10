package com.bubblecloud.oa.api.vo.program;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 动态行（describe 解析为 JSON）。
 *
 * @author qinlei
 * @date 2026/4/8 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "动态行")
public class ProgramDynamicRowVO {

	@Schema(description = "主键")
	private Long id;

	@Schema(description = "操作人ID")
	private Long uid;

	@Schema(description = "操作人姓名")
	private String operator;

	@Schema(description = "关联任务ID等")
	private Long relationId;

	@Schema(description = "动作类型")
	private Integer actionType;

	@Schema(description = "标题")
	private String title;

	@Schema(description = "描述（数组或对象）")
	private JsonNode describe;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "关联任务摘要")
	private ProgramTaskTinyVO task;

}
