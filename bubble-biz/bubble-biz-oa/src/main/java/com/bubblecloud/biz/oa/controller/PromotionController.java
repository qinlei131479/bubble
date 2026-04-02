package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.PromotionService;
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
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "晋升表列表")
	public R<SimplePageVO> page(@ParameterObject Pg<Promotion> pg, @RequestParam(required = false) Integer status) {
		return R.phpOk(promotionService.pagePromotion(pg, status));
	}

	@PostMapping
	@Operation(summary = "晋升表保存")
	public R<String> create(@RequestBody PromotionSaveDTO dto) {
		promotionService.createPromotion(dto);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}")
	@Operation(summary = "晋升表状态/详情")
	public R<Promotion> details(@PathVariable Long id) {
		return R.phpOk(promotionService.getPromotionDetail(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "晋升表修改")
	public R<String> update(@PathVariable Long id, @RequestBody PromotionSaveDTO dto) {
		promotionService.updatePromotion(id, dto);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "晋升表删除（软删）")
	public R<String> removeById(@PathVariable Long id) {
		promotionService.removePromotion(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
