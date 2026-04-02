package com.bubblecloud.biz.oa.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AssessPlanService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
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
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject AssessPlan query) {
		Page<AssessPlan> res = assessPlanService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取考核计划详情")
	public R<AssessPlan> details(@PathVariable Long id) {
		return R.phpOk(assessPlanService.getById(id));
	}

	@PostMapping
	@Operation(summary = "创建考核计划")
	public R<String> create(@RequestBody AssessPlanSaveDTO dto) {
		AssessPlan obj = PojoConvertUtil.convertPojo(dto, AssessPlan.class);
		if (CollUtil.isNotEmpty(dto.getUserIds())) {
			obj.setUserIds(dto.getUserIds().stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]")));
		}
		assessPlanService.create(obj);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改考核计划")
	public R<String> update(@PathVariable Long id, @RequestBody AssessPlanSaveDTO dto) {
		AssessPlan obj = PojoConvertUtil.convertPojo(dto, AssessPlan.class);
		obj.setId(id);
		if (CollUtil.isNotEmpty(dto.getUserIds())) {
			obj.setUserIds(dto.getUserIds().stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]")));
		}
		assessPlanService.update(obj);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除考核计划")
	public R<String> removeById(@PathVariable Long id) {
		assessPlanService.removeById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/enabled")
	@Operation(summary = "已启用周期列表")
	public R<List<AssessPlan>> enabled(@RequestParam(required = false) Long entid) {
		List<AssessPlan> list = assessPlanService.list(Wrappers.lambdaQuery(AssessPlan.class)
			.eq(ObjectUtil.isNotNull(entid), AssessPlan::getEntid, entid)
			.eq(AssessPlan::getStatus, 1)
			.isNull(AssessPlan::getDeletedAt)
			.orderByDesc(AssessPlan::getId));
		return R.phpOk(list);
	}

	@GetMapping("/users/{id}")
	@Operation(summary = "计划选中人员列表")
	public R<List<String>> planUsers(@PathVariable Long id) {
		AssessPlan detail = assessPlanService.getById(id);
		if (ObjectUtil.isNull(detail) || StrUtil.isBlank(detail.getUserIds())) {
			return R.phpOk(Collections.emptyList());
		}
		return R.phpOk(Collections.singletonList(detail.getUserIds()));
	}

}
