package com.bubblecloud.oa.api.dto.hr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 职级保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "职级保存")
public class RankSaveDTO {

	@Schema(description = "职级体系分类ID")
	private Long cateId;

	@Schema(description = "职级名称")
	private String name;

	@Schema(description = "职级编码")
	private String code;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "状态 1启用 0停用")
	private Integer status;

	@Schema(description = "备注")
	private String mark;

}
