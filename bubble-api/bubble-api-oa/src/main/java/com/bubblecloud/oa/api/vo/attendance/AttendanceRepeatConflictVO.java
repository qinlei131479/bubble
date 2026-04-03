package com.bubblecloud.oa.api.vo.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考勤重复检测命中人员（对齐 PHP memberRepeatCheck 单项）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@Schema(description = "考勤重复冲突项")
public class AttendanceRepeatConflictVO {

	@Schema(description = "员工 admin.id")
	private Long id;

	@Schema(description = "姓名")
	private String name;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "手机号")
	private String phone;

	@Schema(description = "所在考勤组")
	private OaIdNameVO group;

}
