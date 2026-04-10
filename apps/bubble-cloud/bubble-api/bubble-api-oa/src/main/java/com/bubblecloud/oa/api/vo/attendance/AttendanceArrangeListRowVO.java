package com.bubblecloud.oa.api.vo.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排班列表行。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "排班列表行")
public class AttendanceArrangeListRowVO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("group_id")
	private Integer groupId;

	@JsonProperty("uid")
	private Integer uid;

	@JsonProperty("date")
	private String date;

	@JsonProperty("created_at")
	private String createdAt;

	@JsonProperty("updated_at")
	private String updatedAt;

	@JsonProperty("group")
	private AttendanceArrangeGroupStubVO group;

}
