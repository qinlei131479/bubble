package com.bubblecloud.oa.api.vo.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典类型详情（含是否可删除），对齐 PHP dict_type info。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:05
 */
@Data
@Schema(description = "字典类型详情")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DictTypeInfoVO {

	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "字典名称")
	private String name;

	@Schema(description = "字典标识")
	private String ident;

	@Schema(description = "关联业务")
	private String linkType;

	@Schema(description = "最大层级")
	private Integer level;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "是否允许删除")
	private Boolean canDelete;

}
