package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AttendanceShiftService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.attendance.AttendanceShiftSaveDTO;
import com.bubblecloud.oa.api.entity.AttendanceShift;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftDetailVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftListRowVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftSelectItemVO;
import com.bubblecloud.oa.api.vo.attendance.OaCreatedIntIdVO;
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
 * 考勤班次（对齐 PHP {@code ent/attendance/shift}）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/attendance/shift")
@Tag(name = "考勤班次")
public class AttendanceShiftController {

	private final AttendanceShiftService attendanceShiftService;

	@GetMapping({ "", "/" })
	@Operation(summary = "考勤班次列表")
	public R<ListCountVO<AttendanceShiftListRowVO>> index(@ParameterObject Pg<AttendanceShift> pg,
			@RequestParam(required = false) String name, @RequestParam(required = false) Integer group_id) {
		return R.phpOk(attendanceShiftService.listPage(pg, name, group_id));
	}

	@GetMapping("/select")
	@Operation(summary = "考勤班次下拉")
	public R<List<AttendanceShiftSelectItemVO>> select(@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer group_id) {
		return R.phpOk(attendanceShiftService.selectList(name, group_id));
	}

	@PostMapping({ "", "/" })
	@Operation(summary = "考勤班次保存")
	public R<OaCreatedIntIdVO> store(@RequestBody(required = false) AttendanceShiftSaveDTO dto) {
		try {
			if (dto == null) {
				return R.phpFailed("参数错误");
			}
			Integer id = attendanceShiftService.saveShift(OaSecurityUtil.currentUserId(), dto);
			return R.phpOk(new OaCreatedIntIdVO(id));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "考勤班次修改")
	public R<String> update(@PathVariable Integer id, @RequestBody(required = false) AttendanceShiftSaveDTO dto) {
		try {
			if (dto == null) {
				return R.phpFailed("参数错误");
			}
			attendanceShiftService.updateShift(id, dto);
			return R.phpOk(OaConstants.UPDATE_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "考勤班次删除")
	public R<String> destroy(@PathVariable Integer id) {
		try {
			attendanceShiftService.deleteShift(id);
			return R.phpOk(OaConstants.DELETE_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "考勤班次详情")
	public R<AttendanceShiftDetailVO> info(@PathVariable Integer id) {
		try {
			return R.phpOk(attendanceShiftService.getDetail(id));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
