package com.bubblecloud.oa.api.vo.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统配置分类项（标签与分类键）。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "配置分类项")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ConfigCateItemVO {

	@Schema(description = "展示名称")
	private String label;

	@Schema(description = "分类键")
	private String key;

}
