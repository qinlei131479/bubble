package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.HayGroupService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.HayGroupSaveDTO;
import com.bubblecloud.oa.api.entity.HayGroup;
import com.bubblecloud.oa.api.entity.HayGroupData;
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
 * 海氏评估组（对齐 PHP {@code ent/company/evaluate}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/company/evaluate")
@Tag(name = "海氏评估组")
public class HayGroupController {

	private final HayGroupService hayGroupService;

	@GetMapping({"", "/page"})
	@Operation(summary = "海氏评估组列表")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject HayGroup query) {
		Page<HayGroup> res = hayGroupService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@PostMapping
	@Operation(summary = "创建海氏评估组")
	public R<String> create(@RequestBody HayGroupSaveDTO dto) {
		HayGroup obj = PojoConvertUtil.convertPojo(dto, HayGroup.class);
		hayGroupService.create(obj);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取评估组详情")
	public R<HayGroup> details(@PathVariable Long id) {
		return R.phpOk(hayGroupService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改海氏评估组")
	public R<String> update(@PathVariable Long id, @RequestBody HayGroupSaveDTO dto) {
		HayGroup obj = PojoConvertUtil.convertPojo(dto, HayGroup.class);
		obj.setId(id);
		hayGroupService.update(obj);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除海氏评估组")
	public R<String> removeById(@PathVariable Long id) {
		hayGroupService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/data/{group_id}")
	@Operation(summary = "评估表数据列表")
	public R<List<HayGroupData>> dataList(@PathVariable("group_id") Long groupId) {
		return R.phpOk(hayGroupService.dataList(groupId));
	}

	@GetMapping("/history/{group_id}")
	@Operation(summary = "评估表历史记录")
	public R<List<HayGroupData>> historyList(@PathVariable("group_id") Long groupId) {
		return R.phpOk(hayGroupService.historyList(groupId));
	}

}
