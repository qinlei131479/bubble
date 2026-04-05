package com.bubblecloud.oa.api.dto.resume;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存个人简历工作经历请求 DTO。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "保存个人简历工作经历")
public class UserWorkHistorySaveDTO {

	@Schema(description = "简历ID")
	@JsonProperty("resume_id")
	private Long resumeId;

	@Schema(description = "开始时间")
	@JsonProperty("start_time")
	private String startTime;

	@Schema(description = "结束时间")
	@JsonProperty("end_time")
	private String endTime;

	@Schema(description = "公司名称")
	private String company;

	@Schema(description = "岗位名称")
	private String position;

	@Schema(description = "工作描述")
	private String describe;

	@Schema(description = "离职原因")
	@JsonProperty("quit_reason")
	private String quitReason;

}
