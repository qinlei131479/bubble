package com.bubblecloud.oa.api.vo.schedule;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日程类型嵌套（列表项）。
 *
 * @author qinlei
 * @date 2026/3/29 20:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ScheduleTypeCardVO {

	private Long id;

	private String name;

	private String color;

	private String info;

}
