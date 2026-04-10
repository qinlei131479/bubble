package com.bubblecloud.oa.api.dto.hr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效删除原因（对齐 PHP {@code delete} 请求体 {@code mark}）。
 *
 * @author qinlei
 * @date 2026/4/5 18:30
 */
@Data
@Schema(description = "绩效删除")
public class AssessDeleteDTO {

	@Schema(description = "删除原因", requiredMode = Schema.RequiredMode.REQUIRED)
	private String mark;

}
