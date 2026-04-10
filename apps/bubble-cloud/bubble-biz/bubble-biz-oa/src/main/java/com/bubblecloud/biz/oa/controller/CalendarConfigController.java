package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.CalendarConfigService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.CalendarUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 考勤日历配置（对齐 PHP {@code ent/attendance/calendar}）。
 *
 * @author qinlei
 * @date 2026/4/2 15:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/attendance/calendar")
@Tag(name = "考勤日历配置")
public class CalendarConfigController {

	private final CalendarConfigService calendarConfigService;

	@GetMapping("/{time}")
	@Operation(summary = "考勤日历配置详情（休息日列表）")
	public R<List<String>> index(@PathVariable String time) {
		return R.phpOk(calendarConfigService.getRestList(time));
	}

	@PutMapping("/{date}")
	@Operation(summary = "考勤日历配置保存")
	public R<String> update(@PathVariable String date, @RequestBody(required = false) CalendarUpdateDTO dto) {
		calendarConfigService.updateCalendar(date, dto);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

}
