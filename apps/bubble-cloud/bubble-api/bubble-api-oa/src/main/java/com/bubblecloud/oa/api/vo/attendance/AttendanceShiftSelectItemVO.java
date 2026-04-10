package com.bubblecloud.oa.api.vo.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 班次下拉项。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "班次下拉项")
public class AttendanceShiftSelectItemVO {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("color")
	private String color;

}
