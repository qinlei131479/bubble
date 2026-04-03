package com.bubblecloud.oa.api.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考勤日历批量保存（对齐 PHP postMore data）。
 *
 * @author qinlei
 * @date 2026/4/2 14:00
 */
@Data
@Schema(description = "考勤日历保存")
public class CalendarUpdateDTO {

	@Schema(description = "日期配置列表")
	private List<CalendarDayItemDTO> data;

	@Schema(description = "日期")
	private String date;

}
