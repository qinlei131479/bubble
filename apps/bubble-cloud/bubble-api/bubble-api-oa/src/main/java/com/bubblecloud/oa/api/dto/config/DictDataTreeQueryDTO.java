package com.bubblecloud.oa.api.dto.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典数据树查询条件（POST body）。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:05
 */
@Data
@Schema(description = "字典数据树查询")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DictDataTreeQueryDTO {

	@Schema(description = "字典类型ID")
	private Integer typeId;

	@Schema(description = "类型名称/标识")
	private String types;

	@Schema(description = "名称模糊")
	private String name;

	@Schema(description = "状态")
	private Integer status;

}
