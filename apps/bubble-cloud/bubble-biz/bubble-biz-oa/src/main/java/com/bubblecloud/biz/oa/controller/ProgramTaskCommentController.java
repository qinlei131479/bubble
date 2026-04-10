package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.ProgramTaskCommentOaService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.program.ProgramTaskCommentSaveDTO;
import com.bubblecloud.oa.api.vo.CreatedIdVO;
import com.bubblecloud.oa.api.vo.program.ProgramCommentListVO;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
 * 任务评论（PHP {@code ent/task_comment}）。
 *
 * @author qinlei
 * @date 2026/4/8 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/task_comment")
@Tag(name = "任务评论")
public class ProgramTaskCommentController {

	private final ProgramTaskCommentOaService programTaskCommentOaService;

	@GetMapping({ "", "/" })
	@Operation(summary = "评论列表")
	public R<ProgramCommentListVO> index(@RequestParam Long taskId) {
		return R.phpOk(programTaskCommentOaService.listByTask(taskId));
	}

	@PostMapping
	@Operation(summary = "保存评论")
	public R<CreatedIdVO> store(@RequestBody ProgramTaskCommentSaveDTO dto) {
		Long op = OaSecurityUtil.currentUserId();
		return R.phpOk(programTaskCommentOaService.save(dto, ObjectUtil.defaultIfNull(op, 0L)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改评论")
	public R<String> update(@PathVariable Long id, @RequestBody ProgramTaskCommentSaveDTO dto) {
		Long op = OaSecurityUtil.currentUserId();
		programTaskCommentOaService.update(id, dto, ObjectUtil.defaultIfNull(op, 0L));
		return R.phpOk("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除评论")
	public R<String> destroy(@PathVariable Long id) {
		Long op = OaSecurityUtil.currentUserId();
		programTaskCommentOaService.delete(id, ObjectUtil.defaultIfNull(op, 0L));
		return R.phpOk("common.delete.succ");
	}

}
