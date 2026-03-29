package com.bubblecloud.biz.oa.controller;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.placeholder.DailyReportMemberVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日报/汇报（占位，与 PHP {@code ent/daily} 对齐）。
 *
 * @author qinlei
 * @date 2026/3/28 14:00
 */
@RestController
@RequestMapping("/ent/daily")
@Tag(name = "日报汇报")
public class DailyController {

	/**
	 * 与 PHP {@code ent/daily/report_member} 一致：默认汇报人列表 {@code [{card: {...}}, ...]}。
	 */
	@GetMapping("/report_member")
	@Operation(summary = "默认汇报人")
	public PhpResponse<List<DailyReportMemberVO>> reportMember() {
		return PhpResponse.ok(Collections.emptyList());
	}

}
