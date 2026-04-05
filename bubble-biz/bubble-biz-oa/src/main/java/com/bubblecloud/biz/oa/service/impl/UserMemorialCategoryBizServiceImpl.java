package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.UserMemorialCategoryMapper;
import com.bubblecloud.biz.oa.mapper.UserMemorialMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.UserMemorialCategoryBizService;
import com.bubblecloud.biz.oa.util.OaFormRuleFactory;
import com.bubblecloud.biz.oa.util.TreeUtil;
import com.bubblecloud.oa.api.dto.memorial.MemorialCategorySaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.UserMemorial;
import com.bubblecloud.oa.api.entity.UserMemorialCategory;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.bubblecloud.oa.api.vo.memorial.MemorialCategoryIndexVO;
import com.bubblecloud.oa.api.vo.memorial.MemorialCategoryTreeNodeVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserMemorialCategoryBizServiceImpl implements UserMemorialCategoryBizService {

	private final UserMemorialCategoryMapper categoryMapper;

	private final UserMemorialMapper memorialMapper;

	private final AdminService adminService;

	private final ObjectMapper objectMapper;

	@Data
	private static class CascOpt {

		private Long value;

		private String label;

		private Long pid;

		private List<CascOpt> children = new ArrayList<>();

	}

	@Override
	public MemorialCategoryIndexVO index(Long adminId) {
		String uid = requireUid(adminId);
		ensureDefaultFolder(uid);
		long total = memorialMapper.selectCount(Wrappers.lambdaQuery(UserMemorial.class).eq(UserMemorial::getUid, uid));
		List<UserMemorialCategory> rows = categoryMapper.selectList(Wrappers.lambdaQuery(UserMemorialCategory.class)
			.eq(UserMemorialCategory::getUid, uid)
			.eq(UserMemorialCategory::getTypes, 1)
			.orderByAsc(UserMemorialCategory::getId));
		List<MemorialCategoryTreeNodeVO> flat = new ArrayList<>();
		for (UserMemorialCategory c : rows) {
			MemorialCategoryTreeNodeVO n = new MemorialCategoryTreeNodeVO();
			n.setId(c.getId());
			n.setName(c.getName());
			n.setPid(c.getPid());
			n.setCount(countMemorial(uid, c.getId()));
			flat.add(n);
		}
		List<MemorialCategoryTreeNodeVO> tree = TreeUtil.buildTree(flat, MemorialCategoryTreeNodeVO::getId,
				n -> n.getPid() == null ? 0L : n.getPid().longValue(), MemorialCategoryTreeNodeVO::getChildren);
		return new MemorialCategoryIndexVO(total, tree);
	}

	@Override
	public OaElFormVO createForm(Long adminId, long pid, ObjectMapper om) {
		String uid = requireUid(adminId);
		UserMemorialCategory root = ensureDefaultFolder(uid);
		ArrayNode val = om.createArrayNode();
		if (pid > 0) {
			UserMemorialCategory p = requireCategory(uid, pid);
			for (Integer x : parsePathList(p.getPath())) {
				val.add(x);
			}
			val.add(p.getId().intValue());
		}
		else {
			val.add(root.getId().intValue());
		}
		ArrayNode options = buildCascaderOptions(uid, null);
		return new OaElFormVO("添加备忘录文件夹", "post", "/ent/user/memorial_cate",
				OaFormRuleFactory.memorialCategoryRules(om, options, val, ""));
	}

	@Override
	public OaElFormVO editForm(Long adminId, long id, ObjectMapper om) {
		String uid = requireUid(adminId);
		UserMemorialCategory row = requireOwned(uid, id);
		ArrayNode val = om.createArrayNode();
		for (Integer x : parsePathList(row.getPath())) {
			val.add(x);
		}
		ArrayNode options = buildCascaderOptions(uid, row.getId());
		return new OaElFormVO("修改备忘录文件夹", "put", "/ent/user/memorial_cate/" + id,
				OaFormRuleFactory.memorialCategoryRules(om, options, val, row.getName()));
	}

	@Override
	public Long store(Long adminId, MemorialCategorySaveDTO dto) {
		String uid = requireUid(adminId);
		UserMemorialCategory root = ensureDefaultFolder(uid);
		dto.setUid(uid);
		int resolvedPid = resolvePid(uid, dto.getPath(), dto.getPid(), root);
		if (StrUtil.isBlank(dto.getName())) {
			throw new IllegalArgumentException("请输入文件夹名称");
		}
		String name = dto.getName().trim();
		long dup = categoryMapper.selectCount(Wrappers.lambdaQuery(UserMemorialCategory.class)
			.eq(UserMemorialCategory::getUid, uid)
			.eq(UserMemorialCategory::getPid, resolvedPid)
			.eq(UserMemorialCategory::getName, name));
		if (dup > 0) {
			throw new IllegalArgumentException("文件夹已存在，请勿重复添加");
		}
		UserMemorialCategory parent = categoryMapper.selectById((long) resolvedPid);
		if (ObjectUtil.isNull(parent)) {
			throw new IllegalArgumentException("父级文件夹获取异常");
		}
		List<Integer> pathArr = pathForNewUnder(parent);
		if (pathArr.size() > 2) {
			throw new IllegalArgumentException("文件夹最多可添加三级");
		}
		LocalDateTime now = LocalDateTime.now();
		UserMemorialCategory c = new UserMemorialCategory();
		c.setUid(uid);
		c.setPid(resolvedPid);
		c.setName(name);
		c.setTypes(1);
		c.setPath(writePath(pathArr));
		c.setCreatedAt(now);
		c.setUpdatedAt(now);
		categoryMapper.insert(c);
		return c.getId();
	}

	@Override
	public void update(Long adminId, long id, MemorialCategorySaveDTO dto) {
		String uid = requireUid(adminId);
		UserMemorialCategory row = requireOwned(uid, id);
		if (Integer.valueOf(0).equals(row.getTypes()) && dto.getPid() != null && dto.getPid() > 0) {
			throw new IllegalArgumentException("该文件夹不能修改前置文件夹");
		}
		UserMemorialCategory root = ensureDefaultFolder(uid);
		int newPid = resolvePid(uid, dto.getPath(), dto.getPid(), root);
		if (StrUtil.isBlank(dto.getName())) {
			throw new IllegalArgumentException("请输入文件夹名称");
		}
		String name = dto.getName().trim();
		long dup = categoryMapper.selectCount(Wrappers.lambdaQuery(UserMemorialCategory.class)
			.eq(UserMemorialCategory::getUid, uid)
			.eq(UserMemorialCategory::getPid, newPid)
			.eq(UserMemorialCategory::getName, name)
			.ne(UserMemorialCategory::getId, id));
		if (dup > 0) {
			throw new IllegalArgumentException("文件夹已存在，请勿重复添加");
		}
		UserMemorialCategory parent = categoryMapper.selectById((long) newPid);
		if (ObjectUtil.isNull(parent)) {
			throw new IllegalArgumentException("父级文件夹获取异常");
		}
		List<Integer> pathArr = pathForNewUnder(parent);
		if (pathArr.size() > 2) {
			throw new IllegalArgumentException("文件夹最多可添加三级");
		}
		row.setPid(newPid);
		row.setPath(writePath(pathArr));
		row.setName(name);
		row.setUpdatedAt(LocalDateTime.now());
		categoryMapper.updateById(row);
	}

	@Override
	public void delete(Long adminId, long id) {
		String uid = requireUid(adminId);
		UserMemorialCategory row = requireOwned(uid, id);
		if (Integer.valueOf(0).equals(row.getTypes())) {
			throw new IllegalArgumentException("该文件夹无法删除");
		}
		long mem = memorialMapper.selectCount(Wrappers.lambdaQuery(UserMemorial.class)
			.eq(UserMemorial::getUid, uid)
			.eq(UserMemorial::getPid, row.getId().intValue()));
		if (mem > 0) {
			throw new IllegalArgumentException("请先删除关联备忘录或文件夹,再尝试删除");
		}
		long sub = categoryMapper.selectCount(Wrappers.lambdaQuery(UserMemorialCategory.class)
			.eq(UserMemorialCategory::getUid, uid)
			.eq(UserMemorialCategory::getPid, row.getId().intValue()));
		if (sub > 0) {
			throw new IllegalArgumentException("请先删除关联备忘录或文件夹,再尝试删除");
		}
		categoryMapper.deleteById(id);
	}

	private String requireUid(Long adminId) {
		if (ObjectUtil.isNull(adminId)) {
			throw new IllegalArgumentException("用户未登录");
		}
		Admin admin = adminService.getById(adminId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		return admin.getUid();
	}

	private UserMemorialCategory ensureDefaultFolder(String uid) {
		UserMemorialCategory root = categoryMapper.selectOne(Wrappers.lambdaQuery(UserMemorialCategory.class)
			.eq(UserMemorialCategory::getUid, uid)
			.eq(UserMemorialCategory::getTypes, 0)
			.last("LIMIT 1"));
		if (ObjectUtil.isNotNull(root)) {
			return root;
		}
		LocalDateTime now = LocalDateTime.now();
		UserMemorialCategory c = new UserMemorialCategory();
		c.setUid(uid);
		c.setName("我的文件夹");
		c.setTypes(0);
		c.setPid(0);
		c.setPath("[]");
		c.setCreatedAt(now);
		c.setUpdatedAt(now);
		categoryMapper.insert(c);
		return c;
	}

	private UserMemorialCategory requireOwned(String uid, long id) {
		UserMemorialCategory row = categoryMapper.selectById(id);
		if (ObjectUtil.isNull(row) || !uid.equals(row.getUid())) {
			throw new IllegalArgumentException("记录不存在");
		}
		return row;
	}

	private UserMemorialCategory requireCategory(String uid, long id) {
		UserMemorialCategory row = categoryMapper.selectById(id);
		if (ObjectUtil.isNull(row) || !uid.equals(row.getUid())) {
			throw new IllegalArgumentException("父级文件夹获取异常");
		}
		return row;
	}

	private int resolvePid(String uid, List<Integer> path, Integer pidParam, UserMemorialCategory root) {
		if (path != null && !path.isEmpty()) {
			int last = path.get(path.size() - 1);
			requireCategory(uid, last);
			return last;
		}
		if (pidParam != null && pidParam > 0) {
			requireCategory(uid, pidParam);
			return pidParam;
		}
		return root.getId().intValue();
	}

	private List<Integer> pathForNewUnder(UserMemorialCategory parent) {
		if (parent.getTypes() != null && parent.getTypes() == 0) {
			return new ArrayList<>();
		}
		List<Integer> p = new ArrayList<>(parsePathList(parent.getPath()));
		if (parent.getTypes() != null && parent.getTypes() == 1) {
			p.add(parent.getId().intValue());
		}
		return p;
	}

	private List<Integer> parsePathList(String raw) {
		if (StrUtil.isBlank(raw)) {
			return new ArrayList<>();
		}
		try {
			List<Integer> list = objectMapper.readValue(raw, new TypeReference<List<Integer>>() {
			});
			return list == null ? new ArrayList<>() : new ArrayList<>(list);
		}
		catch (Exception e) {
			return new ArrayList<>();
		}
	}

	private String writePath(List<Integer> path) {
		try {
			return objectMapper.writeValueAsString(path == null ? List.of() : path);
		}
		catch (Exception e) {
			return "[]";
		}
	}

	private long countMemorial(String uid, Long cateId) {
		return memorialMapper.selectCount(Wrappers.lambdaQuery(UserMemorial.class)
			.eq(UserMemorial::getUid, uid)
			.eq(UserMemorial::getPid, cateId.intValue()));
	}

	private ArrayNode buildCascaderOptions(String uid, Long excludeId) {
		UserMemorialCategory root = ensureDefaultFolder(uid);
		List<CascOpt> flat = new ArrayList<>();
		CascOpt r = new CascOpt();
		r.setValue(root.getId());
		r.setLabel("我的文件夹");
		r.setPid(0L);
		flat.add(r);
		List<UserMemorialCategory> list = categoryMapper.selectList(Wrappers.lambdaQuery(UserMemorialCategory.class)
			.eq(UserMemorialCategory::getUid, uid)
			.eq(UserMemorialCategory::getTypes, 1)
			.orderByAsc(UserMemorialCategory::getId));
		for (UserMemorialCategory c : list) {
			if (excludeId != null && excludeId.equals(c.getId())) {
				continue;
			}
			CascOpt o = new CascOpt();
			o.setValue(c.getId());
			o.setLabel(c.getName());
			long ppid = c.getPid() == null ? root.getId() : c.getPid().longValue();
			o.setPid(ppid);
			flat.add(o);
		}
		List<CascOpt> tree = TreeUtil.buildTree(flat, CascOpt::getValue, CascOpt::getPid, CascOpt::getChildren);
		return toCascaderJson(tree);
	}

	private ArrayNode toCascaderJson(List<CascOpt> roots) {
		ArrayNode arr = objectMapper.createArrayNode();
		if (roots == null) {
			return arr;
		}
		for (CascOpt n : roots) {
			ObjectNode o = objectMapper.createObjectNode();
			o.put("label", n.getLabel());
			o.put("value", n.getValue());
			if (n.getChildren() != null && !n.getChildren().isEmpty()) {
				o.set("children", toCascaderJson(n.getChildren()));
			}
			arr.add(o);
		}
		return arr;
	}

}
