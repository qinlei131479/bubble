package com.bubblecloud.oa.api.vo.attendance;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考勤白名单（对齐 PHP getWhitelist）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@Schema(description = "考勤白名单")
public class AttendanceWhitelistVO {

	@Schema(description = "无需考勤人员 uid 列表（与 id 同义）")
	private List<OaIdNameVO> members = Collections.emptyList();

	@Schema(description = "超级管理员白名单")
	private List<OaIdNameVO> admins = Collections.emptyList();

}
