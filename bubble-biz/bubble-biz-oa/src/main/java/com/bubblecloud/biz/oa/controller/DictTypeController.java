package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.DictTypeService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.entity.DictType;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据字典类型（对齐 PHP {@code ent/config/dict_type} 列表分页）。
 *
 * @author qinlei
 * @date 2026/3/29 下午12:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/config/dict_type")
@Tag(name = "字典类型")
public class DictTypeController {

	private final DictTypeService dictTypeService;

	@GetMapping
	@Operation(summary = "字典类型分页列表")
	public PhpResponse<SimplePageVO> index(@RequestParam(defaultValue = "1") long current,
										   @RequestParam(defaultValue = "20") long size) {
		Page<DictType> page = new Page<>(current, size);
		Page<DictType> r = dictTypeService.page(page,
				Wrappers.lambdaQuery(DictType.class).orderByDesc(DictType::getId));
		return PhpResponse.ok(SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords()));
	}

}
