package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.RankService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.RankSaveDTO;
import com.bubblecloud.oa.api.entity.Rank;
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
 * 职级（对齐 PHP {@code ent/rank}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/rank")
@Tag(name = "职级")
public class RankController {

	private final RankService rankService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "职级列表")
	public R<SimplePageVO> page(@ParameterObject Pg<Rank> pg, @ParameterObject Rank query) {
		return R.phpOk(rankService.pageRank(pg, query));
	}

	@GetMapping("/create")
	@Operation(summary = "获取创建表单")
	public R<Void> createForm() {
		return R.phpOk(null);
	}

	@PostMapping
	@Operation(summary = "创建职级")
	public R<String> create(@RequestBody RankSaveDTO dto) {
		rankService.createRank(dto);
		return R.phpOk("common.insert.succ");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取职级详情")
	public R<Rank> details(@PathVariable long id) {
		return R.phpOk(rankService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改职级")
	public R<String> update(@PathVariable long id, @RequestBody RankSaveDTO dto) {
		rankService.updateRank(id, dto);
		return R.phpOk("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除职级")
	public R<String> removeById(@PathVariable long id) {
		rankService.removeRank(id);
		return R.phpOk("common.delete.succ");
	}

}
