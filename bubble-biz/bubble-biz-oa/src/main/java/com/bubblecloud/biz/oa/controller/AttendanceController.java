package com.bubblecloud.biz.oa.controller;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.vo.placeholder.LabelValueOptionVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 考勤公共路由（统计/打卡与 PHP ent/attendance 同前缀；复杂算法后续按 W-13 补全）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/attendance")
@Tag(name = "考勤管理")
public class AttendanceController {

	private final ObjectMapper objectMapper;

	@GetMapping("/abnormal_date")
	@Operation(summary = "考勤异常日期列表")
	public R<List<LabelValueOptionVO>> abnormalDate() {
		return R.phpOk(Collections.emptyList());
	}

	@GetMapping("/daily_statistics")
	@Operation(summary = "考勤每日统计（占位）")
	public R<JsonNode> dailyStatistics() {
		return R.phpOk(emptyStatsNode());
	}

	@GetMapping("/monthly_statistics")
	@Operation(summary = "考勤月度统计（占位）")
	public R<JsonNode> monthlyStatistics() {
		return R.phpOk(emptyStatsNode());
	}

	@GetMapping("/attendance_statistics")
	@Operation(summary = "出勤统计（占位）")
	public R<JsonNode> attendanceStatistics() {
		return R.phpOk(emptyStatsNode());
	}

	@GetMapping("/individual_statistics")
	@Operation(summary = "个人统计（占位）")
	public R<JsonNode> individualStatistics() {
		return R.phpOk(emptyStatsNode());
	}

	@GetMapping("/clock_record")
	@Operation(summary = "打卡记录（占位）")
	public R<JsonNode> clockRecord() {
		return R.phpOk(objectMapper.createObjectNode().set("list", objectMapper.createArrayNode()));
	}

	@GetMapping("/clock_record/{id}")
	@Operation(summary = "打卡详情（占位）")
	public R<JsonNode> clockRecordInfo(@PathVariable Long id) {
		return R.phpOk(objectMapper.createObjectNode());
	}

	@GetMapping("/abnormal_record/{id}")
	@Operation(summary = "异常记录列表（占位）")
	public R<JsonNode> abnormalRecord(@PathVariable Long id) {
		return R.phpOk(objectMapper.createArrayNode());
	}

	@PostMapping("/clock/import_record")
	@Operation(summary = "导入打卡（占位成功）")
	public R<String> importRecord() {
		return R.phpOk("导入成功");
	}

	@PostMapping("/clock/import_third")
	@Operation(summary = "导入三方打卡（占位成功）")
	public R<String> importThird() {
		return R.phpOk("导入成功");
	}

	@PutMapping("/statistics/{id}")
	@Operation(summary = "添加处理记录（占位）")
	public R<String> statisticsUpdate(@PathVariable Long id) {
		return R.phpOk("common.update.succ");
	}

	@GetMapping("/statistics/{id}")
	@Operation(summary = "处理记录（占位）")
	public R<JsonNode> statisticsRecord(@PathVariable Long id) {
		return R.phpOk(objectMapper.createArrayNode());
	}

	private ObjectNode emptyStatsNode() {
		return objectMapper.createObjectNode().set("list", objectMapper.createArrayNode());
	}

}
