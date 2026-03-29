package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.WorkbenchService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.dto.WorkbenchSaveMenusDTO;
import com.bubblecloud.oa.api.dto.WorkbenchSaveStatisticsTypeDTO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchCountVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchFastEntryVO;
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

}
