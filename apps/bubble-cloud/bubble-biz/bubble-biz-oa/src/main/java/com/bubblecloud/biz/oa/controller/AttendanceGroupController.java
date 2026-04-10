package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AttendanceGroupService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.attendance.AttendanceGroupStepOneDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceRepeatCheckDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceWhitelistSetDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.AttendanceGroup;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceGroupCreateVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceGroupDetailVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceGroupListItemVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceGroupSelectItemVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceRepeatConflictVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceWhitelistVO;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 考勤组（对齐 PHP {@code ent/attendance/group}）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/attendance/group")
@Tag(name = "考勤组")
public class AttendanceGroupController {

	private final AttendanceGroupService attendanceGroupService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "考勤组列表")
	public R<ListCountVO<AttendanceGroupListItemVO>> index(@ParameterObject Pg pg,
			@RequestParam(required = false) String name) {
		return R.phpOk(attendanceGroupService.listPage(pg, name));
	}

	@PostMapping
	@Operation(summary = "考勤组保存（第一步）")
	public R<AttendanceGroupCreateVO> create(@RequestBody AttendanceGroupStepOneDTO dto) {
		Integer id = attendanceGroupService.saveGroup(dto, OaSecurityUtil.currentUserId());
		return R.phpOk(new AttendanceGroupCreateVO(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "考勤组分步修改")
	public R<String> update(@PathVariable Long id, @RequestBody JsonNode body) {
		attendanceGroupService.updateByStep(id, body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "考勤组删除")
	public R<String> removeById(@PathVariable Long id) {
		attendanceGroupService.deleteGroup(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "考勤组详情")
	public R<AttendanceGroupDetailVO> info(@PathVariable Long id) {
		return R.phpOk(attendanceGroupService.getInfo(id));
	}

	@GetMapping("/white")
	@Operation(summary = "获取考勤白名单")
	public R<AttendanceWhitelistVO> getWhiteList() {
		return R.phpOk(attendanceGroupService.getWhitelist());
	}

	@PostMapping("/white")
	@Operation(summary = "设置考勤白名单")
	public R<String> setWhiteList(@RequestBody AttendanceWhitelistSetDTO dto) {
		attendanceGroupService.setWhitelist(dto);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@PostMapping("/repeat_check")
	@Operation(summary = "考勤组人员重复检测")
	public R<List<AttendanceRepeatConflictVO>> repeatCheck(@RequestBody AttendanceRepeatCheckDTO dto) {
		return R.phpOk(attendanceGroupService.memberRepeatCheck(dto));
	}

	@GetMapping("/unattended_member")
	@Operation(summary = "未参与考勤人员")
	public R<List<Admin>> unAttendMember() {
		return R.phpOk(attendanceGroupService.getUnAttendMember());
	}

	@GetMapping("/select")
	@Operation(summary = "考勤组下拉")
	public R<List<AttendanceGroupSelectItemVO>> select() {
		return R.phpOk(attendanceGroupService.getSelectList());
	}

	@GetMapping("/member")
	@Operation(summary = "获取参加考勤人员")
	public R<List<AttendanceRepeatConflictVO>> attendanceMember(@RequestParam(required = false) Integer type,
			@RequestParam(required = false) Integer filterId) {
		return R.phpOk(attendanceGroupService.getGroupMembersByType(type, filterId));
	}

}
