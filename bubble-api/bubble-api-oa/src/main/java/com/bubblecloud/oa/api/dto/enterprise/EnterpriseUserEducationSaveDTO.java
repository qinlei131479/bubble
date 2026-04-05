package com.bubblecloud.oa.api.dto.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存企业员工教育经历请求 DTO。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "保存企业员工教育经历")
public class EnterpriseUserEducationSaveDTO {

	@Schema(description = "用户ID")
	@JsonProperty("user_id")
	private Long userId;

	@Schema(description = "名片/员工 admin.id")
	@JsonProperty("card_id")
	private Long cardId;

	@Schema(description = "开始时间")
	@JsonProperty("start_time")
	private String startTime;

	@Schema(description = "结束时间")
	@JsonProperty("end_time")
	private String endTime;

	@Schema(description = "学校名称")
	@JsonProperty("school_name")
	private String schoolName;

	@Schema(description = "专业")
	private String major;

	@Schema(description = "学历")
	private String education;

	@Schema(description = "学位")
	private String academic;

	@Schema(description = "备注")
	private String remark;

}
