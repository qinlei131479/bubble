package com.bubblecloud.oa.api.vo.attendance;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考勤组列表行（对齐 PHP index 主要字段）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@Schema(description = "考勤组列表项")
public class AttendanceGroupListItemVO {

	@Schema(description = "主键")
	private Integer id;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "0 人员；1 部门")
	private Integer type;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "班次ID列表")
	private List<Integer> shiftIds = Collections.emptyList();

	@Schema(description = "考勤人员或部门展示")
	private List<OaIdNameVO> members = Collections.emptyList();

	@Schema(description = "负责人 admin.id（含创建人合并）")
	private List<Long> admins = Collections.emptyList();

	@Schema(description = "考勤超级管理员白名单（全局相同）")
	private List<Long> superAdmins = Collections.emptyList();

}
