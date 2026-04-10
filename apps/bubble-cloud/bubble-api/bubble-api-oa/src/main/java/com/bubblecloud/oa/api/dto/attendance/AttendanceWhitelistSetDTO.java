package com.bubblecloud.oa.api.dto.attendance;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考勤白名单设置。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@Schema(description = "考勤白名单保存")
public class AttendanceWhitelistSetDTO {

	@Schema(description = "无需考勤人员 admin.id")
	private List<Integer> members = Collections.emptyList();

	@Schema(description = "超级管理员白名单 admin.id")
	private List<Integer> admins = Collections.emptyList();

}
