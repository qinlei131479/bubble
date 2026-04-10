package com.bubblecloud.oa.api.vo.attendance;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.oa.api.entity.AttendanceGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考勤组详情（对齐 PHP getInfo toArray 主要字段）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@Schema(description = "考勤组详情")
public class AttendanceGroupDetailVO {

	@Schema(description = "主表数据")
	private AttendanceGroup group;

	@Schema(description = "班次ID列表")
	private List<Integer> shiftIds = Collections.emptyList();

	@Schema(description = "考勤人员/部门展示")
	private List<OaIdNameVO> members = Collections.emptyList();

	@Schema(description = "无需考勤人员")
	private List<OaIdNameVO> filters = Collections.emptyList();

	@Schema(description = "负责人")
	private List<OaIdNameVO> admins = Collections.emptyList();

	@Schema(description = "补卡类型（解析 repair_type JSON，便于前端展示）")
	private List<Integer> repairTypes = Collections.emptyList();

}
