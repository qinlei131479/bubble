package com.bubblecloud.biz.oa.controller;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.EnterpriseUserDailyService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

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
public class EnterpriseUserDailyController {

	private final EnterpriseUserDailyService enterpriseUserDailyService;

	private final AdminService adminService;

	@GetMapping("/menus")
	@Operation(summary = "快捷入口")
	public R<WorkbenchFastEntryVO> menus() {
		return R.phpOk(enterpriseUserDailyService.getFastEntry());
	}

	@PostMapping("/menus")
	@Operation(summary = "保存快捷入口")
	public R<String> saveMenus(@RequestBody WorkbenchSaveMenusDTO dto) {
		List<Integer> ids = ObjectUtil.isNotNull(dto) && ObjectUtil.isNotNull(dto.getData()) ? dto.getData()
				: List.of();
		enterpriseUserDailyService.saveFastEntry(ids);
		return R.phpOk("ok");
	}

	@GetMapping("/count")
	@Operation(summary = "顶部数量统计")
	public R<WorkbenchCountVO> count() {
		return R.phpOk(enterpriseUserDailyService.getWorkCount());
	}

	@GetMapping("/statistics_type")
	@Operation(summary = "业绩统计类型管理")
	public R<WorkbenchStatisticsTypeVO> statisticsType() {
		return R.phpOk(enterpriseUserDailyService.getStatisticsType());
	}

	@PostMapping("/statistics_type")
	@Operation(summary = "保存业绩统计类型")
	public R<String> saveStatisticsType(@RequestBody WorkbenchSaveStatisticsTypeDTO dto) {
		List<String> keys = ObjectUtil.isNotNull(dto) && ObjectUtil.isNotNull(dto.getData()) ? dto.getData()
				: List.of();
		enterpriseUserDailyService.saveStatisticsType(keys);
		return R.phpOk("ok");
	}

	@GetMapping("/statistics/{types}")
	@Operation(summary = "业绩统计卡片")
	public R<List<WorkbenchStatisticCardVO>> statistics(@PathVariable Integer types) {
		return R.phpOk(enterpriseUserDailyService.getStatistics(types));
	}

	@GetMapping("/daily")
	@Operation(summary = "某月日报填写列表")
	public R<Map<Integer, WorkbenchDailyDayVO>> daily(@RequestParam(required = false) String time) {
		Admin admin = adminService.getById(OaSecurityUtil.currentUserId());
		if (ObjectUtil.isNull(admin)) {
			return R.phpFailed("用户不存在");
		}
		String ym;
		if (StrUtil.isBlank(time)) {
			YearMonth now = YearMonth.now();
			ym = now.getYear() + "-" + String.format("%02d", now.getMonthValue());
		} else {
			if (!time.matches("\\d{4}-\\d{1,2}")) {
				return R.phpFailed("时间段格式错误");
			}
			String[] p = time.split("-");
			ym = p[0] + "-" + String.format("%02d", Integer.parseInt(p[1]));
		}
		return R.phpOk(enterpriseUserDailyService.getMonthDaily(admin.getUid(), 1, ym));
	}

	@GetMapping("/pending")
	@Operation(summary = "待办列表")
	public R<List<UserPending>> pending(@RequestParam String status) {
		Admin admin = adminService.getById(OaSecurityUtil.currentUserId());
		if (ObjectUtil.isNull(admin)) {
			return R.phpFailed("用户不存在");
		}
		return R.phpOk(enterpriseUserDailyService.getPendingList(admin.getUid(), 1, status));
	}

}
