package com.bubblecloud.oa.api.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * POST /ent/schedule/store 请求体（与 PHP postMore 对齐）。
 *
 * @author qinlei
 * @date 2026/3/29 20:40
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "新建日程")
public class ScheduleSaveDTO {

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

	private Long linkId;

}
