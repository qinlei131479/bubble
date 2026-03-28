package com.bubblecloud.oa.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统配置查询参数。
 *
 * @author qinlei
 */
@Data
@Schema(description = "系统配置查询")
public class ConfigQueryDTO {

	@Schema(description = "配置类型，如 system")
	private String type = "system";

}
