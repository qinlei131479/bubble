package com.bubblecloud.oa.api.dto.program;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存下级任务（对齐 PHP subordinateStore）。
 *
 * @author qinlei
 * @date 2026/4/8 10:30
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "下级任务保存参数")
public class ProgramTaskSubordinateDTO {

	@Schema(description = "父任务ID")
	private Long pid;

	@Schema(description = "任务名称")
	private String name;

}
