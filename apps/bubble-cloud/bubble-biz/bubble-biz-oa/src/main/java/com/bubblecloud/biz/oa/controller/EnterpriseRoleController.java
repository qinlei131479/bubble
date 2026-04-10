package com.bubblecloud.biz.oa.controller;

import java.util.ArrayList;
import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.EnterpriseRoleService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.vo.role.EnterpriseRoleListItemVO;
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
import cn.hutool.core.util.StrUtil;

/**
 * 企业角色（对齐 PHP {@code ent/system/roles}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午12:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/roles")
@Tag(name = "企业角色")
public class EnterpriseRoleController {

	private final EnterpriseRoleService enterpriseRoleService;

	private final AdminService adminService;

	@GetMapping("/create")
	@Operation(summary = "新建角色表单数据")
	public R<JsonNode> createForm(@RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(enterpriseRoleService.getRoleInfo(entid, 0L));
	}

	@GetMapping
	@Operation(summary = "角色列表")
	public R<List<EnterpriseRoleListItemVO>> list(@RequestParam(required = false) String role_name,
			@RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(enterpriseRoleService.listRoles(role_name, entid));
	}

	@GetMapping("/{id:\\d+}/edit")
	@Operation(summary = "编辑角色表单数据")
	public R<JsonNode> editForm(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(enterpriseRoleService.getRoleInfo(entid, id));
	}

	@GetMapping("/{id:\\d+}")
	@Operation(summary = "启用/禁用角色（前端 GET + query）")
	public R<String> changeStatusByGet(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid,
			@RequestParam Integer status) {
		enterpriseRoleService.changeRoleStatus(entid, id, status);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@PostMapping
	@Operation(summary = "保存角色")
	public R<String> create(@RequestParam(defaultValue = "1") Long entid, @RequestBody JsonNode body) {
		enterpriseRoleService.saveRole(entid, body);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@PutMapping("/{id:\\d+}")
	@Operation(summary = "修改角色")
	public R<String> update(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid,
			@RequestBody JsonNode body) {
		enterpriseRoleService.updateRole(id, entid, body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@PostMapping("/{id:\\d+}/status")
	@Operation(summary = "启用/禁用角色（POST 兼容）")
	public R<String> changeStatusByPost(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid,
			@RequestParam Integer status) {
		enterpriseRoleService.changeRoleStatus(entid, id, status);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id:\\d+}")
	@Operation(summary = "删除角色")
	public R<String> removeById(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid) {
		enterpriseRoleService.deleteRole(id, entid);
		return R.phpOk("删除成功");
	}

	@GetMapping("/user/{id:\\d+}")
	@Operation(summary = "角色成员")
	public R<JsonNode> getRoleUser(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid,
			@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit) {
		return R.phpOk(enterpriseRoleService.getRoleUserList(id, entid, page, limit));
	}

	@GetMapping("/role/{uid:\\d+}")
	@Operation(summary = "用户角色与菜单树")
	public R<JsonNode> getUserRole(@PathVariable Long uid, @RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(enterpriseRoleService.getUserRoleData(entid, uid));
	}

	@PostMapping("/user")
	@Operation(summary = "修改用户角色")
	public R<String> updateUserRole(@RequestParam(defaultValue = "1") Long entid, @RequestBody JsonNode body) {
		long userId = body.has("user_id") ? body.get("user_id").asLong() : 0L;
		JsonNode roleIds = body.get("role_id");
		enterpriseRoleService.changeUserRole(entid, userId, roleIds);
		return R.phpOk("修改成功");
	}

	@PostMapping("/add_user")
	@Operation(summary = "角色添加成员")
	public R<String> addUser(@RequestParam(defaultValue = "1") Long entid, @RequestBody JsonNode body) {
		Long roleId = body.has("role_id") ? body.get("role_id").asLong() : 0;
		List<Long> userIds = new ArrayList<>();
		if (body.has("user_id") && body.get("user_id").isArray()) {
			for (JsonNode n : body.get("user_id")) {
				userIds.add(n.asLong());
			}
		}
		List<Integer> frameIds = new ArrayList<>();
		if (body.has("frame_id") && body.get("frame_id").isArray()) {
			for (JsonNode n : body.get("frame_id")) {
				frameIds.add(n.asInt());
			}
		}
		enterpriseRoleService.addRoleUsers(entid, roleId, userIds, frameIds);
		return R.phpOk("添加成员成功");
	}

	@PostMapping("/show_user")
	@Operation(summary = "成员状态")
	public R<String> showUser(@RequestParam(defaultValue = "1") Long entid, @RequestBody JsonNode body) {
		Long uid = body.has("uid") ? body.get("uid").asLong() : 0;
		int status = body.has("status") ? body.get("status").asInt() : 1;
		Long roleId = body.has("role_id") ? body.get("role_id").asLong() : 0;
		enterpriseRoleService.changeRoleUserStatus(uid, entid, roleId, status);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@PostMapping("/del_user")
	@Operation(summary = "移除成员（与前端 POST 一致）")
	public R<String> deleteUser(@RequestParam(defaultValue = "1") Long entid, @RequestBody JsonNode body) {
		Long uid = body.has("uid") ? body.get("uid").asLong() : 0;
		Long roleId = body.has("role_id") ? body.get("role_id").asLong() : 0;
		enterpriseRoleService.delRoleUser(uid, entid, roleId);
		return R.phpOk("删除成功");
	}

	@PostMapping("/update_super_role")
	@Operation(summary = "修改企业超级角色权限（写入 eb_system_role）")
	public R<String> updateSuperRole(@RequestParam(defaultValue = "1") Long entid, @RequestBody JsonNode body) {
		enterpriseRoleService.updateSuperRole(entid, body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@GetMapping("/get_super_role")
	@Operation(summary = "获取超级角色权限树（menus + rules/apis）")
	public R<JsonNode> getSuperRole(@RequestParam(defaultValue = "1") Long entid,
			@RequestParam(required = false) String menu_name) {
		return R.phpOk(enterpriseRoleService.getSuperRoleMenus(entid, menu_name));
	}

	@GetMapping("/menus_rule/{pid:\\d+}")
	@Operation(summary = "某菜单下接口/按钮权限（与 PHP RoleController#getMenusRule 一致）")
	public R<JsonNode> menusRule(@PathVariable Long pid) {
		return R.phpOk(enterpriseRoleService.getMenusRuleByPid(pid));
	}

	@PostMapping("/pwd")
	@Operation(summary = "修改用户密码")
	public R<String> updateUserPassword(@RequestBody JsonNode body) {
		String password = body.has("password") ? body.get("password").asText() : "";
		String uid = body.has("uid") ? body.get("uid").asText() : "";
		if (StrUtil.isBlank(password) || StrUtil.isBlank(uid)) {
			return R.phpFailed("参数错误");
		}
		adminService.updatePasswordByUid(uid, password);
		return R.phpOk("修改成功");
	}

}
