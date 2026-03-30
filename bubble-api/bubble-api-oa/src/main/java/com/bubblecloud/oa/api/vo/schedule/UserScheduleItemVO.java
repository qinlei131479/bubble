package com.bubblecloud.oa.api.vo.schedule;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户待办日程项（eb_user_schedule）。
 *
 * @author qinlei
 * @date 2026/3/29 20:45
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "待办日程项")
public class UserScheduleItemVO {

	private Long id;

	private String types;

	private String content;

	private String mark;

	private Integer remind;

	private Integer repeat;

	private Integer period;

	private String remindDay;

	private String remindTime;

	private String endTime;

	private Long linkId;

}
