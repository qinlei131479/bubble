package com.bubblecloud.oa.api.vo.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排班详情日历格。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "排班日历日")
public class AttendanceArrangeCalendarDayVO {

	@JsonProperty("date")
	private String date;

	@JsonProperty("is_rest")
	private Integer isRest;

}
