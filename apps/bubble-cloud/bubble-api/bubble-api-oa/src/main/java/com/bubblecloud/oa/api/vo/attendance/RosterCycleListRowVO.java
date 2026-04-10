package com.bubblecloud.oa.api.vo.attendance;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排班周期列表行（含关联班次）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "排班周期列表行")
public class RosterCycleListRowVO {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("group_id")
	private Integer groupId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("cycle")
	private Integer cycle;

	@JsonProperty("shifts")
	private List<AttendanceShiftSelectItemVO> shifts = Collections.emptyList();

}
