package com.bubblecloud.biz.oa.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.DictDataService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.dto.config.DictDataTreeQueryDTO;
import com.bubblecloud.oa.api.entity.DictData;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.config.DictDataTreeNodeVO;
import com.bubblecloud.oa.api.vo.config.DictTypeStoreResultVO;
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

	@GetMapping
	@Operation(summary = "字典数据分页")
	public PhpResponse<SimplePageVO> index(@RequestParam(defaultValue = "1") long current,
			@RequestParam(defaultValue = "20") long size, @RequestParam(required = false) String name,
			@RequestParam(required = false) String types, @RequestParam(required = false) Integer type_id,
			@RequestParam(required = false) Integer status) {
		var q = Wrappers.lambdaQuery(DictData.class);
		if (StringUtils.hasText(name)) {
			q.like(DictData::getName, name);
		}
		if (StringUtils.hasText(types)) {
			q.eq(DictData::getTypeName, types);
		}
		if (type_id != null) {
			q.eq(DictData::getTypeId, type_id);
		}
		if (status != null) {
			q.eq(DictData::getStatus, status);
		}
		q.orderByDesc(DictData::getSort).orderByDesc(DictData::getId);
		Page<DictData> page = new Page<>(current, size);
		Page<DictData> r = dictDataService.page(page, q);
		return PhpResponse.ok(SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords()));
	}

	@GetMapping("/create")
	@Operation(summary = "新增字典数据默认值")
	public PhpResponse<DictData> create() {
		DictData d = new DictData();
		d.setStatus(1);
		d.setLevel(1);
		return PhpResponse.ok(d);
	}

	@PostMapping
	@Operation(summary = "保存字典数据")
	public PhpResponse<DictTypeStoreResultVO> store(@RequestBody DictData body) {
		dictDataService.save(body);
		return PhpResponse.ok(new DictTypeStoreResultVO(body.getId()));
	}

	@GetMapping("/{id}")
	@Operation(summary = "显示/隐藏字典数据")
	public PhpResponse<String> show(@PathVariable Long id, @RequestParam Integer status) {
		DictData u = new DictData();
		u.setId(id);
		u.setStatus(status);
		dictDataService.updateById(u);
		return PhpResponse.ok("common.update.succ");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取修改字典数据")
	public PhpResponse<DictData> edit(@PathVariable Long id) {
		return PhpResponse.ok(dictDataService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改字典数据")
	public PhpResponse<String> update(@PathVariable Long id, @RequestBody DictData body) {
		body.setId(id);
		dictDataService.updateById(body);
		return PhpResponse.ok("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除字典数据")
	public PhpResponse<String> destroy(@PathVariable Long id) {
		dictDataService.removeById(id);
		return PhpResponse.ok("删除成功");
	}

	@PostMapping("/tree")
	@Operation(summary = "字典数据树形结构")
	public PhpResponse<List<DictDataTreeNodeVO>> tree(@RequestBody(required = false) DictDataTreeQueryDTO query) {
		var q = Wrappers.lambdaQuery(DictData.class);
		if (query != null) {
			if (query.getTypeId() != null) {
				q.eq(DictData::getTypeId, query.getTypeId());
			}
			if (StringUtils.hasText(query.getTypes())) {
				q.eq(DictData::getTypeName, query.getTypes());
			}
			if (StringUtils.hasText(query.getName())) {
				q.like(DictData::getName, query.getName());
			}
			if (query.getStatus() != null) {
				q.eq(DictData::getStatus, query.getStatus());
			}
		}
		q.orderByAsc(DictData::getSort).orderByAsc(DictData::getId);
		List<DictData> flat = dictDataService.list(q);
		return PhpResponse.ok(buildTree(flat));
	}

	private List<DictDataTreeNodeVO> buildTree(List<DictData> flat) {
		Map<String, List<DictData>> byPid = flat.stream()
			.collect(Collectors.groupingBy(d -> d.getPid() == null ? "" : d.getPid()));
		List<DictDataTreeNodeVO> roots = new ArrayList<>();
		for (DictData d : flat) {
			if (d.getPid() == null || d.getPid().isEmpty() || "0".equals(d.getPid())) {
				roots.add(toNode(d, byPid));
			}
		}
		return roots;
	}

	private DictDataTreeNodeVO toNode(DictData d, Map<String, List<DictData>> byPid) {
		DictDataTreeNodeVO n = new DictDataTreeNodeVO();
		n.setId(d.getId());
		n.setName(d.getName());
		n.setValue(d.getValue());
		n.setPid(d.getPid());
		n.setTypeId(d.getTypeId());
		n.setStatus(d.getStatus());
		n.setSort(d.getSort());
		List<DictData> children = byPid.getOrDefault(d.getValue(), List.of());
		if (!children.isEmpty()) {
			List<DictDataTreeNodeVO> ch = new ArrayList<>();
			for (DictData c : children) {
				ch.add(toNode(c, byPid));
			}
			n.setChildren(ch);
		}
		return n;
	}

}
