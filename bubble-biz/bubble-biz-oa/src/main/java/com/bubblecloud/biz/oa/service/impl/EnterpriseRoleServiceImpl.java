package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseRoleMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseRoleUserMapper;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.mapper.FrameMapper;
import com.bubblecloud.biz.oa.mapper.SystemMenusMapper;
import com.bubblecloud.biz.oa.service.EnterpriseRoleService;
import com.bubblecloud.biz.oa.service.SystemMenusService;
import com.bubblecloud.biz.oa.util.TreeUtil;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.FrameAssistView;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.EnterpriseRole;
import com.bubblecloud.oa.api.entity.EnterpriseRoleUser;
import com.bubblecloud.oa.api.entity.Frame;
import com.bubblecloud.oa.api.entity.SystemMenus;
import com.bubblecloud.oa.api.vo.menu.SystemMenusTreeNodeVO;
import com.bubblecloud.oa.api.vo.role.EnterpriseRoleListItemVO;
import com.bubblecloud.oa.api.vo.role.RoleMemberRowVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.collection.CollUtil;
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

	private final FrameMapper frameMapper;

	private final FrameAssistMapper frameAssistMapper;

	private final SystemMenusService systemMenusService;

	private final ObjectMapper objectMapper;

	@Override
	public List<EnterpriseRoleListItemVO> listRoles(String roleName, Long entId) {
		var q = Wrappers.lambdaQuery(EnterpriseRole.class).eq(EnterpriseRole::getEntid, entId);
		if (StrUtil.isNotBlank(roleName)) {
			q.like(EnterpriseRole::getRoleName, roleName);
		}
		q.orderByDesc(EnterpriseRole::getId);
		List<EnterpriseRole> rows = baseMapper.selectList(q);
		if (CollUtil.isEmpty(rows)) {
			return List.of();
		}
		List<EnterpriseRoleListItemVO> out = new ArrayList<>(rows.size());
		for (EnterpriseRole r : rows) {
			EnterpriseRoleListItemVO vo = new EnterpriseRoleListItemVO();
			vo.setId(r.getId());
			vo.setRoleName(r.getRoleName());
			vo.setUserCount(r.getUserCount());
			vo.setStatus(r.getStatus());
			vo.setDataLevel(r.getDataLevel());
			vo.setDirectly(r.getDirectly());
			vo.setFrameId(r.getFrameId());
			vo.setFrame(resolveFrames(r.getFrameId()));
			out.add(vo);
		}
		return out;
	}

	private List<EnterpriseRoleListItemVO.FrameBriefVO> resolveFrames(String frameIdRaw) {
		List<Long> ids = parseIdList(frameIdRaw);
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		List<Frame> frames = frameMapper
			.selectList(Wrappers.lambdaQuery(Frame.class).in(Frame::getId, ids).isNull(Frame::getDeletedAt));
		List<EnterpriseRoleListItemVO.FrameBriefVO> list = new ArrayList<>();
		for (Frame f : frames) {
			EnterpriseRoleListItemVO.FrameBriefVO b = new EnterpriseRoleListItemVO.FrameBriefVO();
			b.setId(f.getId());
			b.setName(f.getName());
			list.add(b);
		}
		return list;
	}

	private List<Long> parseIdList(String raw) {
		if (StrUtil.isBlank(raw)) {
			return List.of();
		}
		String t = raw.trim();
		try {
			JsonNode n = objectMapper.readTree(t);
			if (n.isArray()) {
				List<Long> ids = new ArrayList<>();
				for (JsonNode x : n) {
					if (x.isIntegralNumber()) {
						ids.add(x.longValue());
					}
					else if (x.isTextual()) {
						Long v = parseLongSafe(x.asText());
						if (ObjectUtil.isNotNull(v)) {
							ids.add(v);
						}
					}
				}
				return ids;
			}
		}
		catch (Exception ignored) {
		}
		if (t.contains(",")) {
			List<Long> ids = new ArrayList<>();
			for (String p : t.split(",")) {
				Long v = parseLongSafe(p.trim());
				if (ObjectUtil.isNotNull(v)) {
					ids.add(v);
				}
			}
			return ids;
		}
		Long one = parseLongSafe(t);
		return ObjectUtil.isNull(one) ? List.of() : List.of(one);
	}

	private static Long parseLongSafe(String s) {
		if (StrUtil.isBlank(s)) {
			return null;
		}
		try {
			return Long.parseLong(s);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public JsonNode getRoleInfo(Long entId, Long roleId) {
		List<Long> defaultApis = new ArrayList<>();
		ObjectNode rule = objectMapper.createObjectNode();
		if (ObjectUtil.isNotNull(roleId) && roleId > 0) {
			EnterpriseRole er = baseMapper.selectOne(Wrappers.lambdaQuery(EnterpriseRole.class)
				.eq(EnterpriseRole::getId, roleId)
				.eq(EnterpriseRole::getEntid, entId));
			if (ObjectUtil.isNull(er)) {
				throw new IllegalArgumentException("修改的角色不存在");
			}
			rule.put("id", er.getId());
			rule.put("role_name", StrUtil.nullToEmpty(er.getRoleName()));
			rule.set("rules", readJsonArray(er.getRules()));
			rule.set("apis", readJsonArray(er.getApis()));
			rule.put("status", ObjectUtil.defaultIfNull(er.getStatus(), 0));
			rule.put("data_level", ObjectUtil.defaultIfNull(er.getDataLevel(), 1));
			rule.put("directly", ObjectUtil.defaultIfNull(er.getDirectly(), 0));
			ArrayNode frameArr = objectMapper.createArrayNode();
			for (EnterpriseRoleListItemVO.FrameBriefVO f : resolveFrames(er.getFrameId())) {
				ObjectNode o = objectMapper.createObjectNode();
				o.put("id", f.getId());
				o.put("name", f.getName());
				frameArr.add(o);
			}
			rule.set("frame", frameArr);
			defaultApis.addAll(jsonArrayToLongList(er.getApis()));
		}
		JsonNode tree = systemMenusService.buildRoleMenuCascader(entId, defaultApis);
		ObjectNode root = objectMapper.createObjectNode();
		root.set("tree", tree);
		root.set("rule", rule);
		root.set("crud", objectMapper.createArrayNode());
		return root;
	}

	private ArrayNode readJsonArray(String raw) {
		if (StrUtil.isBlank(raw)) {
			return objectMapper.createArrayNode();
		}
		try {
			JsonNode n = objectMapper.readTree(raw);
			if (n.isArray()) {
				return (ArrayNode) n;
			}
		}
		catch (Exception ignored) {
		}
		return objectMapper.createArrayNode();
	}

	private List<Long> jsonArrayToLongList(String raw) {
		ArrayNode arr = readJsonArray(raw);
		List<Long> ids = new ArrayList<>();
		for (JsonNode x : arr) {
			if (x.isIntegralNumber()) {
				ids.add(x.longValue());
			}
		}
		return ids;
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
	public JsonNode getRoleUserList(Long roleId, Long entId, Integer page, Integer limit) {
		long ok = baseMapper.selectCount(Wrappers.lambdaQuery(EnterpriseRole.class)
			.eq(EnterpriseRole::getId, roleId)
			.eq(EnterpriseRole::getEntid, entId));
		if (ok == 0) {
			throw new IllegalArgumentException("无效的角色ID");
		}
		List<EnterpriseRoleUser> rus = enterpriseRoleUserMapper
			.selectList(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
				.eq(EnterpriseRoleUser::getRoleId, roleId)
				.eq(EnterpriseRoleUser::getEntid, entId));
		int pg = ObjectUtil.defaultIfNull(page, 1);
		int lim = ObjectUtil.defaultIfNull(limit, 15);
		int from = Math.max(0, (pg - 1) * lim);
		List<EnterpriseRoleUser> slice = rus.stream().skip(from).limit(lim).toList();
		ArrayNode list = objectMapper.createArrayNode();
		for (EnterpriseRoleUser ru : slice) {
			Admin a = adminMapper.selectById(ru.getUserId());
			if (ObjectUtil.isNull(a) || ObjectUtil.isNotNull(a.getDeletedAt())) {
				continue;
			}
			RoleMemberRowVO row = new RoleMemberRowVO();
			row.setId(a.getId());
			row.setName(a.getName());
			row.setStatus(ru.getStatus());
			List<FrameAssistView> frames = frameAssistMapper.selectUserFrames(a.getId(), entId);
			RoleMemberRowVO.FrameNameVO fn = new RoleMemberRowVO.FrameNameVO();
			fn.setName("--");
			for (FrameAssistView fv : frames) {
				if (ObjectUtil.equal(fv.getIsMastart(), 1) && StrUtil.isNotBlank(fv.getFrameName())) {
					fn.setName(fv.getFrameName());
					break;
				}
			}
			if ("--".equals(fn.getName()) && CollUtil.isNotEmpty(frames)) {
				fn.setName(frames.get(0).getFrameName());
			}
			row.setFrame(fn);
			list.add(objectMapper.valueToTree(row));
		}
		ObjectNode root = objectMapper.createObjectNode();
		root.set("list", list);
		root.put("count", rus.size());
		return root;
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
		}
		catch (Exception e) {
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
			.eq(EnterpriseRoleUser::getUserId, userId)
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
			}
			catch (Exception e) {
				a.setRoles("[]");
			}
			adminMapper.updateById(a);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addRoleUsers(Long entId, Long roleId, List<Long> userIds, List<Integer> frameIds) {
		if (ObjectUtil.isNull(roleId) || roleId <= 0) {
			throw new IllegalArgumentException("角色id不能为空");
		}
		Set<Long> merged = new LinkedHashSet<>();
		if (CollUtil.isNotEmpty(userIds)) {
			merged.addAll(userIds);
		}
		if (CollUtil.isNotEmpty(frameIds)) {
			List<Long> fromFrames = frameAssistMapper.selectUserIdsByFrameIds(entId, frameIds);
			merged.addAll(fromFrames);
		}
		if (merged.isEmpty()) {
			throw new IllegalArgumentException("至少选择一个部门或者一个用户");
		}
		List<Long> userIdsList = enterpriseRoleUserMapper
			.selectList(Wrappers.lambdaQuery(EnterpriseRoleUser.class)
				.eq(EnterpriseRoleUser::getRoleId, roleId)
				.in(EnterpriseRoleUser::getUserId, merged))
			.stream()
			.map(EnterpriseRoleUser::getUserId)
			.toList();
		Set<Long> existing = new LinkedHashSet<>(userIdsList);
		List<Long> newIds = merged.stream().filter(id -> !existing.contains(id)).toList();
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
			mergeRoleIntoAdmin(uid, roleId);
		}
		EnterpriseRole role = getBaseMapper().selectById(roleId);
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
		ids.add(roleId);
		try {
			a.setRoles(objectMapper.writeValueAsString(ids));
		}
		catch (Exception e) {
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
		}
		catch (Exception e) {
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
			ids.remove(roleId);
			try {
				a.setRoles(objectMapper.writeValueAsString(ids));
			}
			catch (Exception e) {
				a.setRoles("[]");
			}
			adminMapper.updateById(a);
		}
		EnterpriseRole role = getBaseMapper().selectById(roleId);
		if (ObjectUtil.isNotNull(role)) {
			long cnt = enterpriseRoleUserMapper
				.selectCount(Wrappers.lambdaQuery(EnterpriseRoleUser.class).eq(EnterpriseRoleUser::getRoleId, roleId));
			role.setUserCount((int) cnt);
			getBaseMapper().updateById(role);
		}
	}

	@Override
	public void updateSuperRole(Long entId, JsonNode body) {
		JsonNode rules = body.get("rules");
		if (ObjectUtil.isNull(rules) || !rules.isArray() || rules.isEmpty()) {
			throw new IllegalArgumentException("至少选择一个权限");
		}
		// 无 eb_system_role 等企业超级角色表时，仅占位成功；后续可接表持久化。
	}

	@Override
	public JsonNode getSuperRoleMenus(Long entId, String menuName) {
		ObjectNode root = objectMapper.createObjectNode();
		root.set("menus", objectMapper.createArrayNode());
		ObjectNode rules = objectMapper.createObjectNode();
		rules.set("rules", objectMapper.createArrayNode());
		rules.set("apis", objectMapper.createArrayNode());
		root.set("rules", rules);
		return root;
	}

	@Override
	public JsonNode getMenusRuleByPid(Long pid) {
		return systemMenusService.listPidMenuRules(pid);
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
