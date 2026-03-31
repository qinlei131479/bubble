package com.bubblecloud.biz.oa.controller;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import com.bubblecloud.biz.oa.security.OaCurrentUser;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.WorkbenchService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.UserPending;
import com.bubblecloud.oa.api.dto.WorkbenchSaveMenusDTO;
import com.bubblecloud.oa.api.dto.WorkbenchSaveStatisticsTypeDTO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchCountVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchFastEntryVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchDailyDayVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchStatisticCardVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchStatisticsTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作台接口（PHP ent/user/work）。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user/work")
@Tag(name = "工作台")
public class WorkbenchController {

	private final WorkbenchService workbenchService;

	private final AdminService adminService;

	@GetMapping("/menus")
	@Operation(summary = "快捷入口")
	public PhpResponse<WorkbenchFastEntryVO> menus() {
		return PhpResponse.ok(workbenchService.getFastEntry());
	}

	@PostMapping("/menus")
	@Operation(summary = "保存快捷入口")
	public PhpResponse<String> saveMenus(@RequestBody(required = false) WorkbenchSaveMenusDTO body) {
		List<Integer> ids = body != null && body.getData() != null ? body.getData() : List.of();
		workbenchService.saveFastEntry(ids);
		return PhpResponse.ok("ok");
	}

	@GetMapping("/count")
	@Operation(summary = "顶部数量统计")
	public PhpResponse<WorkbenchCountVO> count() {
		return PhpResponse.ok(workbenchService.getWorkCount());
	}

	@GetMapping("/statistics_type")
	@Operation(summary = "业绩统计类型管理")
	public PhpResponse<WorkbenchStatisticsTypeVO> statisticsType() {
		return PhpResponse.ok(workbenchService.getStatisticsType());
	}

	@PostMapping("/statistics_type")
	@Operation(summary = "保存业绩统计类型")
	public PhpResponse<String> saveStatisticsType(@RequestBody(required = false) WorkbenchSaveStatisticsTypeDTO body) {
		List<String> keys = body != null && body.getData() != null ? body.getData() : List.of();
		workbenchService.saveStatisticsType(keys);
		return PhpResponse.ok("ok");
	}

	@GetMapping("/statistics/{types}")
	@Operation(summary = "业绩统计卡片")
	public PhpResponse<List<WorkbenchStatisticCardVO>> statistics(@PathVariable int types) {
		return PhpResponse.ok(workbenchService.getStatistics(types));
	}

	@GetMapping("/daily")
	@Operation(summary = "某月日报填写列表")
	public PhpResponse<Map<Integer, WorkbenchDailyDayVO>> daily(Authentication authentication,
			@RequestParam(required = false) String time) {
		if (authentication == null || !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return PhpResponse.failed("未登录");
		}
		Admin admin = adminService.getById(currentUser.getId());
		if (admin == null) {
			return PhpResponse.failed("用户不存在");
		}
		String ym;
		if (time == null || time.isBlank()) {
			YearMonth now = YearMonth.now();
			ym = now.getYear() + "-" + String.format("%02d", now.getMonthValue());
		}
		else {
			if (!time.matches("\\d{4}-\\d{1,2}")) {
				return PhpResponse.failed("时间段格式错误");
			}
			String[] p = time.split("-");
			ym = p[0] + "-" + String.format("%02d", Integer.parseInt(p[1]));
		}
		return PhpResponse.ok(workbenchService.getMonthDaily(admin.getUid(), 1, ym));
	}

	@GetMapping("/pending")
	@Operation(summary = "待办列表")
	public PhpResponse<List<UserPending>> pending(Authentication authentication,
			@RequestParam(required = false) String status) {
		if (authentication == null || !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return PhpResponse.failed("未登录");
		}
		Admin admin = adminService.getById(currentUser.getId());
		if (admin == null) {
			return PhpResponse.failed("用户不存在");
		}
		return PhpResponse.ok(workbenchService.getPendingList(admin.getUid(), 1, status));
	}

}
