package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.SystemMenusService;
import com.bubblecloud.common.core.util.R;
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

	@GetMapping("/create")
	@Operation(summary = "新增菜单抽屉表单")
	public R<JsonNode> createForm(@RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(systemMenusService.getMenuDrawerCreateForm(entid));
	}

	@GetMapping("/{id:\\d+}/edit")
	@Operation(summary = "编辑菜单抽屉表单")
	public R<JsonNode> editForm(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(systemMenusService.getMenuDrawerUpdateForm(id, entid));
	}

	@GetMapping("/rule_list/{pid:\\d+}")
	@Operation(summary = "某菜单下接口/按钮权限")
	public R<JsonNode> ruleList(@PathVariable Long pid) {
		return R.phpOk(systemMenusService.listPidMenuRules(pid));
	}

	@PostMapping({ "/save", "/save_enterprise" })
	@Operation(summary = "保存企业菜单到超级角色（body: rules、apis 菜单 ID 数组）")
	public R<String> saveCompanyMenus(@RequestParam(defaultValue = "1") Long entid,
			@RequestBody(required = false) JsonNode body) {
		systemMenusService.saveMenusForCompany(entid, body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

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
	@Operation(summary = "新增菜单（抽屉/JSON 体）")
	public R<String> create(@RequestParam(defaultValue = "1") Long entid, @RequestBody JsonNode body) {
		systemMenusService.saveMenuFromForm(entid, body);
		return R.phpOk("添加成功");
	}

	@PutMapping("/{id:\\d+}")
	@Operation(summary = "修改菜单（抽屉/JSON 体）")
	public R<String> update(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid,
			@RequestBody JsonNode body) {
		systemMenusService.updateMenuFromForm(id, entid, body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@GetMapping("/{id:\\d+}")
	@Operation(summary = "显示/隐藏（前端 GET + query，与 PHP show 对齐）")
	public R<String> showByGet(@PathVariable Long id, @RequestParam Integer is_show) {
		systemMenusService.updateIsShow(id, is_show);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@PostMapping("/{id:\\d+}/is_show")
	@Operation(summary = "显示/隐藏（POST 兼容）")
	public R<String> showByPost(@PathVariable Long id, @RequestParam Integer is_show) {
		systemMenusService.updateIsShow(id, is_show);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id:\\d+}")
	@Operation(summary = "删除菜单")
	public R<String> removeById(@PathVariable Long id) {
		systemMenusService.deleteById(id);
		return R.phpOk("删除成功");
	}

	@PostMapping("/not_save")
	@Operation(summary = "获取未保存到角色的菜单权限（ent/uni 分组）")
	public R<JsonNode> getNotSaveMenus(@RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(systemMenusService.getNotSaveMenus(entid));
	}

}
