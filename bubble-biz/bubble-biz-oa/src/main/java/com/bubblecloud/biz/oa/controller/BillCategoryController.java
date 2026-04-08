package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.BillCategoryService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.finance.BillCategorySaveDTO;
import com.bubblecloud.oa.api.entity.BillCategory;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
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
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

/**
 * 财务流水分类（PHP {@code ent/bill_cate}，W-23）。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/bill_cate")
@Tag(name = "财务流水分类")
public class BillCategoryController {

	private final BillCategoryService billCategoryService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "分类列表")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject BillCategory query) {
		Page<BillCategory> res = billCategoryService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@GetMapping("/create")
	@Operation(summary = "创建表单（elForm 对齐）")
	public R<OaElFormVO> createForm(@RequestParam(defaultValue = "1") long entid) {
		return R.phpOk(billCategoryService.buildCategoryCreateForm(entid));
	}

	@PostMapping
	@Operation(summary = "保存分类")
	public R<String> store(@RequestBody BillCategorySaveDTO dto) {
		try {
			BillCategory e = new BillCategory();
			e.setName(dto.getName());
			e.setTypes(ObjectUtil.defaultIfNull(dto.getTypes(), 0));
			e.setEntid(ObjectUtil.defaultIfNull(dto.getEntid(), 1L));
			e.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
			e.setPath(buildPath(dto.getPath(), dto.getPid()));
			billCategoryService.create(e);
			return R.phpOk(OaConstants.INSERT_SUCC);
		}
		catch (Exception ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑表单（elForm 对齐）")
	public R<OaElFormVO> editForm(@PathVariable Long id, @RequestParam(defaultValue = "1") long entid) {
		return R.phpOk(billCategoryService.buildCategoryEditForm(id, entid));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改分类")
	public R<String> update(@PathVariable Long id, @RequestBody BillCategorySaveDTO dto) {
		try {
			BillCategory e = new BillCategory();
			e.setId(id);
			e.setName(dto.getName());
			e.setTypes(ObjectUtil.defaultIfNull(dto.getTypes(), 0));
			e.setEntid(ObjectUtil.defaultIfNull(dto.getEntid(), 1L));
			e.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
			e.setPath(buildPath(dto.getPath(), dto.getPid()));
			billCategoryService.update(e);
			return R.phpOk(OaConstants.UPDATE_SUCC);
		}
		catch (Exception ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除分类")
	public R<String> destroy(@PathVariable Long id) {
		try {
			billCategoryService.removeById(id);
			return R.phpOk(OaConstants.DELETE_SUCC);
		}
		catch (Exception ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	private static String buildPath(java.util.List<Long> pathIds, Integer pidFallback) {
		if (CollUtil.isNotEmpty(pathIds)) {
			StringBuilder sb = new StringBuilder("/");
			for (Long x : pathIds) {
				sb.append(x).append("/");
			}
			return sb.toString();
		}
		if (ObjectUtil.isNotNull(pidFallback) && pidFallback > 0) {
			return "/" + pidFallback + "/";
		}
		return "";
	}

}
