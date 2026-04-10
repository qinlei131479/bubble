package com.bubblecloud.oa.api.vo.schedule;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日程提醒嵌套（列表项）。
 *
 * @author qinlei
 * @date 2026/3/29 20:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ScheduleRemindCardVO {

	private String uniqued;

	private Long sid;

	private String remindDay;

	private String remindTime;

}
