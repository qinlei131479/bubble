package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.PromotionService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.dto.PromotionSaveDTO;
import com.bubblecloud.oa.api.entity.Promotion;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
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
 * 晋升表（对齐 PHP {@code ent/company/promotions}）。
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

	@GetMapping
	@Operation(summary = "晋升表列表")
	public PhpResponse<SimplePageVO> index(@RequestParam(required = false) Integer status,
			@RequestParam(defaultValue = "1") long current,
			@RequestParam(defaultValue = "20") long size) {
		Page<Promotion> page = new Page<>(current, size);
		var q = Wrappers.lambdaQuery(Promotion.class).isNull(Promotion::getDeletedAt);
		if (status != null) {
			q.eq(Promotion::getStatus, status);
		}
		q.orderByAsc(Promotion::getSort).orderByDesc(Promotion::getId);
		Page<Promotion> r = promotionService.page(page, q);
		return PhpResponse.ok(SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords()));
	}

	@PostMapping
	@Operation(summary = "晋升表保存")
	public PhpResponse<String> store(@RequestBody PromotionSaveDTO dto) {
		Promotion p = new Promotion();
		p.setName(dto.getName());
		p.setSort(dto.getSort() == null ? 0 : dto.getSort());
		p.setStatus(1);
		promotionService.save(p);
		return PhpResponse.ok("common.insert.succ");
	}

	@GetMapping("/{id}")
	@Operation(summary = "晋升表状态/详情")
	public PhpResponse<Promotion> show(@PathVariable long id) {
		Promotion p = promotionService.getById(id);
		if (p == null || p.getDeletedAt() != null) {
			return PhpResponse.failed("common.operation.noExists");
		}
		return PhpResponse.ok(p);
	}

	@PutMapping("/{id}")
	@Operation(summary = "晋升表修改")
	public PhpResponse<String> update(@PathVariable long id, @RequestBody PromotionSaveDTO dto) {
		Promotion existing = promotionService.getById(id);
		if (existing == null || existing.getDeletedAt() != null) {
			return PhpResponse.failed("common.operation.noExists");
		}
		if (StringUtils.hasText(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (dto.getSort() != null) {
			existing.setSort(dto.getSort());
		}
		promotionService.updateById(existing);
		return PhpResponse.ok("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "晋升表删除（软删）")
	public PhpResponse<String> destroy(@PathVariable long id) {
		Promotion existing = promotionService.getById(id);
		if (existing == null || existing.getDeletedAt() != null) {
			return PhpResponse.failed("common.operation.noExists");
		}
		promotionService.removeById(id);
		return PhpResponse.ok("common.delete.succ");
	}

}
