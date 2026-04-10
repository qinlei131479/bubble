package com.bubblecloud.oa.api.vo.schedule;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日程类型项。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ScheduleTypeVO {

	private Long id;

	private String name;

	private String key;

	private String color;

	private String info;

	private Integer isPublic;

}
