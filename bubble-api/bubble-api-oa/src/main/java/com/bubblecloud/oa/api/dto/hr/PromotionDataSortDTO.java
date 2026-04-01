package com.bubblecloud.oa.api.dto.hr;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 晋升数据项排序 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "晋升数据项排序")
public class PromotionDataSortDTO {

	@Schema(description = "排序后的ID列表（从前到后对应 sort 1,2,3...）")
	private List<Long> ids;

}
