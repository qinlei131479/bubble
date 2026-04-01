package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.AssessPlanService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.AssessPlanSaveDTO;
import com.bubblecloud.oa.api.entity.AssessPlan;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绩效考核计划（对齐 PHP {@code ent/assess/plan}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/assess/plan")
@Tag(name = "绩效考核计划")
public class AssessPlanController {

	private final AssessPlanService assessPlanService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "考核计划列表")
	public R<SimplePageVO> page(@ParameterObject Pg<AssessPlan> pg, @ParameterObject AssessPlan query) {
		return R.phpOk(assessPlanService.pagePlan(pg, query));
	}

	@PostMapping
	@Operation(summary = "创建考核计划")
	public R<String> create(@RequestBody AssessPlanSaveDTO dto) {
		assessPlanService.createPlan(dto);
		return R.phpOk("common.insert.succ");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取考核计划详情")
	public R<AssessPlan> details(@PathVariable long id) {
		return R.phpOk(assessPlanService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改考核计划")
	public R<String> update(@PathVariable long id, @RequestBody AssessPlanSaveDTO dto) {
		assessPlanService.updatePlan(id, dto);
		return R.phpOk("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除考核计划")
	public R<String> removeById(@PathVariable long id) {
		assessPlanService.removePlan(id);
		return R.phpOk("common.delete.succ");
	}

	@GetMapping("/enabled")
	@Operation(summary = "已启用周期列表")
	public R<List<AssessPlan>> enabledPlans(@RequestParam(required = false) Long entid) {
		return R.phpOk(assessPlanService.enabledPlans(entid));
	}

	@GetMapping("/users/{id}")
	@Operation(summary = "计划选中人员列表")
	public R<List<Object>> planUsers(@PathVariable long id) {
		return R.phpOk(assessPlanService.planUsers(id));
	}

}
