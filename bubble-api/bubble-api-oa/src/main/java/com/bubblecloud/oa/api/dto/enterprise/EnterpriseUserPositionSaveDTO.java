package com.bubblecloud.oa.api.dto.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "保存企业员工任职经历")
public class EnterpriseUserPositionSaveDTO {

	@JsonProperty("user_id")
	private Long userId;

	@JsonProperty("card_id")
	private Long cardId;

	@JsonProperty("start_time")
	private String startTime;

	@JsonProperty("end_time")
	private String endTime;

	private String position;

	private String department;

	@JsonProperty("is_admin")
	private Integer isAdmin;

	private Integer status;

	private String remark;

}
