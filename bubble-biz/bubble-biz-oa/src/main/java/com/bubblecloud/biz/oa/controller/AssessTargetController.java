package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.AssessTargetService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.AssessTargetSaveDTO;
import com.bubblecloud.oa.api.entity.AssessTarget;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * 绩效指标（对齐 PHP {@code ent/assess/target}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/assess/target")
@Tag(name = "绩效指标")
public class AssessTargetController {

	private final AssessTargetService assessTargetService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "绩效指标列表")
	public R<SimplePageVO> page(@ParameterObject Pg<AssessTarget> pg, @ParameterObject AssessTarget query) {
		return R.phpOk(assessTargetService.pageTarget(pg, query));
	}

	@GetMapping("/create")
	@Operation(summary = "获取创建表单")
	public R<Void> createForm() {
		return R.phpOk(null);
	}

	@PostMapping
	@Operation(summary = "创建绩效指标")
	public R<String> create(@RequestBody AssessTargetSaveDTO dto) {
		assessTargetService.createTarget(dto);
		return R.phpOk("common.insert.succ");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取绩效指标详情")
	public R<AssessTarget> details(@PathVariable long id) {
		return R.phpOk(assessTargetService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改绩效指标")
	public R<String> update(@PathVariable long id, @RequestBody AssessTargetSaveDTO dto) {
		assessTargetService.updateTarget(id, dto);
		return R.phpOk("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除绩效指标")
	public R<String> removeById(@PathVariable long id) {
		assessTargetService.removeTarget(id);
		return R.phpOk("common.delete.succ");
	}

}
