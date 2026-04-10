package com.bubblecloud.oa.api.vo.program;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 动态中关联的任务摘要（对齐 PHP task 关联）。
 *
 * @author qinlei
 * @date 2026/4/8 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "任务摘要")
public class ProgramTaskTinyVO {

	@Schema(description = "任务ID")
	private Long id;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "分享标识")
	private String ident;

	@Schema(description = "项目ID")
	private Long programId;

}
