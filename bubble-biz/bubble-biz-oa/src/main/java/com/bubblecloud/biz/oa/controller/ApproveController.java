package com.bubblecloud.biz.oa.controller;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.placeholder.HolidayTypeOptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Phase 6：审批（占位）。
 *
 * @author qinlei
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/approve")
@Tag(name = "审批管理")
public class ApproveController {

	@GetMapping("/page")
	@Operation(summary = "审批配置分页")
	public PhpResponse<SimplePageVO> page(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

	/**
	 * 与 PHP {@code ent/approve/holiday_type/select} 一致：{@code [{value,label,duration_type},...]}。
	 */
	@GetMapping("/holiday_type/select")
	@Operation(summary = "假期类型下拉")
	public PhpResponse<List<HolidayTypeOptionVO>> holidayTypeSelect() {
		return PhpResponse.ok(Collections.emptyList());
	}

}
