package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * POST /ent/schedule/reply/save 请求体。
 *
 * @author qinlei
 * @date 2026/3/29 20:40
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "保存日程评价")
public class ScheduleReplySaveDTO {

	@Schema(description = "日程 id，PHP 字段 pid")
	private Long scheduleId;

	private Long replyId;

	private String content;

	private String startTime;

	private String endTime;

	@Schema(description = "被回复人 admin id")
	private String toUid;

}
