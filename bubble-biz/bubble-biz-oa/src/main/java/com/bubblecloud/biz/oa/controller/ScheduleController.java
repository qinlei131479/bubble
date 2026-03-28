package com.bubblecloud.biz.oa.controller;

import java.util.List;
import java.util.Map;

import com.bubblecloud.biz.oa.service.ScheduleApiService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.SimplePageVO;
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
	public PhpResponse<SimplePageVO> page(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

	@GetMapping("/types")
	@Operation(summary = "日程类型列表")
	public PhpResponse<List<Map<String, Object>>> types() {
		return PhpResponse.ok(scheduleApiService.typeList());
	}

	@PostMapping("/index")
	@Operation(summary = "日程列表")
	public PhpResponse<List<Map<String, Object>>> index(@RequestBody(required = false) Map<String, Object> body) {
		return PhpResponse.ok(scheduleApiService.scheduleIndex(body == null ? Map.of() : body));
	}

	@PutMapping("/status/{id}")
	@Operation(summary = "修改日程状态")
	public PhpResponse<String> status(@PathVariable long id,
			@RequestBody(required = false) Map<String, Object> body) {
		scheduleApiService.updateStatus(id, body == null ? Map.of() : body);
		return PhpResponse.ok("ok");
	}

}
