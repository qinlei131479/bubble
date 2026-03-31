package com.bubblecloud.biz.oa.controller;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.placeholder.LabelValueOptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Phase 5：考勤（占位）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/attendance")
@Tag(name = "考勤管理")
public class AttendanceController {

	@GetMapping("/group/page")
	@Operation(summary = "考勤组分页")
	public R<SimplePageVO> groupPage(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return R.phpOk(SimplePageVO.empty(current, size));
	}

	/**
	 * 与 PHP {@code ent/attendance/abnormal_date} 一致：可补卡异常日期下拉
	 * {@code [{value,label},...]}。
	 */
	@GetMapping("/abnormal_date")
	@Operation(summary = "考勤异常日期列表")
	public R<List<LabelValueOptionVO>> abnormalDate() {
		return R.phpOk(Collections.emptyList());
	}

}
