package com.bubblecloud.oa.api.dto.attendance;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考勤组第一步保存（基本信息）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@Schema(description = "考勤组第一步")
public class AttendanceGroupStepOneDTO {

	@Schema(description = "名称")
	private String name;

	@Schema(description = "班次ID列表")
	private List<Integer> shifts = Collections.emptyList();

	@Schema(description = "0 人员；1 部门")
	private Integer type;

	@Schema(description = "人员ID 或 部门ID")
	private List<Integer> members = Collections.emptyList();

	@Schema(description = "负责人 admin.id")
	private List<Integer> admins = Collections.emptyList();

	@Schema(description = "无需考勤人员")
	private List<Integer> filters = Collections.emptyList();

	@Schema(description = "其他过滤（无需考勤）")
	private List<Integer> otherFilters = Collections.emptyList();

}
