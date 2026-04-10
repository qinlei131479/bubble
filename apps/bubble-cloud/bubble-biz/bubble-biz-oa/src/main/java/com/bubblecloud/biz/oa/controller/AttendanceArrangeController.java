package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AttendanceArrangeService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.attendance.AttendanceArrangeBatchUpdateDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceArrangeMonthInitDTO;
import com.bubblecloud.oa.api.entity.AttendanceArrange;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceArrangeInfoVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceArrangeListRowVO;
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
 * 考勤排班（对齐 PHP {@code ent/attendance/arrange}）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/attendance/arrange")
@Tag(name = "考勤排班")
public class AttendanceArrangeController {

	private final AttendanceArrangeService attendanceArrangeService;

	@GetMapping({ "", "/" })
	@Operation(summary = "考勤排班列表")
	public R<ListCountVO<AttendanceArrangeListRowVO>> index(@ParameterObject Pg<AttendanceArrange> pg,
			@RequestParam(required = false) String name, @RequestParam(required = false) String time) {
		return R.phpOk(attendanceArrangeService.listPage(pg, name, time));
	}

	@PostMapping({ "", "/" })
	@Operation(summary = "考勤排班保存（初始化月份）")
	public R<String> store(@RequestBody(required = false) AttendanceArrangeMonthInitDTO dto) {
		try {
			attendanceArrangeService.saveMonth(OaSecurityUtil.currentUserId(), dto);
			return R.phpOk(OaConstants.INSERT_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/{groupId}")
	@Operation(summary = "考勤排班修改")
	public R<String> update(@PathVariable Integer groupId,
			@RequestBody(required = false) AttendanceArrangeBatchUpdateDTO body) {
		try {
			attendanceArrangeService.updateBatch(groupId, OaSecurityUtil.currentUserId(), body);
			return R.phpOk(OaConstants.UPDATE_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/info/{groupId}")
	@Operation(summary = "考勤排班详情")
	public R<AttendanceArrangeInfoVO> info(@PathVariable Integer groupId, @RequestParam(required = false) String name,
			@RequestParam(required = false) String date) {
		try {
			return R.phpOk(attendanceArrangeService.getInfo(groupId, name, date));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
