package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * POST /ent/schedule/reply/save 请求体（pid 为日程 id）。
 *
 * @author qinlei
 * @date 2026/4/6 10:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "日程评价保存")
public class ScheduleReplySaveDTO {

	@Schema(description = "日程 id")
	private Long pid;

	private Long replyId;

	private String content;

	private String startTime;

	private String endTime;

	private Long toUid;

}
