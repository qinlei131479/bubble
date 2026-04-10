package com.bubblecloud.oa.api.dto.attendance;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考勤组人员重复检测。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@Schema(description = "考勤重复检测参数")
public class AttendanceRepeatCheckDTO {

	@Schema(description = "当前考勤组ID，新建为0")
	private Integer id;

	@Schema(description = "0 人员；1 部门")
	private Integer type;

	@Schema(description = "人员ID 或 部门ID")
	private List<Integer> members = Collections.emptyList();

}
