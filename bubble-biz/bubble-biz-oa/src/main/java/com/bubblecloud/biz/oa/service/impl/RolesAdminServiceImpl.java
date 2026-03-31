package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseRoleMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseRoleUserMapper;
import com.bubblecloud.biz.oa.mapper.SystemMenusMapper;
import com.bubblecloud.biz.oa.service.RolesAdminService;
import com.bubblecloud.biz.oa.util.TreeUtil;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.EnterpriseRole;
import com.bubblecloud.oa.api.entity.EnterpriseRoleUser;
import com.bubblecloud.oa.api.entity.SystemMenus;
import com.bubblecloud.oa.api.vo.menu.MenuAdminTreeNodeVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 企业角色管理实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
public class RolesAdminServiceImpl implements RolesAdminService {

	private final EnterpriseRoleMapper enterpriseRoleMapper;

	private final EnterpriseRoleUserMapper enterpriseRoleUserMapper;

	private final AdminMapper adminMapper;

	private final SystemMenusMapper systemMenusMapper;

	private final ObjectMapper objectMapper;

	@Override
	public List<EnterpriseRole> listRoles(String roleName, int entid) {
		var q = Wrappers.lambdaQuery(EnterpriseRole.class).eq(EnterpriseRole::getEntid, entid);
		if (StringUtils.hasText(roleName)) {
			q.like(EnterpriseRole::getRoleName, roleName);
		}
		q.orderByDesc(EnterpriseRole::getId);
		return enterpriseRoleMapper.selectList(q);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public long saveRole(int entid, JsonNode body) {
		EnterpriseRole r = new EnterpriseRole();
		fillRole(r, body);
		r.setEntid(entid);
		enterpriseRoleMapper.insert(r);
		return r.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRole(long id, int entid, JsonNode body) {
		EnterpriseRole exist = enterpriseRoleMapper.selectOne(Wrappers.lambdaQuery(EnterpriseRole.class)
			.eq(EnterpriseRole::getId, id)
			.eq(EnterpriseRole::getEntid, entid));
		if (exist == null) {
			throw new IllegalArgumentException("未找到可修改的角色");
		}
		fillRole(exist, body);
		exist.setId(id);
		enterpriseRoleMapper.updateById(exist);
	}

	private void fillRole(EnterpriseRole r, JsonNode body) {
		r.setRoleName(text(body, "role_name"));
		r.setRules(jsonField(body, "rules"));
		r.setApis(jsonField(body, "apis"));
		r.setRuleUnique("");
		r.setApiUnique("");
		if (body.has("status")) {
			r.setStatus(body.get("status").asInt());
		}
		if (body.has("data_level")) {
			r.setDataLevel(body.get("data_level").asInt());
		}
		if (body.has("directly")) {
			r.setDirectly(body.get("directly").asInt());
		}
		if (body.has("frame_id")) {
			r.setFrameId(body.get("frame_id").toString());
		}
	}

	private static String text(JsonNode n, String field) {
		if (n == null || !n.has(field) || n.get(field).isNull()) {
			return "";
		}
		return n.get(field).asText("");
	}

	private String jsonField(JsonNode n, String field) {
		if (n == null || !n.has(field) || n.get(field).isNull()) {
			return "[]";
		}
		JsonNode v = n.get(field);
		if (v.isArray() || v.isObject()) {
			return v.toString();
		}
		return v.asText("[]");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteRole(long id, int entid) {
		EnterpriseRole r = enterpriseRoleMapper.selectOne(Wrappers.lambdaQuery(EnterpriseRole.class)
			.eq(EnterpriseRole::getId, id)
			.eq(EnterpriseRole::getEntid, entid));
		if (r == null) {
			throw new IllegalArgumentException("未找到可删除的角色");
		}
		enterpriseRoleUserMapper
			.delete(Wrappers.lambdaQuery(EnterpriseRoleUser.class).eq(EnterpriseRoleUser::getRoleId, id));
		enterpriseRoleMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeRoleStatus(int entid, long roleId, int status) {
		EnterpriseRole r = enterpriseRoleMapper.selectOne(Wrappers.lambdaQuery(EnterpriseRole.class)
			.eq(EnterpriseRole::getId, roleId)
			.eq(EnterpriseRole::getEntid, entid));
		if (r == null) {
			throw new IllegalArgumentException("未找到可修改的角色");
		}
		if (Objects.equals(r.getStatus(), status)) {
			return;
		}
		r.setStatus(status);
		enterpriseRoleMapper.updateById(r);
		enterpriseRoleUserMapper.update(null,
				Wrappers.lambdaUpdate(EnterpriseRoleUser.class)
					.eq(EnterpriseRoleUser::getRoleId, roleId)
					.set(EnterpriseRoleUser::getStatus, status));
	}

	@Override
	public List<Admin> getRoleUsers(long roleId, int entid) {
		List<Integer> userIds = enterpriseRoleUserMapper
			.selectList(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
				.eq(EnterpriseRoleUser::getRoleId, roleId)
				.eq(EnterpriseRoleUser::getEntid, entid)
				.eq(EnterpriseRoleUser::getStatus, 1))
			.stream()
			.map(EnterpriseRoleUser::getUserId)
			.toList();
		if (userIds.isEmpty()) {
			return List.of();
		}
		return adminMapper
			.selectList(Wrappers.lambdaQuery(Admin.class).in(Admin::getId, userIds).eq(Admin::getStatus, 1));
	}

	@Override
	public JsonNode getUserRoleData(int entid, long userId) {
		List<Integer> roleIds = enterpriseRoleUserMapper
			.selectList(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
				.eq(EnterpriseRoleUser::getUserId, userId)
				.eq(EnterpriseRoleUser::getEntid, entid)
				.eq(EnterpriseRoleUser::getStatus, 1))
			.stream()
			.map(EnterpriseRoleUser::getRoleId)
			.toList();
		ArrayNode rolesArr = objectMapper.createArrayNode();
		roleIds.forEach(rolesArr::add);

		List<EnterpriseRole> all = enterpriseRoleMapper
			.selectList(Wrappers.lambdaQuery(EnterpriseRole.class).eq(EnterpriseRole::getEntid, entid));
		ArrayNode roleList = objectMapper.createArrayNode();
		for (EnterpriseRole er : all) {
			ObjectNode o = roleList.addObject();
			o.put("value", er.getId());
			o.put("label", er.getRoleName());
			o.set("rules", readJson(er.getRules()));
			o.set("apis", readJson(er.getApis()));
		}
		ObjectNode root = objectMapper.createObjectNode();
		root.set("menus", buildMenuTreeJson(entid));
		root.set("roles", rolesArr);
		root.set("roleList", roleList);
		return root;
	}

	private JsonNode readJson(String raw) {
		if (!StringUtils.hasText(raw)) {
			return objectMapper.createArrayNode();
		}
		try {
			return objectMapper.readTree(raw);
		}
		catch (Exception e) {
			return objectMapper.createArrayNode();
		}
	}

	private JsonNode buildMenuTreeJson(int entid) {
		List<SystemMenus> rows = systemMenusMapper.selectList(Wrappers.lambdaQuery(SystemMenus.class)
			.eq(SystemMenus::getEntid, entid)
			.isNull(SystemMenus::getDeletedAt)
			.orderByDesc(SystemMenus::getSort)
			.orderByAsc(SystemMenus::getId));
		List<MenuAdminTreeNodeVO> flat = new ArrayList<>();
		for (SystemMenus m : rows) {
			MenuAdminTreeNodeVO n = new MenuAdminTreeNodeVO();
			n.setId(m.getId());
			n.setPid(m.getPid());
			n.setMenuName(m.getMenuName());
			n.setIsShow(m.getIsShow());
			n.setType(m.getType());
			n.setSort(m.getSort());
			flat.add(n);
		}
		return objectMapper.valueToTree(TreeUtil.buildTree(flat, MenuAdminTreeNodeVO::getId,
				MenuAdminTreeNodeVO::getPid, MenuAdminTreeNodeVO::getChildren));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeUserRole(int entid, long userId, JsonNode roleIdsNode) {
		List<Long> roleIds = new ArrayList<>();
		if (roleIdsNode != null && roleIdsNode.isArray()) {
			for (JsonNode n : roleIdsNode) {
				roleIds.add(n.asLong());
			}
		}
		enterpriseRoleUserMapper.delete(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
			.eq(EnterpriseRoleUser::getUserId, (int) userId)
			.eq(EnterpriseRoleUser::getEntid, entid));
		for (Long rid : roleIds) {
			EnterpriseRoleUser ru = new EnterpriseRoleUser();
			ru.setEntid(entid);
			ru.setRoleId(rid.intValue());
			ru.setUserId((int) userId);
			ru.setStatus(1);
			enterpriseRoleUserMapper.insert(ru);
		}
		Admin a = adminMapper.selectById(userId);
		if (a != null) {
			try {
				a.setRoles(objectMapper.writeValueAsString(roleIds));
			}
			catch (Exception e) {
				a.setRoles("[]");
			}
			adminMapper.updateById(a);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addRoleUsers(int entid, int roleId, List<Integer> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			throw new IllegalArgumentException("至少选择一个部门或者一个用户");
		}
		Set<Integer> existing = enterpriseRoleUserMapper
			.selectList(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
				.eq(EnterpriseRoleUser::getRoleId, roleId)
				.in(EnterpriseRoleUser::getUserId, userIds))
			.stream()
			.map(EnterpriseRoleUser::getUserId)
			.collect(Collectors.toSet());
		List<Integer> newIds = userIds.stream().filter(id -> !existing.contains(id)).toList();
		if (newIds.isEmpty()) {
			throw new IllegalArgumentException("您选择的用户已全部加入该角色下");
		}
		for (Integer uid : newIds) {
			EnterpriseRoleUser ru = new EnterpriseRoleUser();
			ru.setEntid(entid);
			ru.setRoleId(roleId);
			ru.setUserId(uid);
			ru.setStatus(1);
			enterpriseRoleUserMapper.insert(ru);
			mergeRoleIntoAdmin(uid.longValue(), roleId);
		}
		EnterpriseRole role = enterpriseRoleMapper.selectById((long) roleId);
		if (role != null) {
			long cnt = enterpriseRoleUserMapper
				.selectCount(Wrappers.lambdaQuery(EnterpriseRoleUser.class).eq(EnterpriseRoleUser::getRoleId, roleId));
			role.setUserCount((int) cnt);
			enterpriseRoleMapper.updateById(role);
		}
	}

	private void mergeRoleIntoAdmin(long adminId, int roleId) {
		Admin a = adminMapper.selectById(adminId);
		if (a == null) {
			return;
		}
		Set<Long> ids = new LinkedHashSet<>(parseRoleIds(a.getRoles()));
		ids.add((long) roleId);
		try {
			a.setRoles(objectMapper.writeValueAsString(ids));
		}
		catch (Exception e) {
			a.setRoles("[" + roleId + "]");
		}
		adminMapper.updateById(a);
	}

	private List<Long> parseRoleIds(String raw) {
		if (!StringUtils.hasText(raw)) {
			return new ArrayList<>();
		}
		try {
			return objectMapper.readValue(raw, new TypeReference<List<Long>>() {
			});
		}
		catch (Exception e) {
			return new ArrayList<>();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeRoleUserStatus(int uid, int entid, int roleId, int status) {
		EnterpriseRoleUser ru = enterpriseRoleUserMapper.selectOne(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
			.eq(EnterpriseRoleUser::getUserId, uid)
			.eq(EnterpriseRoleUser::getEntid, entid)
			.eq(EnterpriseRoleUser::getRoleId, roleId));
		if (ru == null) {
			throw new IllegalArgumentException("修改的成员不存在!");
		}
		ru.setStatus(status);
		enterpriseRoleUserMapper.updateById(ru);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delRoleUser(int uid, int entid, int roleId) {
		enterpriseRoleUserMapper.delete(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
			.eq(EnterpriseRoleUser::getUserId, uid)
			.eq(EnterpriseRoleUser::getEntid, entid)
			.eq(EnterpriseRoleUser::getRoleId, roleId));
		Admin a = adminMapper.selectById((long) uid);
		if (a != null) {
			List<Long> ids = parseRoleIds(a.getRoles());
			ids.remove(Long.valueOf(roleId));
			try {
				a.setRoles(objectMapper.writeValueAsString(ids));
			}
			catch (Exception e) {
				a.setRoles("[]");
			}
			adminMapper.updateById(a);
		}
		EnterpriseRole role = enterpriseRoleMapper.selectById((long) roleId);
		if (role != null) {
			long cnt = enterpriseRoleUserMapper
				.selectCount(Wrappers.lambdaQuery(EnterpriseRoleUser.class).eq(EnterpriseRoleUser::getRoleId, roleId));
			role.setUserCount((int) cnt);
			enterpriseRoleMapper.updateById(role);
		}
	}

}
