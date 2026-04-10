package com.bubblecloud.oa.api.dto.program;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目附件重命名（PHP real_name）。
 *
 * @author qinlei
 * @date 2026/4/8 15:30
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "附件重命名")
public class ProgramAttachRenameDTO {

	@Schema(description = "新显示名")
	private String realName;

}
