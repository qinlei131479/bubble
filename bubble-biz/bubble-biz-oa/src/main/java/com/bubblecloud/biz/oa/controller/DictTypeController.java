package com.bubblecloud.biz.oa.controller;

import java.util.Set;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.DictTypeService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.entity.DictType;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.config.DictTypeInfoVO;
import com.bubblecloud.oa.api.vo.config.DictTypeStoreResultVO;
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

/**
 * 数据字典类型，对齐 PHP {@code ent/config/dict_type} 资源接口。
 *
 * @author qinlei
 * @date 2026/3/30 下午10:18
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/config/dict_type")
@Tag(name = "字典类型")
public class DictTypeController {

	private static final Set<String> CAN_DELETE_IDENTS = Set.of("customer_way", "customer_type", "client_renew",
			"contract_type", "area_cascade");

	private final DictTypeService dictTypeService;

	@GetMapping
	@Operation(summary = "字典类型分页列表")
	public PhpResponse<SimplePageVO> index(@RequestParam(defaultValue = "1") long current,
			@RequestParam(defaultValue = "20") long size, @RequestParam(required = false) String name,
			@RequestParam(required = false) String link_type, @RequestParam(required = false) Integer status) {
		Page<DictType> r = dictTypeService.pageDictTypes(current, size, name, link_type, status);
		return PhpResponse.ok(SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords()));
	}

	@GetMapping("/create")
	@Operation(summary = "新增字典表单默认值")
	public PhpResponse<DictType> create() {
		DictType t = new DictType();
		t.setLinkType("custom");
		t.setStatus(1);
		t.setLevel(1);
		return PhpResponse.ok(t);
	}

	@PostMapping
	@Operation(summary = "新增字典")
	public PhpResponse<DictTypeStoreResultVO> store(@RequestBody DictType body) {
		dictTypeService.save(body);
		return PhpResponse.ok(new DictTypeStoreResultVO(body.getId()));
	}

	@GetMapping("/{id}")
	@Operation(summary = "显示/隐藏字典（status 查询参数）")
	public PhpResponse<String> show(@PathVariable Long id, @RequestParam Integer status) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			return PhpResponse.failed("缺少ID");
		}
		if (ObjectUtil.isNull(status)) {
			return PhpResponse.failed("缺少状态");
		}
		DictType u = new DictType();
		u.setId(id);
		u.setStatus(status);
		dictTypeService.updateById(u);
		return PhpResponse.ok("common.update.succ");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取修改字典数据")
	public PhpResponse<DictType> edit(@PathVariable Long id) {
		return PhpResponse.ok(dictTypeService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改字典")
	public PhpResponse<String> update(@PathVariable Long id, @RequestBody DictType body) {
		body.setId(id);
		dictTypeService.updateById(body);
		return PhpResponse.ok("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除字典")
	public PhpResponse<String> destroy(@PathVariable Long id) {
		DictType row = dictTypeService.getById(id);
		if (ObjectUtil.isNull(row)) {
			return PhpResponse.failed("数据不存在");
		}
		if (ObjectUtil.isNull(row.getIdent()) || !CAN_DELETE_IDENTS.contains(row.getIdent())) {
			return PhpResponse.failed("仅允许删除业务白名单内的字典类型");
		}
		dictTypeService.removeById(id);
		return PhpResponse.ok("删除成功");
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "字典详情（含是否可删标记）")
	public PhpResponse<DictTypeInfoVO> info(@PathVariable Long id) {
		DictType row = dictTypeService.getById(id);
		if (ObjectUtil.isNull(row)) {
			return PhpResponse.failed("数据不存在");
		}
		DictTypeInfoVO vo = new DictTypeInfoVO();
		vo.setId(row.getId());
		vo.setName(row.getName());
		vo.setIdent(row.getIdent());
		vo.setLinkType(row.getLinkType());
		vo.setLevel(row.getLevel());
		vo.setStatus(row.getStatus());
		vo.setMark(row.getMark());
		vo.setCanDelete(ObjectUtil.isNotNull(row.getIdent()) && CAN_DELETE_IDENTS.contains(row.getIdent()));
		return PhpResponse.ok(vo);
	}

}
