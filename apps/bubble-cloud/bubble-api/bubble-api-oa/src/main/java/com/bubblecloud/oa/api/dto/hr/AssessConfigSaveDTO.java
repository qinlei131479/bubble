package com.bubblecloud.oa.api.dto.hr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效配置保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效配置保存")
public class AssessConfigSaveDTO {

	@Schema(description = "配置键")
	private String key;

	@Schema(description = "配置值（JSON）")
	private String value;

}
