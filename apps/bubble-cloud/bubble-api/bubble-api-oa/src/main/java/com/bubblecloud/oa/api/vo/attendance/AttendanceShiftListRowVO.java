package com.bubblecloud.oa.api.vo.attendance;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 班次列表行（对齐 PHP index with card/times）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "班次列表行")
public class AttendanceShiftListRowVO {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("uid")
	private Integer uid;

	@JsonProperty("color")
	private String color;

	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	@JsonProperty("updated_at")
	private LocalDateTime updatedAt;

	@JsonProperty("card")
	private AttendanceShiftAdminBriefVO card;

	@JsonProperty("times")
	private List<AttendanceShiftTimeSlotVO> times = Collections.emptyList();

}
