package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.BillCategoryMapper;
import com.bubblecloud.biz.oa.mapper.BillListMapper;
import com.bubblecloud.biz.oa.service.BillCategoryService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.BillCategory;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 财务分类实现。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Service
public class BillCategoryServiceImpl extends UpServiceImpl<BillCategoryMapper, BillCategory>
		implements BillCategoryService {

	@Autowired
	private BillListMapper billListMapper;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public List<BillCategory> searchCate(Integer anchorId, int types, long entid) {
		if (ObjectUtil.isNull(anchorId) || anchorId == 0) {
			return baseMapper.selectList(Wrappers.lambdaQuery(BillCategory.class)
				.eq(BillCategory::getEntid, entid)
				.eq(BillCategory::getTypes, types)
				.eq(BillCategory::getPid, 0)
				.orderByAsc(BillCategory::getSort)
				.orderByAsc(BillCategory::getId));
		}
		List<BillCategory> children = baseMapper.selectList(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getEntid, entid)
			.eq(BillCategory::getTypes, types)
			.eq(BillCategory::getPid, anchorId)
			.orderByAsc(BillCategory::getSort)
			.orderByAsc(BillCategory::getId));
		if (CollUtil.isNotEmpty(children)) {
			return children;
		}
		return baseMapper.selectList(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getEntid, entid)
			.eq(BillCategory::getTypes, types)
			.eq(BillCategory::getId, anchorId.longValue()));
	}

	@Override
	public List<Integer> subtreeInclusiveIds(int rootId, long entid, int types) {
		List<BillCategory> all = baseMapper.selectList(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getEntid, entid)
			.eq(BillCategory::getTypes, types));
		if (rootId <= 0) {
			return all.stream().map(c -> c.getId().intValue()).collect(Collectors.toList());
		}
		Set<Integer> out = new LinkedHashSet<>();
		out.add(rootId);
		boolean growing = true;
		while (growing) {
			growing = false;
			int before = out.size();
			for (BillCategory c : all) {
				if (c.getPid() != null && out.contains(c.getPid())) {
					out.add(c.getId().intValue());
				}
			}
			if (out.size() > before) {
				growing = true;
			}
		}
		return new ArrayList<>(out);
	}

	@Override
	public ArrayNode buildBillCascaderOptions(int types, long excludeCategoryId, long entid) {
		List<BillCategory> all = baseMapper.selectList(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getEntid, entid)
			.eq(BillCategory::getTypes, types)
			.orderByAsc(BillCategory::getSort)
			.orderByAsc(BillCategory::getId));
		Set<Long> blocked = new HashSet<>();
		if (excludeCategoryId > 0) {
			for (Integer id : subtreeInclusiveIds((int) excludeCategoryId, entid, types)) {
				blocked.add(id.longValue());
			}
		}
		List<BillCategory> nodes = all.stream().filter(c -> !blocked.contains(c.getId())).collect(Collectors.toList());
		return toCascaderTree(nodes);
	}

	@Override
	public OaElFormVO buildCategoryCreateForm(long entid) {
		ArrayNode rules = objectMapper.createArrayNode();
		ObjectNode typesField = objectMapper.createObjectNode();
		typesField.put("type", "radio");
		typesField.put("field", "types");
		typesField.put("title", "分类类型");
		typesField.put("value", 0);
		typesField.put("disabled", true);
		ArrayNode opts = objectMapper.createArrayNode();
		opts.addObject().put("value", 1).put("label", "收入");
		opts.addObject().put("value", 0).put("label", "支出");
		typesField.set("options", opts);
		ArrayNode control = objectMapper.createArrayNode();
		control.add(buildCascaderControlBranch(0, entid, 0L, List.of()));
		control.add(buildCascaderControlBranch(1, entid, 0L, List.of()));
		typesField.set("control", control);
		rules.add(typesField);
		rules.add(textField("name", "分类名称", "", true));
		rules.add(numberField("sort", "排序", 0, 0, 999999));
		return new OaElFormVO("添加财务流水类别", "POST", "/ent/bill_cate", rules);
	}

	@Override
	public OaElFormVO buildCategoryEditForm(long id, long entid) {
		BillCategory cat = baseMapper.selectById(id);
		if (ObjectUtil.isNull(cat) || cat.getEntid() == null || cat.getEntid().longValue() != entid) {
			throw new IllegalArgumentException("修改的财务流水类别不存在");
		}
		List<Integer> pathIds = parsePathToIds(cat.getPath());
		ArrayNode rules = objectMapper.createArrayNode();
		ObjectNode typesField = objectMapper.createObjectNode();
		typesField.put("type", "radio");
		typesField.put("field", "types");
		typesField.put("title", "分类类型");
		typesField.put("value", cat.getTypes() == null ? 0 : cat.getTypes());
		typesField.put("disabled", true);
		ArrayNode opts = objectMapper.createArrayNode();
		opts.addObject().put("value", 1).put("label", "收入");
		opts.addObject().put("value", 0).put("label", "支出");
		typesField.set("options", opts);
		ArrayNode control = objectMapper.createArrayNode();
		control.add(buildCascaderControlBranch(0, entid, id, pathIds));
		control.add(buildCascaderControlBranch(1, entid, id, pathIds));
		typesField.set("control", control);
		rules.add(typesField);
		rules.add(textField("name", "分类名称", StrUtil.nullToEmpty(cat.getName()), true));
		rules.add(numberField("sort", "排序", ObjectUtil.defaultIfNull(cat.getSort(), 0), 0, 999999));
		return new OaElFormVO("修改财务流水类别", "PUT", "/ent/bill_cate/" + id, rules);
	}

	private ObjectNode buildCascaderControlBranch(int typeVal, long entid, long excludeId, List<Integer> pathValue) {
		ObjectNode branch = objectMapper.createObjectNode();
		branch.put("value", typeVal);
		ArrayNode innerRules = objectMapper.createArrayNode();
		ObjectNode cascader = objectMapper.createObjectNode();
		cascader.put("type", "cascader");
		cascader.put("field", "path");
		cascader.put("title", "前置分类");
		ArrayNode pArr = objectMapper.createArrayNode();
		for (Integer p : pathValue) {
			pArr.add(p);
		}
		cascader.set("value", pArr);
		cascader.set("options", buildBillCascaderOptions(typeVal, excludeId, entid));
		ObjectNode props = objectMapper.createObjectNode();
		ObjectNode inner = objectMapper.createObjectNode();
		inner.put("checkStrictly", true);
		props.set("props", inner);
		cascader.set("props", props);
		cascader.put("clearable", true);
		innerRules.add(cascader);
		branch.set("rule", innerRules);
		return branch;
	}

	private ObjectNode textField(String field, String title, String value, boolean required) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "input");
		n.put("field", field);
		n.put("title", title);
		n.put("value", value);
		if (required) {
			ObjectNode p = objectMapper.createObjectNode();
			p.put("required", true);
			n.set("props", p);
		}
		return n;
	}

	private ObjectNode numberField(String field, String title, int value, int min, int max) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "inputNumber");
		n.put("field", field);
		n.put("title", title);
		n.put("value", value);
		ObjectNode p = objectMapper.createObjectNode();
		p.put("min", min);
		p.put("max", max);
		p.put("precision", 0);
		n.set("props", p);
		return n;
	}

	private List<Integer> parsePathToIds(String path) {
		if (StrUtil.isBlank(path)) {
			return List.of();
		}
		String p = path.trim();
		if (p.startsWith("/")) {
			p = p.substring(1);
		}
		if (p.endsWith("/")) {
			p = p.substring(0, p.length() - 1);
		}
		if (StrUtil.isBlank(p)) {
			return List.of();
		}
		List<Integer> out = new ArrayList<>();
		for (String s : StrUtil.splitTrim(p, "/")) {
			try {
				out.add(Integer.parseInt(s));
			}
			catch (NumberFormatException ignored) {
			}
		}
		return out;
	}

	private ArrayNode toCascaderTree(List<BillCategory> nodes) {
		Map<Long, List<BillCategory>> byPid = new HashMap<>();
		for (BillCategory c : nodes) {
			long pid = c.getPid() == null ? 0L : c.getPid().longValue();
			byPid.computeIfAbsent(pid, k -> new ArrayList<>()).add(c);
		}
		ArrayNode root = objectMapper.createArrayNode();
		List<BillCategory> roots = byPid.getOrDefault(0L, List.of());
		for (BillCategory r : roots) {
			root.add(buildCascaderNode(r, byPid));
		}
		return root;
	}

	private ObjectNode buildCascaderNode(BillCategory c, Map<Long, List<BillCategory>> byPid) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("label", c.getName());
		n.put("value", c.getId());
		List<BillCategory> children = byPid.getOrDefault(c.getId(), List.of());
		if (CollUtil.isNotEmpty(children)) {
			ArrayNode ch = objectMapper.createArrayNode();
			for (BillCategory x : children) {
				ch.add(buildCascaderNode(x, byPid));
			}
			n.set("children", ch);
		}
		return n;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(BillCategory dto) {
		normalizePath(dto);
		long dup = count(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getPid, dto.getPid())
			.eq(BillCategory::getEntid, dto.getEntid())
			.eq(BillCategory::getName, dto.getName()));
		if (dup > 0) {
			throw new IllegalArgumentException("分类已存在，请勿重复添加");
		}
		dto.setCateNo(generateNo(ObjectUtil.defaultIfNull(dto.getPid(), 0), dto.getEntid()));
		dto.setLevel(calcLevel(dto.getPath()));
		dto.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		dto.setContactId(ObjectUtil.defaultIfNull(dto.getContactId(), 0));
		return super.create(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(BillCategory dto) {
		BillCategory existing = baseMapper.selectById(dto.getId());
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		normalizePath(dto);
		long dup = count(Wrappers.lambdaQuery(BillCategory.class)
			.ne(BillCategory::getId, dto.getId())
			.eq(BillCategory::getPid, dto.getPid())
			.eq(BillCategory::getEntid, dto.getEntid())
			.eq(BillCategory::getName, dto.getName())
			.eq(BillCategory::getTypes, existing.getTypes()));
		if (dup > 0) {
			throw new IllegalArgumentException("分类已存在，请勿重复添加");
		}
		if (ObjectUtil.equal(dto.getPid(), dto.getId().intValue())) {
			throw new IllegalArgumentException("前置分类不能为自己");
		}
		dto.setLevel(calcLevel(dto.getPath()));
		if (StrUtil.isBlank(existing.getCateNo()) || !StrUtil.equals(dto.getPath(), existing.getPath())) {
			dto.setCateNo(generateNo(ObjectUtil.defaultIfNull(dto.getPid(), 0), dto.getEntid()));
		}
		else {
			dto.setCateNo(existing.getCateNo());
		}
		dto.setTypes(existing.getTypes());
		return super.update(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeById(java.io.Serializable id) {
		long used = billListMapper.selectCount(Wrappers.lambdaQuery(com.bubblecloud.oa.api.entity.BillList.class)
			.eq(com.bubblecloud.oa.api.entity.BillList::getCateId, id));
		if (used > 0) {
			throw new IllegalArgumentException("该财务分类已经被使用，不可删除！");
		}
		return super.removeById(id);
	}

	private void normalizePath(BillCategory dto) {
		if (StrUtil.isBlank(dto.getPath())) {
			dto.setPath("");
			dto.setPid(0);
			return;
		}
		String p = dto.getPath().trim();
		if (!p.startsWith("/")) {
			p = "/" + p;
		}
		if (!p.endsWith("/")) {
			p = p + "/";
		}
		dto.setPath(p);
		List<String> parts = StrUtil.splitTrim(p, "/");
		if (!parts.isEmpty()) {
			try {
				dto.setPid(Integer.parseInt(parts.get(parts.size() - 1)));
			}
			catch (NumberFormatException e) {
				dto.setPid(0);
			}
		}
		else {
			dto.setPid(0);
		}
	}

	private int calcLevel(String path) {
		if (StrUtil.isBlank(path)) {
			return 1;
		}
		return StrUtil.splitTrim(path, '/').size();
	}

	private String generateNo(int pid, Long entid) {
		String parentNo = "";
		if (pid > 0) {
			BillCategory p = baseMapper.selectById((long) pid);
			if (ObjectUtil.isNull(p)) {
				throw new IllegalArgumentException("分类信息获取异常");
			}
			parentNo = StrUtil.nullToEmpty(p.getCateNo());
		}
		long n = count(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getPid, pid)
			.eq(BillCategory::getEntid, entid));
		return parentNo + String.format("%02d", n + 1);
	}

}
