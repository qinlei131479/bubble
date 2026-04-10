package com.bubblecloud.oa.api.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * POST /ent/schedule/store 请求体（对齐 PHP）。
 *
 * @author qinlei
 * @date 2026/4/6 10:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "新建日程")
public class ScheduleStoreDTO {

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

}
