package com.bubblecloud.oa.api.dto.hr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 职级体系分类保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "职级体系分类保存")
public class RankCateSaveDTO {

	@Schema(description = "职级体系名称")
	private String name;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "状态 1启用 0停用")
	private Integer status;

}
