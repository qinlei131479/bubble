package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AssessTargetCategoryService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.AssessTargetCateSaveDTO;
import com.bubblecloud.oa.api.entity.AssessTargetCategory;
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
 * 绩效指标分类（对齐 PHP {@code ent/assess/target_cate}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/assess/target_cate")
@Tag(name = "绩效指标分类")
public class AssessTargetCategoryController {

	private final AssessTargetCategoryService assessTargetCategoryService;

	@GetMapping({"", "/page"})
	@Operation(summary = "指标分类列表")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject AssessTargetCategory query) {
		Page<AssessTargetCategory> res = assessTargetCategoryService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@GetMapping("/create/{types}")
	@Operation(summary = "获取创建表单（按类型）")
	public R<Void> createForm(@PathVariable String types) {
		return R.phpOk(null);
	}

	@PostMapping
	@Operation(summary = "创建指标分类")
	public R<String> create(@RequestBody AssessTargetCateSaveDTO dto) {
		AssessTargetCategory obj = PojoConvertUtil.convertPojo(dto, AssessTargetCategory.class);
		assessTargetCategoryService.create(obj);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取指标分类详情")
	public R<AssessTargetCategory> details(@PathVariable Long id) {
		return R.phpOk(assessTargetCategoryService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改指标分类")
	public R<String> update(@PathVariable Long id, @RequestBody AssessTargetCateSaveDTO dto) {
		AssessTargetCategory obj = PojoConvertUtil.convertPojo(dto, AssessTargetCategory.class);
		obj.setId(id);
		assessTargetCategoryService.update(obj);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除指标分类")
	public R<String> removeById(@PathVariable Long id) {
		assessTargetCategoryService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
