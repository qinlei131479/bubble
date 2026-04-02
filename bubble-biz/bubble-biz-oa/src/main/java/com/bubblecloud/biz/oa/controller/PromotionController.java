package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.PromotionService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.PromotionSaveDTO;
import com.bubblecloud.oa.api.entity.Promotion;
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
 * 晋升表（对齐 PHP {@code ent/company/promotions}）。业务见 {@link PromotionService}。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/company/promotions")
@Tag(name = "晋升表")
public class PromotionController {

	private final PromotionService promotionService;

	@GetMapping(value = {"", "/page"})
	@Operation(summary = "晋升表列表")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject Promotion query) {
		Page<Promotion> res = promotionService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@PostMapping
	@Operation(summary = "晋升表保存")
	public R<String> create(@RequestBody PromotionSaveDTO dto) {
		Promotion obj = PojoConvertUtil.convertPojo(dto, Promotion.class);
		promotionService.create(obj);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}")
	@Operation(summary = "晋升表状态/详情")
	public R<Promotion> details(@PathVariable Long id) {
		return R.phpOk(promotionService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "晋升表修改")
	public R<String> update(@PathVariable Long id, @RequestBody PromotionSaveDTO dto) {
		Promotion obj = PojoConvertUtil.convertPojo(dto, Promotion.class);
		obj.setId(id);
		promotionService.update(obj);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "晋升表删除（软删）")
	public R<String> removeById(@PathVariable Long id) {
		promotionService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
