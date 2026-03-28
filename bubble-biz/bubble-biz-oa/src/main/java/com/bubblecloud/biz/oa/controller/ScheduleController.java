package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Phase 4：日程（占位）。
 *
 * @author qinlei
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/schedule")
@Tag(name = "日程")
public class ScheduleController {

	@GetMapping("/page")
	@Operation(summary = "日程分页")
	public PhpResponse<SimplePageVO> page(@RequestParam(defaultValue = "1") Integer current,
										  @RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

}
