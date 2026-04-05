package com.bubblecloud.oa.api.dto.resume;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qinlei
 */
@Data
@Schema(description = "保存个人简历工作经历")
public class UserWorkHistorySaveDTO {

	@JsonProperty("resume_id")
	private Long resumeId;

	@JsonProperty("start_time")
	private String startTime;

	@JsonProperty("end_time")
	private String endTime;

	private String company;

	private String position;

	private String describe;

	@JsonProperty("quit_reason")
	private String quitReason;

}
