package com.bubblecloud.oa.api.vo.attendance;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排班详情中单用户整月班次序列。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "排班详情用户行")
public class AttendanceArrangeUserMonthVO {

	@JsonProperty("uid")
	private Integer uid;

	@JsonProperty("shifts")
	private List<Integer> shifts;

}
