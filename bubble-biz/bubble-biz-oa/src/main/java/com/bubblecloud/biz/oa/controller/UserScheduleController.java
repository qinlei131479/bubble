package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.ScheduleApiService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.UserScheduleQueryDTO;
import com.bubblecloud.oa.api.vo.schedule.UserScheduleDayWrapperVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户待办日程列表（PHP GET ent/user/schedule）。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user/schedule")
@Tag(name = "用户待办日程")
public class UserScheduleController {

	private final ScheduleApiService scheduleApiService;

	@GetMapping
	@Operation(summary = "日历待办列表")
	public R<List<UserScheduleDayWrapperVO>> list(@ModelAttribute UserScheduleQueryDTO query) {
		return R.phpOk(scheduleApiService.userScheduleList(query));
	}

}
