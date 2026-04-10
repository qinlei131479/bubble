package com.bubblecloud.biz.oa.controller;

import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AttendanceEntStatisticsService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.attendance.AttendanceStatisticsAdjustDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 考勤统计与打卡（对齐 PHP {@code AttendanceStatisticsController} +
 * {@code AttendanceClockController}）。
 *
 * @author qinlei
 * @date 2026/4/7
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/attendance")
@Tag(name = "考勤统计与打卡")
public class AttendanceEntStatisticsController {

	private final AttendanceEntStatisticsService attendanceEntStatisticsService;

	@GetMapping("/daily_statistics")
	@Operation(summary = "考勤每日统计")
	public R<Map<String, Object>> dailyStatistics(@ParameterObject Pg<Object> pg,
			@RequestParam(required = false) Integer scope,
			@RequestParam(required = false, name = "personnel_status") List<Integer> personnelStatus,
			@RequestParam(required = false, name = "frame_id") Integer frameId,
			@RequestParam(required = false, name = "group_id") Integer groupId,
			@RequestParam(required = false) String time,
			@RequestParam(required = false, name = "user_id") List<Integer> userId) {
		return R.phpOk(attendanceEntStatisticsService.dailyStatistics(pg, OaSecurityUtil.currentUserId(), scope,
				personnelStatus, frameId, groupId, time, userId, 0));
	}

	@GetMapping("/monthly_statistics")
	@Operation(summary = "考勤月度统计")
	public R<Map<String, Object>> monthlyStatistics(@ParameterObject Pg<Object> pg,
			@RequestParam(required = false) Integer scope,
			@RequestParam(required = false, name = "personnel_status") List<Integer> personnelStatus,
			@RequestParam(required = false, name = "frame_id") Integer frameId,
			@RequestParam(required = false, name = "group_id") Integer groupId,
			@RequestParam(required = false) String time) {
		return R.phpOk(attendanceEntStatisticsService.monthlyStatistics(pg, OaSecurityUtil.currentUserId(), scope,
				personnelStatus, frameId, groupId, time));
	}

	@PutMapping("/statistics/{id}")
	@Operation(summary = "考勤添加处理记录")
	public R<String> updateStatistics(@PathVariable("id") long id,
			@RequestBody(required = false) AttendanceStatisticsAdjustDTO dto) {
		attendanceEntStatisticsService.saveStatisticsResult(OaSecurityUtil.currentUserId(), id, dto);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@GetMapping("/statistics/{id}")
	@Operation(summary = "考勤处理记录")
	public R<Map<String, Object>> statisticsRecords(@ParameterObject Pg<Object> pg, @PathVariable("id") long id) {
		return R.phpOk(attendanceEntStatisticsService.handleRecordList(pg, OaSecurityUtil.currentUserId(), id));
	}

	@GetMapping("/attendance_statistics")
	@Operation(summary = "考勤出勤统计")
	public R<Map<String, Object>> attendanceStatistics(@RequestParam(required = false, name = "user_id") Integer userId,
			@RequestParam(required = false) String time) {
		return R
			.phpOk(attendanceEntStatisticsService.attendanceStatistics(OaSecurityUtil.currentUserId(), userId, time));
	}

	@GetMapping("/individual_statistics")
	@Operation(summary = "考勤个人统计")
	public R<Map<String, Object>> individualStatistics(@ParameterObject Pg<Object> pg,
			@RequestParam(required = false, name = "personnel_status") List<Integer> personnelStatus,
			@RequestParam(required = false, name = "user_id") List<Integer> userId,
			@RequestParam(required = false) String time) {
		String t = StrUtil.isBlank(time) ? "month" : time;
		return R.phpOk(attendanceEntStatisticsService.individualStatistics(pg, OaSecurityUtil.currentUserId(),
				personnelStatus, userId, t));
	}

	@GetMapping("/clock_record")
	@Operation(summary = "考勤打卡记录")
	public R<Map<String, Object>> clockRecord(@ParameterObject Pg<Object> pg,
			@RequestParam(required = false) Integer scope,
			@RequestParam(required = false, name = "frame_id") Integer frameId,
			@RequestParam(required = false, name = "group_id") Integer groupId,
			@RequestParam(required = false) String time, @RequestParam(required = false) Integer uid) {
		return R.phpOk(attendanceEntStatisticsService.clockRecordList(pg, scope, frameId, groupId, time, uid));
	}

	@GetMapping("/clock_record/{id}")
	@Operation(summary = "考勤打卡详情")
	public R<Map<String, Object>> clockRecordInfo(@PathVariable("id") long id) {
		return R.phpOk(attendanceEntStatisticsService.clockRecordInfo(id));
	}

	@GetMapping("/abnormal_date")
	@Operation(summary = "考勤异常日期列表")
	public R<List<Map<String, Object>>> abnormalDate() {
		return R.phpOk(attendanceEntStatisticsService.abnormalDateList(OaSecurityUtil.currentUserId()));
	}

	@GetMapping("/abnormal_record/{id}")
	@Operation(summary = "考勤异常记录列表")
	public R<List<Map<String, Object>>> abnormalRecord(@PathVariable("id") long id) {
		return R.phpOk(attendanceEntStatisticsService.abnormalRecordList(OaSecurityUtil.currentUserId(), id));
	}

	@PostMapping("/clock/import_record")
	@Operation(summary = "导入打卡记录")
	public R<String> importRecord(@RequestBody(required = false) Map<String, Object> body) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> data = body == null ? List.of() : (List<Map<String, Object>>) body.get("data");
		attendanceEntStatisticsService.importClockRecord(data);
		return R.phpOk("导入成功");
	}

	@PostMapping("/clock/import_third")
	@Operation(summary = "导入三方打卡记录")
	public R<String> importThird(@RequestBody(required = false) Map<String, Object> body) {
		int type = 0;
		List<Map<String, Object>> data = List.of();
		if (body != null) {
			Object t = body.get("type");
			if (t instanceof Number n) {
				type = n.intValue();
			}
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> d = (List<Map<String, Object>>) body.get("data");
			data = d == null ? List.of() : d;
		}
		attendanceEntStatisticsService.importClockThirdParty(type, data);
		return R.phpOk("导入成功");
	}

}
