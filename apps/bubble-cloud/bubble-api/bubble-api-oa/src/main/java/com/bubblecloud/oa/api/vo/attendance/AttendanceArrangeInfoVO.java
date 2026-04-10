package com.bubblecloud.oa.api.vo.attendance;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排班详情（对齐 PHP getInfo compact）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "排班详情")
public class AttendanceArrangeInfoVO {

	@JsonProperty("arrange")
	private List<AttendanceArrangeUserMonthVO> arrange = Collections.emptyList();

	@JsonProperty("calendar")
	private List<AttendanceArrangeCalendarDayVO> calendar = Collections.emptyList();

	@JsonProperty("members")
	private List<OaIdNameVO> members = Collections.emptyList();

}
