package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.ScheduleApiService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.ScheduleIndexQueryDTO;
import com.bubblecloud.oa.api.dto.ScheduleStatusUpdateDTO;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleRecordVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;

/**
 * 日程接口（PHP ent/schedule）。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/schedule")
@Tag(name = "日程")
public class ScheduleController {

	private final ScheduleApiService scheduleApiService;

	@GetMapping("/page")
	@Operation(summary = "日程分页（占位）")
	public R<SimplePageVO> page(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return R.phpOk(SimplePageVO.empty(current, size));
	}

	@GetMapping("/types")
	@Operation(summary = "日程类型列表")
	public R<List<ScheduleTypeVO>> types() {
		return R.phpOk(scheduleApiService.typeList());
	}

	@PostMapping("/index")
	@Operation(summary = "日程列表")
	public R<List<ScheduleRecordVO>> list(@RequestBody(required = false) ScheduleIndexQueryDTO body) {
		return R
			.phpOk(scheduleApiService.scheduleIndex(ObjectUtil.isNotNull(body) ? body : new ScheduleIndexQueryDTO()));
	}

	@PutMapping("/status/{id}")
	@Operation(summary = "修改日程状态")
	public R<String> status(@PathVariable long id, @RequestBody(required = false) ScheduleStatusUpdateDTO body) {
		scheduleApiService.updateStatus(id, ObjectUtil.isNotNull(body) ? body : new ScheduleStatusUpdateDTO());
		return R.phpOk("ok");
	}

}
