package com.bubblecloud.oa.api.dto.config;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存防火墙配置请求体。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:05
 */
@Data
@Schema(description = "防火墙配置保存")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FirewallConfigSaveDTO {

	@Schema(description = "防火墙开关")
	private Integer firewallSwitch;

	@Schema(description = "防火墙正则规则列表")
	private List<String> firewallContent;

}
