package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.SystemMenusService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.SystemMenus;
import com.bubblecloud.oa.api.vo.menu.SystemMenusTreeNodeVO;
import com.fasterxml.jackson.databind.JsonNode;
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
 * 企业菜单管理（对齐 PHP {@code ent/system/menus}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/menus")
@Tag(name = "企业菜单管理")
public class SystemMenusController {

	private final SystemMenusService systemMenusService;

	@GetMapping
	@Operation(summary = "企业菜单树")
	public R<List<SystemMenusTreeNodeVO>> list(@RequestParam(required = false) String menu_name,
											   @RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(systemMenusService.listMenuTree(menu_name, entid));
	}

	@GetMapping("/tree")
	@Operation(summary = "菜单树（PHP getTree）")
	public R<List<SystemMenusTreeNodeVO>> getTree(@RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(systemMenusService.listMenuTree(null, entid));
	}

	@PostMapping
	@Operation(summary = "新增菜单")
	public R<String> create(@RequestParam(defaultValue = "1") Long entid, @RequestBody SystemMenus req) {
		req.setEntid(entid);
		systemMenusService.create(req);
		return R.phpOk("添加成功");
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改菜单")
	public R<String> update(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid,
							@RequestBody SystemMenus req) {
		req.setId(id);
		req.setEntid(entid);
		systemMenusService.update(req);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除菜单")
	public R<String> removeById(@PathVariable Long id) {
		systemMenusService.deleteById(id);
		return R.phpOk("删除成功");
	}

	@PostMapping("/{id}/is_show")
	@Operation(summary = "显示/隐藏")
	public R<String> show(@PathVariable Long id, @RequestParam Integer is_show) {
		systemMenusService.updateIsShow(id, is_show);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@PostMapping("/not_save")
	@Operation(summary = "未保存权限占位")
	public R<JsonNode> getNotSaveMenus(@RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(systemMenusService.getNotSaveMenus(entid));
	}

}
