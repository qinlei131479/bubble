package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * POST ent/daily/reply 请求体。
 *
 * @author qinlei
 * @date 2026/4/6 12:30
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "日报回复")
public class DailyReplyDTO {

	private String content;

	private Long dailyId;

	private Long pid;

	private String uid;

}
