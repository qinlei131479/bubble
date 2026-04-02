package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.PromotionDataService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.PromotionDataSaveDTO;
import com.bubblecloud.oa.api.dto.hr.PromotionDataSortDTO;
import com.bubblecloud.oa.api.dto.hr.PromotionDataStandardDTO;
import com.bubblecloud.oa.api.entity.PromotionData;
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
 * 晋升数据项（对齐 PHP {@code ent/company/promotion/data}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/company/promotion/data")
@Tag(name = "晋升数据项")
public class PromotionDataController {

	private final PromotionDataService promotionDataService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "晋升数据项列表")
	public R<SimplePageVO> page(@ParameterObject Pg<PromotionData> pg, @ParameterObject PromotionData query) {
		return R.phpOk(promotionDataService.pagePromotionData(pg, query));
	}

	@PostMapping
	@Operation(summary = "创建晋升数据项")
	public R<String> create(@RequestBody PromotionDataSaveDTO dto) {
		promotionDataService.createPromotionData(dto);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取晋升数据项详情")
	public R<PromotionData> details(@PathVariable Long id) {
		return R.phpOk(promotionDataService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改晋升数据项")
	public R<String> update(@PathVariable Long id, @RequestBody PromotionDataSaveDTO dto) {
		promotionDataService.updatePromotionData(id, dto);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除晋升数据项")
	public R<String> removeById(@PathVariable Long id) {
		promotionDataService.removePromotionData(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@PostMapping("/standard/{id}")
	@Operation(summary = "标准修改")
	public R<String> updateStandard(@PathVariable Long id, @RequestBody PromotionDataStandardDTO dto) {
		promotionDataService.updateStandard(id, dto);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@PostMapping("/sort/{pid}")
	@Operation(summary = "数据项排序")
	public R<String> sort(@PathVariable Long pid, @RequestBody PromotionDataSortDTO dto) {
		promotionDataService.sortPromotionData(pid, dto);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

}
