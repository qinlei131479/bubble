package com.bubblecloud.oa.api.dto.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存企业员工任职经历请求 DTO。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "保存企业员工任职经历")
public class EnterpriseUserPositionSaveDTO {

	@Schema(description = "用户ID")
	@JsonProperty("user_id")
	private Long userId;

	@Schema(description = "名片/员工 admin.id")
	@JsonProperty("card_id")
	private Long cardId;

	@Schema(description = "任职开始时间")
	@JsonProperty("start_time")
	private String startTime;

	@Schema(description = "任职结束时间")
	@JsonProperty("end_time")
	private String endTime;

	@Schema(description = "岗位名称")
	private String position;

	@Schema(description = "部门")
	private String department;

	@Schema(description = "是否负责人")
	@JsonProperty("is_admin")
	private Integer isAdmin;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "备注")
	private String remark;

}
