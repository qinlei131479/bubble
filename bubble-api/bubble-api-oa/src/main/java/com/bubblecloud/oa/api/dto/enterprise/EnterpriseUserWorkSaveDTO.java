package com.bubblecloud.oa.api.dto.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存企业员工工作经历请求 DTO。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "保存企业员工工作经历")
public class EnterpriseUserWorkSaveDTO {

	@Schema(description = "用户ID")
	@JsonProperty("user_id")
	private Long userId;

	@Schema(description = "名片/员工 admin.id")
	@JsonProperty("card_id")
	private Long cardId;

	@Schema(description = "入职时间")
	@JsonProperty("start_time")
	private String startTime;

	@Schema(description = "离职时间")
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
