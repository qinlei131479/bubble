package com.bubblecloud.oa.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 晋升表保存参数。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Data
@Schema(description = "晋升表保存")
public class PromotionSaveDTO {

	@Schema(description = "晋升名称")
	private String name;

	@Schema(description = "排序")
	private Integer sort;

}
