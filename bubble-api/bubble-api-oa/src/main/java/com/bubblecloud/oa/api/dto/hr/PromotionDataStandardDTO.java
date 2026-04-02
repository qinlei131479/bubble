package com.bubblecloud.oa.api.dto.hr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 晋升数据项标准修改 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "晋升数据项标准修改")
public class PromotionDataStandardDTO {

	@Schema(description = "标准值")
	private String standard;

	@Schema(description = "排序后的ID列表（从前到后对应 sort 1,2,3...）")
	private List<Long> ids;
}
