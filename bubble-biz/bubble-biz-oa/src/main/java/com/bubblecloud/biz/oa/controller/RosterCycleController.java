package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.RosterCycleService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.attendance.RosterCycleSaveDTO;
import com.bubblecloud.oa.api.vo.attendance.OaCreatedIntIdVO;
import com.bubblecloud.oa.api.vo.attendance.RosterCycleListRowVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 排班周期（对齐 PHP {@code ent/attendance/cycle}）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/attendance/cycle")
@Tag(name = "排班周期")
public class RosterCycleController {

	private final RosterCycleService rosterCycleService;

	@GetMapping("/list/{groupId}")
	@Operation(summary = "考勤周期排班列表")
	public R<List<RosterCycleListRowVO>> arrangeList(@PathVariable Integer groupId) {
		try {
			return R.phpOk(rosterCycleService.listForArrange(groupId));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/info/{groupId}/{id}")
	@Operation(summary = "考勤周期详情")
	public R<RosterCycleListRowVO> info(@PathVariable Integer groupId, @PathVariable Integer id) {
		try {
			return R.phpOk(rosterCycleService.getDetail(groupId, id));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/{groupId}")
	@Operation(summary = "考勤周期列表")
	public R<List<RosterCycleListRowVO>> index(@PathVariable Integer groupId) {
		try {
			return R.phpOk(rosterCycleService.listByGroup(groupId));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PostMapping({ "", "/" })
	@Operation(summary = "考勤周期保存")
	public R<OaCreatedIntIdVO> store(@RequestBody(required = false) RosterCycleSaveDTO dto) {
		try {
			if (dto == null) {
				return R.phpFailed("参数错误");
			}
			Integer id = rosterCycleService.saveCycle(OaSecurityUtil.currentUserId(), dto);
			return R.phpOk(new OaCreatedIntIdVO(id));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "考勤周期修改")
	public R<String> update(@PathVariable Integer id, @RequestBody(required = false) RosterCycleSaveDTO dto) {
		try {
			if (dto == null) {
				return R.phpFailed("参数错误");
			}
			rosterCycleService.updateCycle(id, OaSecurityUtil.currentUserId(), dto);
			return R.phpOk("保存成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "考勤周期删除")
	public R<String> destroy(@PathVariable Integer id) {
		try {
			rosterCycleService.deleteCycle(id);
			return R.phpOk(OaConstants.DELETE_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
