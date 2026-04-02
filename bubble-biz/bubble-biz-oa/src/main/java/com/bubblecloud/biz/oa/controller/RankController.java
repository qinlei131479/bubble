package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.RankService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
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
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject Rank query) {
		Page<Rank> res = rankService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@GetMapping("/create")
	@Operation(summary = "获取创建表单")
	public R<Void> createForm() {
		return R.phpOk(null);
	}

	@PostMapping
	@Operation(summary = "创建职级")
	public R<String> create(@RequestBody RankSaveDTO dto) {
		Rank obj = PojoConvertUtil.convertPojo(dto, Rank.class);
		rankService.create(obj);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取职级详情")
	public R<Rank> details(@PathVariable Long id) {
		return R.phpOk(rankService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改职级")
	public R<String> update(@PathVariable Long id, @RequestBody RankSaveDTO dto) {
		Rank obj = PojoConvertUtil.convertPojo(dto, Rank.class);
		obj.setId(id);
		rankService.update(obj);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除职级")
	public R<String> removeById(@PathVariable Long id) {
		rankService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
