package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.ProgramTaskOaService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.program.ProgramTaskBatchDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskIndexQuery;
import com.bubblecloud.oa.api.dto.program.ProgramTaskSortDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskStoreDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskSubordinateDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskUpdateDTO;
import com.bubblecloud.oa.api.entity.ProgramTask;
import com.bubblecloud.oa.api.vo.CreatedIdVO;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.program.ProgramTaskNodeVO;
import com.bubblecloud.oa.api.vo.program.ProgramTaskSelectNodeVO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
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

import java.util.List;

/**
 * 项目任务（PHP {@code ent/program_task}）。
 *
 * @author qinlei
 * @date 2026/4/8 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/program_task")
@Tag(name = "项目任务")
public class ProgramTaskController {

	private final ProgramTaskOaService programTaskOaService;

	@GetMapping({ "", "/" })
	@Operation(summary = "任务列表")
	public R<ListCountVO<ProgramTaskNodeVO>> index(@ParameterObject Pg pg, @ParameterObject ProgramTaskIndexQuery qq) {
		Long op = OaSecurityUtil.currentUserId();
		ProgramTask query = toTaskQuery(qq);
		return R.phpOk(programTaskOaService.pageTaskTree(pg, query, ObjectUtil.defaultIfNull(op, 0L)));
	}

	@PostMapping
	@Operation(summary = "保存任务")
	public R<CreatedIdVO> store(@RequestBody ProgramTaskStoreDTO dto) {
		Long op = OaSecurityUtil.currentUserId();
		return R.phpOk(programTaskOaService.store(dto, ObjectUtil.defaultIfNull(op, 0L)));
	}

	@PostMapping("/subordinate")
	@Operation(summary = "保存下级任务")
	public R<CreatedIdVO> subordinateStore(@RequestBody ProgramTaskSubordinateDTO dto) {
		Long op = OaSecurityUtil.currentUserId();
		return R.phpOk(programTaskOaService.saveSubordinate(dto, ObjectUtil.defaultIfNull(op, 0L)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改任务（单字段 field 在 JSON 内）")
	public R<String> update(@PathVariable Long id, @RequestBody ProgramTaskUpdateDTO dto) {
		Long op = OaSecurityUtil.currentUserId();
		programTaskOaService.update(id, dto, ObjectUtil.defaultIfNull(op, 0L));
		return R.phpOk("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除任务")
	public R<String> destroy(@PathVariable Long id) {
		Long op = OaSecurityUtil.currentUserId();
		programTaskOaService.delete(id, ObjectUtil.defaultIfNull(op, 0L));
		return R.phpOk("common.delete.succ");
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "任务详情")
	public R<ProgramTaskNodeVO> info(@PathVariable Long id) {
		Long op = OaSecurityUtil.currentUserId();
		return R.phpOk(programTaskOaService.info(id, ObjectUtil.defaultIfNull(op, 0L)));
	}

	@GetMapping("/select")
	@Operation(summary = "任务下拉")
	public R<List<ProgramTaskSelectNodeVO>> select(@RequestParam(required = false) Long programId,
			@RequestParam(required = false) Long pid) {
		Long op = OaSecurityUtil.currentUserId();
		return R.phpOk(programTaskOaService.select(programId, pid, ObjectUtil.defaultIfNull(op, 0L)));
	}

	@PostMapping("/batch")
	@Operation(summary = "批量更新")
	public R<String> batchUpdate(@RequestBody ProgramTaskBatchDTO dto) {
		Long op = OaSecurityUtil.currentUserId();
		programTaskOaService.batchUpdate(dto, ObjectUtil.defaultIfNull(op, 0L));
		return R.phpOk("common.operation.succ");
	}

	@PostMapping("/batch_del")
	@Operation(summary = "批量删除")
	public R<String> batchDel(@RequestBody ProgramTaskBatchDTO dto) {
		Long op = OaSecurityUtil.currentUserId();
		programTaskOaService.batchDelete(dto.getData(), ObjectUtil.defaultIfNull(op, 0L));
		return R.phpOk("common.operation.succ");
	}

	@PostMapping("/sort")
	@Operation(summary = "排序")
	public R<String> sort(@RequestBody ProgramTaskSortDTO dto) {
		Long op = OaSecurityUtil.currentUserId();
		programTaskOaService.sort(dto, ObjectUtil.defaultIfNull(op, 0L));
		return R.phpOk("common.operation.succ");
	}

	@GetMapping("/share/{ident}")
	@Operation(summary = "分享详情")
	public R<ProgramTaskNodeVO> share(@PathVariable String ident) {
		Long op = OaSecurityUtil.currentUserId();
		return R.phpOk(programTaskOaService.share(ident, ObjectUtil.defaultIfNull(op, 0L)));
	}

	private static ProgramTask toTaskQuery(ProgramTaskIndexQuery qq) {
		if (qq == null) {
			return new ProgramTask();
		}
		ProgramTask t = new ProgramTask();
		BeanUtil.copyProperties(qq, t);
		return t;
	}

}
