package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.RankLevelService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.RankLevelBatchUpdateDTO;
import com.bubblecloud.oa.api.dto.hr.RankLevelSaveDTO;
import com.bubblecloud.oa.api.entity.Rank;
import com.bubblecloud.oa.api.entity.RankLevel;
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
 * 职位等级（对齐 PHP {@code ent/rank_level}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/rank_level")
@Tag(name = "职位等级")
public class RankLevelController {

	private final RankLevelService rankLevelService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "职位等级列表")
	public R<SimplePageVO> page(@ParameterObject Pg<RankLevel> pg, @ParameterObject RankLevel query) {
		return R.phpOk(rankLevelService.pageRankLevel(pg, query));
	}

	@PostMapping
	@Operation(summary = "创建职位等级")
	public R<String> create(@RequestBody RankLevelSaveDTO dto) {
		rankLevelService.createRankLevel(dto);
		return R.phpOk("common.insert.succ");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取职位等级详情")
	public R<RankLevel> details(@PathVariable Long id) {
		return R.phpOk(rankLevelService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改职位等级")
	public R<String> update(@PathVariable Long id, @RequestBody RankLevelSaveDTO dto) {
		rankLevelService.updateRankLevel(id, dto);
		return R.phpOk("common.update.succ");
	}

	@PutMapping("/batch/{batch}")
	@Operation(summary = "批量修改职位等级")
	public R<String> batchUpdate(@PathVariable String batch, @RequestBody RankLevelBatchUpdateDTO dto) {
		rankLevelService.batchUpdateRankLevel(batch, dto);
		return R.phpOk("common.operation.succ");
	}

	@PutMapping("/relation/{id}")
	@Operation(summary = "修改职位等级关联职级")
	public R<String> relateRank(@PathVariable Long id, @RequestParam Long rankId) {
		rankLevelService.relateRank(id, rankId);
		return R.phpOk("common.operation.succ");
	}

	@DeleteMapping("/relation/{id}")
	@Operation(summary = "删除关联职级")
	public R<String> removeRelation(@PathVariable Long id) {
		rankLevelService.removeRelateRank(id);
		return R.phpOk("common.delete.succ");
	}

	@GetMapping("/rank/{cate_id}")
	@Operation(summary = "获取未关联职级列表")
	public R<List<Rank>> unrelatedRanks(@PathVariable("cate_id") Long cateId,
			@RequestParam(required = false, defaultValue = "0") Long entid) {
		return R.phpOk(rankLevelService.unrelatedRanks(cateId, entid));
	}

}
