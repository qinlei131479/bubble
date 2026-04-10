package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.DictDataService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.config.DictDataTreeQueryDTO;
import com.bubblecloud.oa.api.entity.DictData;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.config.DictDataTreeNodeVO;
import com.bubblecloud.oa.api.vo.config.DictTypeStoreResultVO;
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
 * 数据字典项，对齐 PHP {@code ent/config/dict_data}。
 *
 * @author qinlei
 * @date 2026/3/30 下午10:20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/config/dict_data")
@Tag(name = "字典数据")
public class DictDataController {

	private final DictDataService dictDataService;

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "字典数据分页")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @RequestParam(required = false) String name,
			@RequestParam(required = false) String types, @RequestParam(required = false) Integer type_id,
			@RequestParam(required = false) Integer status) {
		DictData query = new DictData();
		query.setName(name);
		query.setTypeName(types);
		query.setTypeId(type_id);
		query.setStatus(status);
		Page<DictData> r = dictDataService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords()));
	}

	@GetMapping("/create")
	@Operation(summary = "新增字典数据默认值")
	public R<DictData> createForm() {
		DictData d = new DictData();
		d.setStatus(1);
		d.setLevel(1);
		return R.phpOk(d);
	}

	@PostMapping
	@Operation(summary = "保存字典数据")
	public R<DictTypeStoreResultVO> create(@RequestBody DictData req) {
		dictDataService.save(req);
		return R.phpOk(new DictTypeStoreResultVO(req.getId()));
	}

	@GetMapping("/{id}")
	@Operation(summary = "显示/隐藏字典数据")
	public R<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
		DictData u = new DictData();
		u.setId(id);
		u.setStatus(status);
		dictDataService.update(u);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取修改字典数据")
	public R<DictData> details(@PathVariable Long id) {
		return R.phpOk(dictDataService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改字典数据")
	public R<String> update(@PathVariable Long id, @RequestBody DictData req) {
		req.setId(id);
		dictDataService.update(req);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除字典数据")
	public R<String> removeById(@PathVariable Long id) {
		dictDataService.removeById(id);
		return R.phpOk("删除成功");
	}

	@PostMapping("/tree")
	@Operation(summary = "字典数据树形结构")
	public R<List<DictDataTreeNodeVO>> tree(@RequestBody(required = false) DictDataTreeQueryDTO query) {
		return R.phpOk(dictDataService.treeDictData(query));
	}

}
