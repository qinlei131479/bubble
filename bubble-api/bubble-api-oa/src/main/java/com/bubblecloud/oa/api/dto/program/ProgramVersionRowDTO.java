package com.bubblecloud.oa.api.dto.program;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 版本行（保存接口 data 数组元素）。
 *
 * @author qinlei
 * @date 2026/4/8 10:30
 */
@Data
@Schema(description = "版本行")
public class ProgramVersionRowDTO {

	@Schema(description = "版本ID，新建时为空或小于1")
	private Long id;

	@Schema(description = "版本名称")
	private String name;

}
