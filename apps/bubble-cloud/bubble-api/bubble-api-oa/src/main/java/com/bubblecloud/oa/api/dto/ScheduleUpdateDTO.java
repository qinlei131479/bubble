package com.bubblecloud.oa.api.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * PUT /ent/schedule/update/{id} 请求体（对齐 PHP；核心先支持 type=CHANGE_ALL）。
 *
 * @author qinlei
 * @date 2026/4/6 10:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "修改日程")
public class ScheduleUpdateDTO {

	private String title;

	private List<Long> member;

	private String content;

	private Long cid;

	private String color;

	private Integer remind;

	private String remindTime;

	private Integer repeat;

	private Integer period;

	private Integer rate;

	private List<Integer> days;

	private Integer allDay;

	private String startTime;

	private String endTime;

	private String failTime;

	private String uniqued;

	private Long linkId;

	@Schema(description = "0 当前 1 当前及以后 2 全部（核心实现 2）")
	private Integer type;

	private String start;

	private String end;

}
