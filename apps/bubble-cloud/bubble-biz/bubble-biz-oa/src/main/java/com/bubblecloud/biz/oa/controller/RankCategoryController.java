package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.RankCategoryService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.RankCateSaveDTO;
import com.bubblecloud.oa.api.entity.RankCategory;
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
 * 职级体系分类（对齐 PHP {@code ent/rank_cate}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/rank_cate")
@Tag(name = "职级体系分类")
public class RankCategoryController {

	private final RankCategoryService rankCategoryService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "职级体系分类列表")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject RankCategory query) {
		Page<RankCategory> res = rankCategoryService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@PostMapping
	@Operation(summary = "创建职级体系分类")
	public R<String> create(@RequestBody RankCateSaveDTO dto) {
		RankCategory obj = PojoConvertUtil.convertPojo(dto, RankCategory.class);
		rankCategoryService.create(obj);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取职级体系分类详情")
	public R<RankCategory> details(@PathVariable Long id) {
		return R.phpOk(rankCategoryService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改职级体系分类")
	public R<String> update(@PathVariable Long id, @RequestBody RankCateSaveDTO dto) {
		RankCategory obj = PojoConvertUtil.convertPojo(dto, RankCategory.class);
		obj.setId(id);
		rankCategoryService.update(obj);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除职级体系分类")
	public R<String> removeById(@PathVariable Long id) {
		rankCategoryService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
