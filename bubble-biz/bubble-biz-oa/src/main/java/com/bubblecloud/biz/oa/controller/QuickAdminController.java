package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.QuickAdminService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.SystemQuick;
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
public class QuickAdminController {

	private final QuickAdminService quickAdminService;

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "分页列表")
	public R<?> page(@RequestParam(required = false) Integer cid, @RequestParam(required = false) String name,
			@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "20") Integer limit) {
		return R.phpOk(quickAdminService.page(cid, name, pageNum, limit));
	}

	@GetMapping("/create")
	@Operation(summary = "创建占位")
	public R<String> createForm(@RequestParam(required = false) String cid) {
		return R.phpOk("ok");
	}

	@PostMapping
	@Operation(summary = "新增")
	public R<String> create(@RequestBody SystemQuick body) {
		quickAdminService.saveQuick(body);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑数据")
	public R<SystemQuick> details(@PathVariable Integer id) {
		return R.phpOk(quickAdminService.getQuick(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改保存")
	public R<String> update(@PathVariable Integer id, @RequestBody SystemQuick body) {
		body.setId(id);
		quickAdminService.updateQuick(body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@GetMapping("/{id}")
	@Operation(summary = "显示/隐藏占位（PHP show）")
	public R<String> show(@PathVariable Integer id) {
		return R.phpOk("ok");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除")
	public R<String> removeById(@PathVariable Integer id) {
		quickAdminService.deleteQuick(id);
		return R.phpOk("common.delete.succ");
	}

}
