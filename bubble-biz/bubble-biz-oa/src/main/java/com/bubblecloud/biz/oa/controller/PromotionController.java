package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.PromotionService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.dto.PromotionSaveDTO;
import com.bubblecloud.oa.api.entity.Promotion;
import com.bubblecloud.oa.api.vo.SimplePageVO;
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
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

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
			@RequestParam(defaultValue = "1") long current, @RequestParam(defaultValue = "20") long size) {
		Page<Promotion> r = promotionService.pagePromotions(current, size, status);
		return PhpResponse.ok(SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords()));
	}

	@PostMapping
	@Operation(summary = "晋升表保存")
	public PhpResponse<String> store(@RequestBody PromotionSaveDTO dto) {
		Promotion p = new Promotion();
		p.setName(dto.getName());
		p.setSort(ObjectUtil.isNull(dto.getSort()) ? 0 : dto.getSort());
		p.setStatus(1);
		promotionService.save(p);
		return PhpResponse.ok("common.insert.succ");
	}

	@GetMapping("/{id}")
	@Operation(summary = "晋升表状态/详情")
	public PhpResponse<Promotion> show(@PathVariable long id) {
		Promotion p = promotionService.getById(id);
		if (ObjectUtil.isNull(p) || ObjectUtil.isNotNull(p.getDeletedAt())) {
			return PhpResponse.failed("common.operation.noExists");
		}
		return PhpResponse.ok(p);
	}

	@PutMapping("/{id}")
	@Operation(summary = "晋升表修改")
	public PhpResponse<String> update(@PathVariable long id, @RequestBody PromotionSaveDTO dto) {
		Promotion existing = promotionService.getById(id);
		if (ObjectUtil.isNull(existing) || ObjectUtil.isNotNull(existing.getDeletedAt())) {
			return PhpResponse.failed("common.operation.noExists");
		}
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (ObjectUtil.isNotNull(dto.getSort())) {
			existing.setSort(dto.getSort());
		}
		promotionService.updateById(existing);
		return PhpResponse.ok("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "晋升表删除（软删）")
	public PhpResponse<String> destroy(@PathVariable long id) {
		Promotion existing = promotionService.getById(id);
		if (ObjectUtil.isNull(existing) || ObjectUtil.isNotNull(existing.getDeletedAt())) {
			return PhpResponse.failed("common.operation.noExists");
		}
		promotionService.removeById(id);
		return PhpResponse.ok("common.delete.succ");
	}

}
