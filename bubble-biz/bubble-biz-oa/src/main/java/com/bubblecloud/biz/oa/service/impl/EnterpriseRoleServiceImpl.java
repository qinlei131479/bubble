package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseRoleMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseRoleUserMapper;
import com.bubblecloud.biz.oa.mapper.SystemMenusMapper;
import com.bubblecloud.biz.oa.service.EnterpriseRoleService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.biz.oa.util.TreeUtil;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.EnterpriseRole;
import com.bubblecloud.oa.api.entity.EnterpriseRoleUser;
import com.bubblecloud.oa.api.entity.SystemMenus;
import com.bubblecloud.oa.api.vo.menu.SystemMenusTreeNodeVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 企业角色管理实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
public class EnterpriseRoleServiceImpl extends UpServiceImpl<EnterpriseRoleMapper, EnterpriseRole>
		implements EnterpriseRoleService {

	private final EnterpriseRoleUserMapper enterpriseRoleUserMapper;

	private final AdminMapper adminMapper;

	private final SystemMenusMapper systemMenusMapper;

	private final ObjectMapper objectMapper;

	@Override
	public List<EnterpriseRole> listRoles(String roleName, Long entId) {
		var q = Wrappers.lambdaQuery(EnterpriseRole.class).eq(EnterpriseRole::getEntid, entId);
		if (StrUtil.isNotBlank(roleName)) {
			q.like(EnterpriseRole::getRoleName, roleName);
		}
		q.orderByDesc(EnterpriseRole::getId);
		return baseMapper.selectList(q);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long saveRole(Long entId, JsonNode body) {
		EnterpriseRole r = new EnterpriseRole();
		fillRole(r, body);
		r.setEntid(entId);
		baseMapper.insert(r);
		return r.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRole(Long id, Long entId, JsonNode body) {
		EnterpriseRole exist = baseMapper.selectOne(Wrappers.lambdaQuery(EnterpriseRole.class)
				.eq(EnterpriseRole::getId, id)
				.eq(EnterpriseRole::getEntid, entId));
		if (ObjectUtil.isNull(exist)) {
			throw new IllegalArgumentException("未找到可修改的角色");
		}
		fillRole(exist, body);
		exist.setId(id);
		baseMapper.updateById(exist);
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
		if (ObjectUtil.isNull(n) || !n.has(field) || n.get(field).isNull()) {
			return "";
		}
		return n.get(field).asText("");
	}

	private String jsonField(JsonNode n, String field) {
		if (ObjectUtil.isNull(n) || !n.has(field) || n.get(field).isNull()) {
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
	public void deleteRole(Long id, Long entId) {
		EnterpriseRole r = baseMapper.selectOne(Wrappers.lambdaQuery(EnterpriseRole.class)
				.eq(EnterpriseRole::getId, id)
				.eq(EnterpriseRole::getEntid, entId));
		if (ObjectUtil.isNull(r)) {
			throw new IllegalArgumentException("未找到可删除的角色");
		}
		enterpriseRoleUserMapper
				.delete(Wrappers.lambdaQuery(EnterpriseRoleUser.class).eq(EnterpriseRoleUser::getRoleId, id));
		getBaseMapper().deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeRoleStatus(Long entId, Long roleId, Integer status) {
		EnterpriseRole r = getBaseMapper().selectOne(Wrappers.lambdaQuery(EnterpriseRole.class)
				.eq(EnterpriseRole::getId, roleId)
				.eq(EnterpriseRole::getEntid, entId));
		if (ObjectUtil.isNull(r)) {
			throw new IllegalArgumentException("未找到可修改的角色");
		}
		if (Objects.equals(r.getStatus(), status)) {
			return;
		}
		r.setStatus(status);
		getBaseMapper().updateById(r);
		enterpriseRoleUserMapper.update(null,
				Wrappers.lambdaUpdate(EnterpriseRoleUser.class)
						.eq(EnterpriseRoleUser::getRoleId, roleId)
						.set(EnterpriseRoleUser::getStatus, status));
	}

	@Override
	public List<Admin> getRoleUsers(Long roleId, Long entId) {
		List<Long> userIds = enterpriseRoleUserMapper
				.selectList(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
						.eq(EnterpriseRoleUser::getRoleId, roleId)
						.eq(EnterpriseRoleUser::getEntid, entId)
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
	public JsonNode getUserRoleData(Long entId, Long userId) {
		List<Long> roleIds = enterpriseRoleUserMapper
				.selectList(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
						.eq(EnterpriseRoleUser::getUserId, userId)
						.eq(EnterpriseRoleUser::getEntid, entId)
						.eq(EnterpriseRoleUser::getStatus, 1))
				.stream()
				.map(EnterpriseRoleUser::getRoleId)
				.toList();
		ArrayNode rolesArr = objectMapper.createArrayNode();
		roleIds.forEach(rolesArr::add);

		List<EnterpriseRole> all = getBaseMapper()
				.selectList(Wrappers.lambdaQuery(EnterpriseRole.class).eq(EnterpriseRole::getEntid, entId));
		ArrayNode roleList = objectMapper.createArrayNode();
		for (EnterpriseRole er : all) {
			ObjectNode o = roleList.addObject();
			o.put("value", er.getId());
			o.put("label", er.getRoleName());
			o.set("rules", readJson(er.getRules()));
			o.set("apis", readJson(er.getApis()));
		}
		ObjectNode root = objectMapper.createObjectNode();
		root.set("menus", buildMenuTreeJson(entId));
		root.set("roles", rolesArr);
		root.set("roleList", roleList);
		return root;
	}

	private JsonNode readJson(String raw) {
		if (StrUtil.isBlank(raw)) {
			return objectMapper.createArrayNode();
		}
		try {
			return objectMapper.readTree(raw);
		} catch (Exception e) {
			return objectMapper.createArrayNode();
		}
	}

	private JsonNode buildMenuTreeJson(Long entId) {
		List<SystemMenus> rows = systemMenusMapper.selectList(Wrappers.lambdaQuery(SystemMenus.class)
				.eq(SystemMenus::getEntid, entId)
				.isNull(SystemMenus::getDeletedAt)
				.orderByDesc(SystemMenus::getSort)
				.orderByAsc(SystemMenus::getId));
		List<SystemMenusTreeNodeVO> flat = new ArrayList<>();
		for (SystemMenus m : rows) {
			SystemMenusTreeNodeVO n = new SystemMenusTreeNodeVO();
			n.setId(m.getId());
			n.setPid(m.getPid());
			n.setMenuName(m.getMenuName());
			n.setIsShow(m.getIsShow());
			n.setType(m.getType());
			n.setSort(m.getSort());
			flat.add(n);
		}
		return objectMapper.valueToTree(TreeUtil.buildTree(flat, SystemMenusTreeNodeVO::getId,
				SystemMenusTreeNodeVO::getPid, SystemMenusTreeNodeVO::getChildren));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeUserRole(Long entId, Long userId, JsonNode roleIdsNode) {
		List<Long> roleIds = new ArrayList<>();
		if (ObjectUtil.isNotNull(roleIdsNode) && roleIdsNode.isArray()) {
			for (JsonNode n : roleIdsNode) {
				roleIds.add(n.asLong());
			}
		}
		enterpriseRoleUserMapper.delete(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
				.eq(EnterpriseRoleUser::getUserId, userId.intValue())
				.eq(EnterpriseRoleUser::getEntid, entId));
		for (Long rid : roleIds) {
			EnterpriseRoleUser ru = new EnterpriseRoleUser();
			ru.setEntid(entId);
			ru.setRoleId(rid);
			ru.setUserId(userId);
			ru.setStatus(1);
			enterpriseRoleUserMapper.insert(ru);
		}
		Admin a = adminMapper.selectById(userId);
		if (ObjectUtil.isNotNull(a)) {
			try {
				a.setRoles(objectMapper.writeValueAsString(roleIds));
			} catch (Exception e) {
				a.setRoles("[]");
			}
			adminMapper.updateById(a);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addRoleUsers(Long entId, Long roleId, List<Long> userIds) {
		if (ObjectUtil.isNull(userIds) || userIds.isEmpty()) {
			throw new IllegalArgumentException("至少选择一个部门或者一个用户");
		}
		Set<Long> existing = enterpriseRoleUserMapper
				.selectList(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
						.eq(EnterpriseRoleUser::getRoleId, roleId)
						.in(EnterpriseRoleUser::getUserId, userIds))
				.stream()
				.map(EnterpriseRoleUser::getUserId)
				.collect(Collectors.toSet());
		List<Long> newIds = userIds.stream().filter(id -> !existing.contains(id)).toList();
		if (newIds.isEmpty()) {
			throw new IllegalArgumentException("您选择的用户已全部加入该角色下");
		}
		for (Long uid : newIds) {
			EnterpriseRoleUser ru = new EnterpriseRoleUser();
			ru.setEntid(entId);
			ru.setRoleId(roleId);
			ru.setUserId(uid);
			ru.setStatus(1);
			enterpriseRoleUserMapper.insert(ru);
			mergeRoleIntoAdmin(uid.longValue(), roleId);
		}
		EnterpriseRole role = getBaseMapper().selectById(roleId.longValue());
		if (ObjectUtil.isNotNull(role)) {
			long cnt = enterpriseRoleUserMapper
					.selectCount(Wrappers.lambdaQuery(EnterpriseRoleUser.class).eq(EnterpriseRoleUser::getRoleId, roleId));
			role.setUserCount((int) cnt);
			getBaseMapper().updateById(role);
		}
	}

	private void mergeRoleIntoAdmin(Long adminId, Long roleId) {
		Admin a = adminMapper.selectById(adminId);
		if (ObjectUtil.isNull(a)) {
			return;
		}
		Set<Long> ids = new LinkedHashSet<>(parseRoleIds(a.getRoles()));
		ids.add((long) roleId);
		try {
			a.setRoles(objectMapper.writeValueAsString(ids));
		} catch (Exception e) {
			a.setRoles("[" + roleId + "]");
		}
		adminMapper.updateById(a);
	}

	private List<Long> parseRoleIds(String raw) {
		if (StrUtil.isBlank(raw)) {
			return new ArrayList<>();
		}
		try {
			return objectMapper.readValue(raw, new TypeReference<List<Long>>() {
			});
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeRoleUserStatus(Long uid, Long entId, Long roleId, Integer status) {
		EnterpriseRoleUser ru = enterpriseRoleUserMapper.selectOne(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
				.eq(EnterpriseRoleUser::getUserId, uid)
				.eq(EnterpriseRoleUser::getEntid, entId)
				.eq(EnterpriseRoleUser::getRoleId, roleId));
		if (ObjectUtil.isNull(ru)) {
			throw new IllegalArgumentException("修改的成员不存在!");
		}
		ru.setStatus(status);
		enterpriseRoleUserMapper.updateById(ru);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delRoleUser(Long uid, Long entId, Long roleId) {
		enterpriseRoleUserMapper.delete(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
				.eq(EnterpriseRoleUser::getUserId, uid)
				.eq(EnterpriseRoleUser::getEntid, entId)
				.eq(EnterpriseRoleUser::getRoleId, roleId));
		Admin a = adminMapper.selectById(uid);
		if (ObjectUtil.isNotNull(a)) {
			List<Long> ids = parseRoleIds(a.getRoles());
			ids.remove(Long.valueOf(roleId));
			try {
				a.setRoles(objectMapper.writeValueAsString(ids));
			} catch (Exception e) {
				a.setRoles("[]");
			}
			adminMapper.updateById(a);
		}
		EnterpriseRole role = getBaseMapper().selectById(roleId.longValue());
		if (ObjectUtil.isNotNull(role)) {
			long cnt = enterpriseRoleUserMapper
					.selectCount(Wrappers.lambdaQuery(EnterpriseRoleUser.class).eq(EnterpriseRoleUser::getRoleId, roleId));
			role.setUserCount((int) cnt);
			getBaseMapper().updateById(role);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(EnterpriseRole req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(EnterpriseRole req) {
		return super.update(req);
	}

}
