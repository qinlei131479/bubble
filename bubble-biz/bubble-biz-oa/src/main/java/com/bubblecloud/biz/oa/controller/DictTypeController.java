package com.bubblecloud.biz.oa.controller;

import java.util.Set;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.DictTypeService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.DictType;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.config.DictTypeInfoVO;
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

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "字典类型分页列表")
	public R<SimplePageVO> page(@ParameterObject Pg<DictType> pg, DictType query) {
		Page<DictType> r = dictTypeService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords()));
	}

	@GetMapping("/create")
	@Operation(summary = "新增字典表单默认值")
	public R<DictType> createForm() {
		DictType t = new DictType();
		t.setLinkType("custom");
		t.setStatus(1);
		t.setLevel(1);
		return R.phpOk(t);
	}

	@PostMapping
	@Operation(summary = "新增字典")
	public R<DictTypeStoreResultVO> create(@RequestBody DictType body) {
		dictTypeService.save(body);
		return R.phpOk(new DictTypeStoreResultVO(body.getId()));
	}

	@GetMapping("/{id}")
	@Operation(summary = "显示/隐藏字典（status 查询参数）")
	public R<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			return R.phpFailed("缺少ID");
		}
		if (ObjectUtil.isNull(status)) {
			return R.phpFailed("缺少状态");
		}
		DictType u = new DictType();
		u.setId(id);
		u.setStatus(status);
		dictTypeService.updateById(u);
		return R.phpOk("common.update.succ");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取修改字典数据")
	public R<DictType> details(@PathVariable Long id) {
		return R.phpOk(dictTypeService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改字典")
	public R<String> update(@PathVariable Long id, @RequestBody DictType body) {
		body.setId(id);
		dictTypeService.updateById(body);
		return R.phpOk("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除字典")
	public R<String> removeById(@PathVariable Long id) {
		DictType row = dictTypeService.getById(id);
		if (ObjectUtil.isNull(row)) {
			return R.phpFailed("数据不存在");
		}
		if (ObjectUtil.isNull(row.getIdent()) || !CAN_DELETE_IDENTS.contains(row.getIdent())) {
			return R.phpFailed("仅允许删除业务白名单内的字典类型");
		}
		dictTypeService.removeById(id);
		return R.phpOk("删除成功");
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "字典详情（含是否可删标记）")
	public R<DictTypeInfoVO> info(@PathVariable Long id) {
		DictType row = dictTypeService.getById(id);
		if (ObjectUtil.isNull(row)) {
			return R.phpFailed("数据不存在");
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
		return R.phpOk(vo);
	}

}
