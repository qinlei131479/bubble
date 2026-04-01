package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.RankJobService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.JobSaveDTO;
import com.bubblecloud.oa.api.dto.hr.JobSubordinateUpdateDTO;
import com.bubblecloud.oa.api.entity.RankJob;
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
 * 岗位（对齐 PHP {@code ent/jobs}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/jobs")
@Tag(name = "岗位")
public class JobController {

	private final RankJobService rankJobService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "岗位列表")
	public R<SimplePageVO> page(@ParameterObject Pg<RankJob> pg, @ParameterObject RankJob query) {
		return R.phpOk(rankJobService.pageJob(pg, query));
	}

	@GetMapping("/create")
	@Operation(summary = "获取创建表单")
	public R<Void> createForm() {
		return R.phpOk(null);
	}

	@PostMapping
	@Operation(summary = "创建岗位")
	public R<String> create(@RequestBody JobSaveDTO dto) {
		rankJobService.createJob(dto);
		return R.phpOk("common.insert.succ");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取岗位详情")
	public R<RankJob> details(@PathVariable long id) {
		return R.phpOk(rankJobService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改岗位")
	public R<String> update(@PathVariable long id, @RequestBody JobSaveDTO dto) {
		rankJobService.updateJob(id, dto);
		return R.phpOk("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除岗位")
	public R<String> removeById(@PathVariable long id) {
		rankJobService.removeJob(id);
		return R.phpOk("common.delete.succ");
	}

	@PutMapping("/show/{id}/{status}")
	@Operation(summary = "修改岗位状态")
	public R<String> updateStatus(@PathVariable long id, @PathVariable int status) {
		rankJobService.updateJobStatus(id, status);
		return R.phpOk("common.operation.succ");
	}

	@GetMapping("/select")
	@Operation(summary = "岗位下拉列表")
	public R<List<RankJob>> select(@RequestParam(required = false) Long entid) {
		return R.phpOk(rankJobService.selectList(entid));
	}

	@GetMapping("/subordinate")
	@Operation(summary = "下级岗位职责列表")
	public R<SimplePageVO> subordinate(@ParameterObject Pg<RankJob> pg, @ParameterObject RankJob query) {
		return R.phpOk(rankJobService.pageJob(pg, query));
	}

	@GetMapping("/subordinate/{id}")
	@Operation(summary = "获取下级职责详情")
	public R<RankJob> subordinateDetail(@PathVariable long id) {
		return R.phpOk(rankJobService.getSubordinateDetail(id));
	}

	@PutMapping("/subordinate/{id}")
	@Operation(summary = "修改下级职责")
	public R<String> updateSubordinate(@PathVariable long id, @RequestBody JobSubordinateUpdateDTO dto) {
		rankJobService.updateSubordinate(id, dto);
		return R.phpOk("common.update.succ");
	}

}
