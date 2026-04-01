package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.RolesAdminService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.EnterpriseRole;
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
public class RolesAdminController {

	private final RolesAdminService rolesAdminService;

	private final AdminService adminService;

	@GetMapping
	@Operation(summary = "角色列表")
	public R<List<EnterpriseRole>> list(@RequestParam(required = false) String role_name,
			@RequestParam(defaultValue = "1") Integer entid) {
		return R.phpOk(rolesAdminService.listRoles(role_name, entid));
	}

	@PostMapping
	@Operation(summary = "保存角色")
	public R<String> create(@RequestParam(defaultValue = "1") Integer entid, @RequestBody JsonNode body) {
		try {
			rolesAdminService.saveRole(entid, body);
			return R.phpOk("common.insert.succ");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改角色")
	public R<String> update(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer entid,
			@RequestBody JsonNode body) {
		try {
			rolesAdminService.updateRole(id, entid, body);
			return R.phpOk("common.update.succ");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PostMapping("/{id}/status")
	@Operation(summary = "启用/禁用角色")
	public R<String> show(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer entid,
			@RequestParam Integer status) {
		try {
			rolesAdminService.changeRoleStatus(entid, id, status);
			return R.phpOk("common.update.succ");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除角色")
	public R<String> removeById(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer entid) {
		try {
			rolesAdminService.deleteRole(id, entid);
			return R.phpOk("删除成功");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@GetMapping("/user/{id}")
	@Operation(summary = "角色成员")
	public R<?> getRoleUser(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer entid) {
		try {
			return R.phpOk(rolesAdminService.getRoleUsers(id, entid));
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@GetMapping("/role/{uid}")
	@Operation(summary = "用户角色与菜单树")
	public R<JsonNode> getUserRole(@PathVariable Long uid, @RequestParam(defaultValue = "1") Integer entid) {
		return R.phpOk(rolesAdminService.getUserRoleData(entid, uid));
	}

	@PostMapping("/user")
	@Operation(summary = "修改用户角色")
	public R<String> updateUserRole(@RequestParam(defaultValue = "1") Integer entid, @RequestBody JsonNode body) {
		long userId = body.has("user_id") ? body.get("user_id").asLong() : 0L;
		JsonNode roleIds = body.get("role_id");
		try {
			rolesAdminService.changeUserRole(entid, userId, roleIds);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PostMapping("/add_user")
	@Operation(summary = "角色添加成员")
	public R<String> addUser(@RequestParam(defaultValue = "1") Integer entid, @RequestBody JsonNode body) {
		int roleId = body.has("role_id") ? body.get("role_id").asInt() : 0;
		List<Integer> userIds = new java.util.ArrayList<>();
		if (body.has("user_id") && body.get("user_id").isArray()) {
			for (JsonNode n : body.get("user_id")) {
				userIds.add(n.asInt());
			}
		}
		try {
			rolesAdminService.addRoleUsers(entid, roleId, userIds);
			return R.phpOk("添加成员成功");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PostMapping("/show_user")
	@Operation(summary = "成员状态")
	public R<String> showUser(@RequestParam(defaultValue = "1") Integer entid, @RequestBody JsonNode body) {
		int uid = body.has("uid") ? body.get("uid").asInt() : 0;
		int status = body.has("status") ? body.get("status").asInt() : 1;
		int roleId = body.has("role_id") ? body.get("role_id").asInt() : 0;
		try {
			rolesAdminService.changeRoleUserStatus(uid, entid, roleId, status);
			return R.phpOk("common.update.succ");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@DeleteMapping("/del_user")
	@Operation(summary = "移除成员")
	public R<String> deleteUser(@RequestParam(defaultValue = "1") Integer entid, @RequestBody JsonNode body) {
		int uid = body.has("uid") ? body.get("uid").asInt() : 0;
		int roleId = body.has("role_id") ? body.get("role_id").asInt() : 0;
		try {
			rolesAdminService.delRoleUser(uid, entid, roleId);
			return R.phpOk("删除成功");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PostMapping("/pwd")
	@Operation(summary = "修改用户密码")
	public R<String> updateUserPassword(@RequestBody JsonNode body) {
		String password = body.has("password") ? body.get("password").asText() : "";
		String uid = body.has("uid") ? body.get("uid").asText() : "";
		if (StrUtil.isBlank(password) || StrUtil.isBlank(uid)) {
			return R.phpFailed("参数错误");
		}
		try {
			adminService.updatePasswordByUid(uid, password);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

}
