package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CategoryMapper;
import com.bubblecloud.biz.oa.mapper.SystemAttachMapper;
import com.bubblecloud.biz.oa.service.AttachCateAdminService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Category;
import com.bubblecloud.oa.api.entity.SystemAttach;
import com.bubblecloud.oa.api.vo.CategoryAttachTreeVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 附件分类实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
@RequiredArgsConstructor
public class AttachCateAdminServiceImpl extends UpServiceImpl<CategoryMapper, Category>
		implements AttachCateAdminService {

	private static final String TYPE_ATTACH = "systemAttach";

	private final SystemAttachMapper systemAttachMapper;

	private final ObjectMapper objectMapper;

	@Override
	public List<CategoryAttachTreeVO> attachCateTree(int entid) {
		List<Category> flat = list(Wrappers.lambdaQuery(Category.class)
			.eq(Category::getType, TYPE_ATTACH)
			.eq(Category::getEntid, entid)
			.orderByAsc(Category::getSort)
			.orderByDesc(Category::getId));
		Map<Long, CategoryAttachTreeVO> map = new HashMap<>();
		for (Category c : flat) {
			map.put(c.getId(), toNode(c));
		}
		List<CategoryAttachTreeVO> roots = new ArrayList<>();
		for (Category c : flat) {
			CategoryAttachTreeVO node = map.get(c.getId());
			int pid = ObjectUtil.defaultIfNull(c.getPid(), 0);
			if (pid <= 0) {
				roots.add(node);
			}
			else {
				CategoryAttachTreeVO parent = map.get((long) pid);
				if (ObjectUtil.isNotNull(parent)) {
					parent.getChildren().add(node);
				}
				else {
					roots.add(node);
				}
			}
		}
		return roots;
	}

	private static CategoryAttachTreeVO toNode(Category c) {
		CategoryAttachTreeVO v = new CategoryAttachTreeVO();
		v.setId(c.getId());
		v.setPid(c.getPid());
		v.setCateName(c.getCateName());
		v.setPath(c.getPath());
		v.setSort(c.getSort());
		v.setPic(c.getPic());
		v.setIsShow(c.getIsShow());
		v.setLevel(c.getLevel());
		v.setType(c.getType());
		v.setKeyword(c.getKeyword());
		v.setEntid(c.getEntid());
		v.setChildren(new ArrayList<>());
		return v;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteAttachCategory(Long id, int entid) {
		if (ObjectUtil.isNull(id)) {
			throw new IllegalArgumentException("参数错误");
		}
		Category row = getById(id);
		if (ObjectUtil.isNull(row) || row.getEntid() == null || row.getEntid() != entid) {
			throw new IllegalArgumentException("没有查询到数据！");
		}
		long cnt = systemAttachMapper.selectCount(Wrappers.lambdaQuery(SystemAttach.class)
			.eq(SystemAttach::getCid, id.intValue())
			.eq(SystemAttach::getEntid, entid));
		if (cnt > 0) {
			throw new IllegalArgumentException("删除失败，该分类下有内容！");
		}
		removeById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Category req) {
		req.setType(TYPE_ATTACH);
		if (ObjectUtil.isNull(req.getEntid())) {
			req.setEntid(1);
		}
		if (ObjectUtil.isNull(req.getPid())) {
			req.setPid(0);
		}
		if (ObjectUtil.isNull(req.getSort())) {
			req.setSort(0);
		}
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Category req) {
		req.setType(TYPE_ATTACH);
		return super.update(req);
	}

	@Override
	public OaElFormVO buildAttachCateCreateForm(int entid) {
		ArrayNode rules = objectMapper.createArrayNode();
		rules.add(pidSelect(entid, 0));
		rules.add(textInput("cate_name", "分类名称", "", true, 20));
		rules.add(textInput("pic", "分类图标", "", false, 500));
		rules.add(hiddenStr("type", TYPE_ATTACH));
		rules.add(hiddenInt("entid", entid));
		rules.add(sortNumber(0));
		rules.add(isShowSwitch(1));
		return new OaElFormVO("创建分类", "post", "/ent/system/attach_cate", rules);
	}

	private ObjectNode pidSelect(int entid, long defaultPid) {
		ObjectNode r = objectMapper.createObjectNode();
		r.put("type", "select");
		r.put("field", "pid");
		r.put("title", "父级分类");
		r.put("value", defaultPid);
		r.put("col", 24);
		ArrayNode opts = objectMapper.createArrayNode();
		opts.add(selectOption(0L, "顶级分类"));
		List<Category> flat = list(Wrappers.lambdaQuery(Category.class)
			.eq(Category::getType, TYPE_ATTACH)
			.eq(Category::getEntid, entid)
			.orderByAsc(Category::getSort)
			.orderByAsc(Category::getId));
		for (Category c : flat) {
			if (c.getId() == null) {
				continue;
			}
			opts.add(selectOption(c.getId(), StrUtil.nullToEmpty(c.getCateName())));
		}
		ObjectNode p = objectMapper.createObjectNode();
		p.set("options", opts);
		r.set("props", p);
		return r;
	}

	private ObjectNode selectOption(long value, String label) {
		ObjectNode o = objectMapper.createObjectNode();
		o.put("value", value);
		o.put("label", label);
		return o;
	}

	private ObjectNode textInput(String field, String title, String value, boolean required, int maxLen) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "input");
		n.put("field", field);
		n.put("title", title);
		n.put("value", value);
		ObjectNode p = objectMapper.createObjectNode();
		p.put("maxlength", maxLen);
		n.set("props", p);
		n.put("col", 24);
		if (required) {
			ArrayNode val = objectMapper.createArrayNode();
			ObjectNode rule = objectMapper.createObjectNode();
			rule.put("required", true);
			rule.put("message", "必填项不能为空");
			val.add(rule);
			n.set("validate", val);
		}
		return n;
	}

	private ObjectNode hiddenStr(String field, String value) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "hidden");
		n.put("field", field);
		n.put("value", value);
		return n;
	}

	private ObjectNode hiddenInt(String field, int value) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "hidden");
		n.put("field", field);
		n.put("value", value);
		return n;
	}

	private ObjectNode sortNumber(int value) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "inputNumber");
		n.put("field", "sort");
		n.put("title", "排序");
		n.put("value", value);
		ObjectNode p = objectMapper.createObjectNode();
		p.put("min", 0);
		p.put("max", 999999);
		n.set("props", p);
		n.put("col", 24);
		return n;
	}

	private ObjectNode isShowSwitch(int value) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "switch");
		n.put("field", "is_show");
		n.put("title", "状态");
		n.put("value", value);
		ObjectNode p = objectMapper.createObjectNode();
		p.put("activeValue", 1);
		p.put("inactiveValue", 0);
		p.put("activeText", "开启");
		p.put("inactiveText", "关闭");
		n.set("props", p);
		n.put("col", 24);
		return n;
	}

}
