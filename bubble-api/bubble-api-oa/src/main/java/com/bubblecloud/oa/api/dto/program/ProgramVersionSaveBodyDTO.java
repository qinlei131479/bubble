package com.bubblecloud.oa.api.dto.program;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * POST program_version/{id} 请求体（对齐 PHP setVersion）。
 *
 * @author qinlei
 * @date 2026/4/8 10:30
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "版本批量保存请求体")
public class ProgramVersionSaveBodyDTO {

	@Schema(description = "版本列表")
	private List<ProgramVersionRowDTO> data;

}
