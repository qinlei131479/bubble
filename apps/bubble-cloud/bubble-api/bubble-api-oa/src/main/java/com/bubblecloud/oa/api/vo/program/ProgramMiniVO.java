package com.bubblecloud.oa.api.vo.program;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目/版本简要。
 *
 * @author qinlei
 * @date 2026/4/8 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "项目或版本简要")
public class ProgramMiniVO {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "名称")
	private String name;

}
