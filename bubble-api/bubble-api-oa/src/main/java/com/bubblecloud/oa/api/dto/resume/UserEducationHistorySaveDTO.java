package com.bubblecloud.oa.api.dto.resume;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qinlei
 */
@Data
@Schema(description = "保存个人简历教育经历")
public class UserEducationHistorySaveDTO {

	@JsonProperty("resume_id")
	private Long resumeId;

	@JsonProperty("start_time")
	private String startTime;

	@JsonProperty("end_time")
	private String endTime;

	@JsonProperty("school_name")
	private String schoolName;

	private String major;

	private String education;

	private String academic;

	private String remark;

}
