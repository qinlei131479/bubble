package com.bubblecloud.oa.api.vo.schedule;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /ent/schedule/count 单项（对齐 PHP）。
 *
 * @author qinlei
 * @date 2026/4/6 10:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "日程日历统计日项")
public class ScheduleCalendarCountItemVO {

	private String time;

	private Integer noSubmit;

	private Integer isRest;

}
