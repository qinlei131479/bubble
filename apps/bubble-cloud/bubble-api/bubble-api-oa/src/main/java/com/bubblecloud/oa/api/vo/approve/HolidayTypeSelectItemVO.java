package com.bubblecloud.oa.api.vo.approve;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 假期类型下拉项（对齐 PHP select 接口）。
 *
 * @author qinlei
 * @date 2026/4/2 14:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "假期类型下拉项")
public class HolidayTypeSelectItemVO {

	@Schema(description = "值")
	private Integer value;

	@Schema(description = "标签")
	private String label;

	@Schema(description = "时长类型")
	private Integer durationType;

}
