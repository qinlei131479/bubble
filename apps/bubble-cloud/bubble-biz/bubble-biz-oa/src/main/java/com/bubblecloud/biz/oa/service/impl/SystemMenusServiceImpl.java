package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.bubblecloud.biz.oa.service.SystemRoleService;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemMenusMapper;
import com.bubblecloud.biz.oa.service.SystemMenusService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.biz.oa.util.TreeUtil;
import com.bubblecloud.oa.api.entity.SystemMenus;
import com.bubblecloud.oa.api.vo.menu.SystemMenusTreeNodeVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 企业菜单管理实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
public class SystemMenusServiceImpl extends UpServiceImpl<SystemMenusMapper, SystemMenus>
		implements SystemMenusService {

	private final ObjectMapper objectMapper;

	private final SystemRoleService systemRoleService;

	@Override
	public List<SystemMenusTreeNodeVO> listMenuTree(String menuName, Long entId) {
		var q = Wrappers.lambdaQuery(SystemMenus.class)
			.eq(SystemMenus::getEntid, entId)
			.isNull(SystemMenus::getDeletedAt);
		if (StrUtil.isNotBlank(menuName)) {
			q.like(SystemMenus::getMenuName, menuName);
		}
		q.orderByDesc(SystemMenus::getSort).orderByAsc(SystemMenus::getId);
		List<SystemMenus> rows = baseMapper.selectList(q);
		List<SystemMenusTreeNodeVO> flat = new ArrayList<>(rows.size());
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
		return TreeUtil.buildTree(flat, SystemMenusTreeNodeVO::getId, SystemMenusTreeNodeVO::getPid,
				SystemMenusTreeNodeVO::getChildren);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R deleteById(Long id) {
		long child = baseMapper.selectCount(Wrappers.lambdaQuery(SystemMenus.class).eq(SystemMenus::getPid, id));
		if (child > 0) {
			throw new IllegalArgumentException("请先删除下级菜单");
		}
		return super.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateIsShow(Long id, Integer isShow) {
		SystemMenus m = new SystemMenus();
		m.setId(id);
		m.setIsShow(isShow);
		m.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(m);
	}

	@Override
	public JsonNode getNotSaveMenus(Long entId) {
		com.bubblecloud.oa.api.entity.SystemRole superR = systemRoleService.getEnterpriseSuperRole(entId);
		Set<Long> assigned = expandRuleMenuIds(jsonTextToIdList(superR == null ? null : superR.getRules()));
		assigned.addAll(expandRuleMenuIds(jsonTextToIdList(superR == null ? null : superR.getApis())));
		List<SystemMenus> apiMenus = baseMapper.selectList(Wrappers.lambdaQuery(SystemMenus.class)
			.eq(SystemMenus::getEntid, entId)
			.eq(SystemMenus::getType, "A")
			.eq(SystemMenus::getStatus, 1)
			.isNull(SystemMenus::getDeletedAt));
		ArrayNode ent = objectMapper.createArrayNode();
		ArrayNode uni = objectMapper.createArrayNode();
		for (SystemMenus m : apiMenus) {
			if (assigned.contains(m.getId())) {
				continue;
			}
			ObjectNode o = objectMapper.createObjectNode();
			o.put("name", StrUtil.nullToEmpty(m.getMenuName()));
			o.put("menu_path", StrUtil.nullToEmpty(m.getApi()));
			o.put("method", StrUtil.nullToEmpty(m.getMethods()));
			String api = StrUtil.nullToEmpty(m.getApi()).toLowerCase();
			if (api.contains("uni")) {
				uni.add(o);
			}
			else {
				ent.add(o);
			}
		}
		ObjectNode root = objectMapper.createObjectNode();
		root.set("ent", ent);
		root.set("uni", uni);
		return root;
	}

	@Override
	public Set<Long> expandRuleMenuIds(Collection<Long> seedIds) {
		Set<Long> out = new LinkedHashSet<>();
		if (CollUtil.isEmpty(seedIds)) {
			return out;
		}
		out.addAll(seedIds);
		List<SystemMenus> rows = baseMapper.selectList(Wrappers.lambdaQuery(SystemMenus.class)
			.in(SystemMenus::getId, seedIds)
			.isNull(SystemMenus::getDeletedAt));
		for (SystemMenus m : rows) {
			addPathIds(m.getPath(), out);
		}
		return out;
	}

	private static void addPathIds(String path, Set<Long> out) {
		if (StrUtil.isBlank(path)) {
			return;
		}
		for (String p : path.split("/")) {
			if (StrUtil.isBlank(p)) {
				continue;
			}
			try {
				out.add(Long.parseLong(p.trim()));
			}
			catch (NumberFormatException ignored) {
			}
		}
	}

	private List<Long> jsonTextToIdList(String raw) {
		if (StrUtil.isBlank(raw)) {
			return List.of();
		}
		try {
			JsonNode n = objectMapper.readTree(raw);
			if (!n.isArray()) {
				return List.of();
			}
			List<Long> ids = new ArrayList<>();
			for (JsonNode x : n) {
				if (x.isIntegralNumber()) {
					ids.add(x.longValue());
				}
			}
			return ids;
		}
		catch (Exception e) {
			return List.of();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(SystemMenus req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(SystemMenus req) {
		return super.update(req);
	}

	@Override
	public JsonNode buildRoleMenuCascader(Long entId, Collection<Long> defaultCheckedIds) {
		Set<Long> defaults = defaultCheckedIds == null ? Set.of() : new HashSet<>(defaultCheckedIds);
		List<SystemMenus> rows = baseMapper.selectList(Wrappers.lambdaQuery(SystemMenus.class)
			.eq(SystemMenus::getEntid, entId)
			.eq(SystemMenus::getStatus, 1)
			.isNull(SystemMenus::getDeletedAt)
			.orderByDesc(SystemMenus::getSort)
			.orderByDesc(SystemMenus::getId));
		Map<Long, ObjectNode> byId = rows.stream()
			.collect(Collectors.toMap(SystemMenus::getId, m -> menuCascaderNode(m, defaults), (a, b) -> a,
					java.util.LinkedHashMap::new));
		ArrayNode roots = objectMapper.createArrayNode();
		for (SystemMenus m : rows) {
			ObjectNode node = byId.get(m.getId());
			long pid = m.getPid() == null ? 0L : m.getPid();
			if (pid == 0L || !byId.containsKey(pid)) {
				roots.add(node);
			}
			else {
				((ArrayNode) byId.get(pid).get("children")).add(node);
			}
		}
		return roots;
	}

	private ObjectNode menuCascaderNode(SystemMenus m, Set<Long> defaults) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("value", m.getId());
		n.put("label", StrUtil.nullToEmpty(m.getMenuName()));
		n.put("pid", m.getPid() == null ? 0 : m.getPid());
		n.put("type", StrUtil.nullToEmpty(m.getType()));
		n.put("disabled", false);
		n.put("is_default", defaults.contains(m.getId()));
		n.set("children", objectMapper.createArrayNode());
		return n;
	}

	@Override
	public JsonNode listPidMenuRules(Long pid) {
		List<SystemMenus> list = baseMapper.selectList(Wrappers.lambdaQuery(SystemMenus.class)
			.eq(SystemMenus::getPid, pid)
			.in(SystemMenus::getType, "A", "B")
			.orderByDesc(SystemMenus::getSort)
			.orderByAsc(SystemMenus::getId));
		return objectMapper.valueToTree(list);
	}

	@Override
	public JsonNode getMenuDrawerCreateForm(Long entId) {
		ObjectNode root = objectMapper.createObjectNode();
		root.put("title", "创建菜单");
		root.put("method", "POST");
		root.put("action", "/admin/ent/system/menus?entid=" + entId);
		root.set("rule", buildMenuDrawerRules(entId, null));
		return root;
	}

	@Override
	public JsonNode getMenuDrawerUpdateForm(Long id, Long entId) {
		SystemMenus menu = baseMapper.selectOne(Wrappers.lambdaQuery(SystemMenus.class)
			.eq(SystemMenus::getId, id)
			.eq(SystemMenus::getEntid, entId)
			.isNull(SystemMenus::getDeletedAt));
		if (menu == null) {
			throw new IllegalArgumentException("修改的菜单数据不存在");
		}
		ObjectNode root = objectMapper.createObjectNode();
		root.put("title", "修改菜单");
		root.put("method", "PUT");
		root.put("action", "/admin/ent/system/menus/" + id + "?entid=" + entId);
		root.set("rule", buildMenuDrawerRules(entId, menu));
		return root;
	}

	/**
	 * 简化版 form-create 规则：覆盖菜单类型 M 的常见字段；复杂关联实体/看板与 PHP 完整表单可后续补齐。
	 */
	private ArrayNode buildMenuDrawerRules(Long entId, SystemMenus menu) {
		ArrayNode rules = objectMapper.createArrayNode();
		ArrayNode pathValue = menu == null ? pathTopDefault() : pathStringToValueArray(menu.getPath());
		ObjectNode cascader = objectMapper.createObjectNode();
		cascader.put("type", "cascader");
		cascader.put("field", "path");
		cascader.put("title", "选择上级菜单");
		cascader.set("value", pathValue);
		ObjectNode cProps = objectMapper.createObjectNode();
		cProps.set("options", menuTypeMCascaderOptions(entId));
		ObjectNode inner = objectMapper.createObjectNode();
		inner.put("checkStrictly", true);
		cProps.set("props", inner);
		cascader.set("props", cProps);
		cascader.put("col", 24);
		rules.add(cascader);

		rules.add(formSelectType(menu));
		rules.add(formInput("menu_name", "菜单名称", menu == null ? "" : StrUtil.nullToEmpty(menu.getMenuName()), true));
		rules.add(formInput("menu_path", "路由路径", menu == null ? "" : StrUtil.nullToEmpty(menu.getMenuPath()), false));
		rules.add(formInput("component", "前端路径", menu == null ? "" : StrUtil.nullToEmpty(menu.getComponent()), false));
		rules.add(formInput("api", "权限路由", menu == null ? "" : StrUtil.nullToEmpty(menu.getApi()), false));
		rules.add(formInput("methods", "请求方式", menu == null ? "" : StrUtil.nullToEmpty(menu.getMethods()), false));
		rules.add(
				formInput("unique_auth", "权限标识", menu == null ? "" : StrUtil.nullToEmpty(menu.getUniqueAuth()), false));
		rules.add(formNumber("sort", "排序", menu == null || menu.getSort() == null ? 0 : menu.getSort()));
		rules.add(formSwitch("is_show", "展示菜单", menu == null || menu.getIsShow() == null ? 1 : menu.getIsShow()));
		rules.add(formSwitch("status", "是否可用", menu == null || menu.getStatus() == null ? 1 : menu.getStatus()));
		return rules;
	}

	private ObjectNode formSelectType(SystemMenus menu) {
		ObjectNode r = objectMapper.createObjectNode();
		r.put("type", "select");
		r.put("field", "type");
		r.put("title", "权限类型");
		String v = menu == null || StrUtil.isBlank(menu.getType()) ? "M" : menu.getType();
		r.put("value", v);
		ArrayNode opts = objectMapper.createArrayNode();
		opts.add(selectOption("M", "菜单"));
		opts.add(selectOption("B", "按钮"));
		opts.add(selectOption("A", "接口"));
		ObjectNode p = objectMapper.createObjectNode();
		p.set("options", opts);
		r.set("props", p);
		r.put("col", 24);
		return r;
	}

	private ObjectNode selectOption(String value, String label) {
		ObjectNode o = objectMapper.createObjectNode();
		o.put("value", value);
		o.put("label", label);
		return o;
	}

	private ObjectNode formInput(String field, String title, String value, boolean required) {
		ObjectNode r = objectMapper.createObjectNode();
		r.put("type", "input");
		r.put("field", field);
		r.put("title", title);
		r.put("value", value);
		if (required) {
			ArrayNode val = objectMapper.createArrayNode();
			ObjectNode rule = objectMapper.createObjectNode();
			rule.put("required", true);
			rule.put("message", "必填项不能为空");
			val.add(rule);
			r.set("validate", val);
		}
		r.put("col", 24);
		return r;
	}

	private ObjectNode formNumber(String field, String title, int value) {
		ObjectNode r = objectMapper.createObjectNode();
		r.put("type", "inputNumber");
		r.put("field", field);
		r.put("title", title);
		r.put("value", value);
		ObjectNode p = objectMapper.createObjectNode();
		p.put("min", 0);
		p.put("max", 999999);
		r.set("props", p);
		r.put("col", 24);
		return r;
	}

	private ObjectNode formSwitch(String field, String title, int value) {
		ObjectNode r = objectMapper.createObjectNode();
		r.put("type", "switch");
		r.put("field", field);
		r.put("title", title);
		r.put("value", value);
		ObjectNode p = objectMapper.createObjectNode();
		p.put("activeValue", 1);
		p.put("inactiveValue", 0);
		r.set("props", p);
		r.put("col", 24);
		return r;
	}

	private ArrayNode pathTopDefault() {
		ArrayNode a = objectMapper.createArrayNode();
		a.add(0);
		return a;
	}

	private ArrayNode pathStringToValueArray(String pathStr) {
		ArrayNode a = objectMapper.createArrayNode();
		if (StrUtil.isBlank(pathStr) || "0".equals(pathStr.trim())) {
			a.add(0);
			return a;
		}
		for (String p : pathStr.split("/")) {
			if (StrUtil.isBlank(p)) {
				continue;
			}
			try {
				a.add(Long.parseLong(p.trim()));
			}
			catch (NumberFormatException ignored) {
			}
		}
		if (a.size() == 0) {
			a.add(0);
		}
		return a;
	}

	/**
	 * 上级菜单级联：仅 type=M，并带「顶级菜单」根节点（与 PHP 一致）。
	 */
	private ArrayNode menuTypeMCascaderOptions(Long entId) {
		List<SystemMenus> rows = baseMapper.selectList(Wrappers.lambdaQuery(SystemMenus.class)
			.eq(SystemMenus::getEntid, entId)
			.eq(SystemMenus::getType, "M")
			.isNull(SystemMenus::getDeletedAt)
			.orderByDesc(SystemMenus::getSort)
			.orderByAsc(SystemMenus::getId));
		Map<Long, ObjectNode> byId = new java.util.LinkedHashMap<>();
		for (SystemMenus m : rows) {
			ObjectNode n = objectMapper.createObjectNode();
			n.put("value", m.getId());
			n.put("label", StrUtil.nullToEmpty(m.getMenuName()));
			n.set("children", objectMapper.createArrayNode());
			byId.put(m.getId(), n);
		}
		ArrayNode treeRoots = objectMapper.createArrayNode();
		for (SystemMenus m : rows) {
			ObjectNode node = byId.get(m.getId());
			long pid = m.getPid() == null ? 0L : m.getPid();
			if (pid == 0L || !byId.containsKey(pid)) {
				treeRoots.add(node);
			}
			else {
				((ArrayNode) byId.get(pid).get("children")).add(node);
			}
		}
		ObjectNode top = objectMapper.createObjectNode();
		top.put("value", 0);
		top.put("label", "顶级菜单");
		top.set("children", treeRoots);
		ArrayNode opts = objectMapper.createArrayNode();
		opts.add(top);
		return opts;
	}

	@Override
	public void saveMenusForCompany(Long entId, JsonNode body) {
		if (body == null || body.isNull() || body.isMissingNode()) {
			return;
		}
		List<Long> rules = readLongArrayNode(body.get("rules"));
		List<Long> apis = readLongArrayNode(body.get("apis"));
		systemRoleService.saveEnterpriseSuperRole(entId, rules, apis);
	}

	private static List<Long> readLongArrayNode(JsonNode node) {
		List<Long> out = new ArrayList<>();
		if (node == null || !node.isArray()) {
			return out;
		}
		for (JsonNode n : node) {
			if (n.isIntegralNumber()) {
				out.add(n.longValue());
			}
		}
		return out;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMenuFromForm(Long entId, JsonNode body) {
		SystemMenus m = new SystemMenus();
		m.setEntid(entId);
		applyMenuFormBody(m, body, true);
		m.setCreatedAt(LocalDateTime.now());
		m.setUpdatedAt(LocalDateTime.now());
		baseMapper.insert(m);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMenuFromForm(Long id, Long entId, JsonNode body) {
		SystemMenus exist = baseMapper.selectOne(Wrappers.lambdaQuery(SystemMenus.class)
			.eq(SystemMenus::getId, id)
			.eq(SystemMenus::getEntid, entId)
			.isNull(SystemMenus::getDeletedAt));
		if (exist == null) {
			throw new IllegalArgumentException("修改的菜单不存在");
		}
		applyMenuFormBody(exist, body, false);
		exist.setId(id);
		exist.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(exist);
	}

	private void applyMenuFormBody(SystemMenus m, JsonNode body, boolean isCreate) {
		if (body.has("menu_name")) {
			m.setMenuName(body.get("menu_name").asText(""));
		}
		if (body.has("type") && !body.get("type").isNull()) {
			JsonNode t = body.get("type");
			m.setType(t.isTextual() ? t.asText("M") : String.valueOf(t.asInt()));
		}
		else if (isCreate) {
			m.setType("M");
		}
		if (body.has("api")) {
			m.setApi(body.get("api").asText(""));
		}
		if (body.has("methods")) {
			m.setMethods(body.get("methods").asText(""));
		}
		if (body.has("menu_path")) {
			m.setMenuPath(body.get("menu_path").asText(""));
		}
		if (body.has("component")) {
			m.setComponent(body.get("component").asText(""));
		}
		if (body.has("sort")) {
			m.setSort(body.get("sort").asInt());
		}
		if (body.has("is_show")) {
			m.setIsShow(body.get("is_show").asInt());
		}
		if (body.has("status")) {
			m.setStatus(body.get("status").asInt());
		}
		if (body.has("unique_auth")) {
			m.setUniqueAuth(body.get("unique_auth").asText(""));
		}
		if (body.has("menu_type")) {
			m.setMenuType(body.get("menu_type").asInt());
		}
		if (body.has("uni_path")) {
			m.setUniPath(body.get("uni_path").asText(""));
		}
		if (body.has("uni_img")) {
			m.setUniImg(body.get("uni_img").asText(""));
		}
		if (body.has("position")) {
			m.setPosition(body.get("position").asInt());
		}
		if (body.has("crud_id")) {
			m.setCrudId(body.get("crud_id").asInt());
		}
		String ua = m.getUniqueAuth();
		if (StrUtil.isBlank(ua)) {
			m.setUniqueAuth("menus" + System.nanoTime());
		}
		JsonNode pathNode = body.get("path");
		applyPathFromJson(m, pathNode);
		if (m.getStatus() == null) {
			m.setStatus(1);
		}
		if (m.getIsShow() == null) {
			m.setIsShow(1);
		}
		if (m.getSort() == null) {
			m.setSort(0);
		}
	}

	private void applyPathFromJson(SystemMenus m, JsonNode pathNode) {
		if (pathNode == null || !pathNode.isArray() || pathNode.size() == 0) {
			m.setPid(0L);
			m.setLevel(1);
			m.setPath("0");
			return;
		}
		List<Long> parts = new ArrayList<>();
		for (JsonNode n : pathNode) {
			if (n.isIntegralNumber()) {
				parts.add(n.longValue());
			}
			else if (n.isTextual()) {
				try {
					parts.add(Long.parseLong(n.asText().trim()));
				}
				catch (NumberFormatException ignored) {
				}
			}
		}
		while (!parts.isEmpty() && parts.get(0) == 0L) {
			parts.remove(0);
		}
		if (parts.isEmpty()) {
			m.setPid(0L);
			m.setLevel(1);
			m.setPath("0");
			return;
		}
		long pid = parts.get(parts.size() - 1);
		m.setPid(pid);
		m.setLevel(parts.size());
		m.setPath(parts.stream().map(String::valueOf).collect(Collectors.joining("/")));
	}

}
