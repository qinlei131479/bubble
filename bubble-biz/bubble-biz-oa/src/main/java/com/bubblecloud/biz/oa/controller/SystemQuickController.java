package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.SystemQuickService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.SystemQuick;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
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
 * 快捷入口（对齐 PHP {@code ent/config/quick}）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/config/quick")
@Tag(name = "快捷入口")
public class SystemQuickController {

	private final SystemQuickService systemQuickService;

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "分页列表")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject SystemQuick query,
			@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "20") Integer limit) {
		pg.setCurrent(pageNum);
		pg.setSize(limit);
		Page<SystemQuick> res = systemQuickService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@GetMapping("/create")
	@Operation(summary = "添加快捷入口表单（elForm）")
	public R<OaElFormVO> createForm(@RequestParam(required = false) Integer cid,
			@RequestParam(defaultValue = "1") Long entid) {
		try {
			return R.phpOk(systemQuickService.buildCreateForm(entid, cid));
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PostMapping
	@Operation(summary = "新增")
	public R<String> create(@RequestBody SystemQuick req) {
		systemQuickService.create(req);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑数据")
	public R<OaElFormVO> editForm(@PathVariable Integer id, @RequestParam(defaultValue = "1") Long entid) {
		try {
			return R.phpOk(systemQuickService.buildEditForm(entid, id));
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改保存")
	public R<String> update(@PathVariable Integer id, @RequestBody SystemQuick req) {
		req.setId(id);
		systemQuickService.update(req);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@GetMapping("/{id}")
	@Operation(summary = "显示/隐藏（对齐 PHP show，query: status）")
	public R<String> show(@PathVariable Integer id, @RequestParam Integer status) {
		if (ObjectUtil.isNull(status)) {
			return R.phpFailed("缺少状态参数");
		}
		try {
			systemQuickService.updateShowStatus(id, status);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除")
	public R<String> removeById(@PathVariable Long id) {
		systemQuickService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
