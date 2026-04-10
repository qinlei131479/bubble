package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.bubblecloud.biz.oa.mapper.SystemQuickMapper;
import com.bubblecloud.biz.oa.service.CategoryService;
import com.bubblecloud.biz.oa.service.SystemQuickService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Category;
import com.bubblecloud.oa.api.entity.SystemQuick;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 快捷入口实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
public class SystemQuickServiceImpl extends UpServiceImpl<SystemQuickMapper, SystemQuick>
		implements SystemQuickService {

	private final CategoryService categoryService;

	private final ObjectMapper objectMapper;

	@Override
	public OaElFormVO buildCreateForm(long entId, Integer defaultCid) {
		long cid = defaultCid == null ? 0L : defaultCid.longValue();
		ArrayNode rules = objectMapper.createArrayNode();
		rules.add(quickCidSelect(entId, cid));
		rules.add(quickInput("name", "标题名称", "", true, 50, "请输入标题"));
		rules.add(quickInput("pc_url", "PC端地址", "", true, 120, "非正确路径不会显示"));
		rules.add(quickInput("uni_url", "移动端地址", "", false, 120, "非正确路径不会显示"));
		rules.add(quickInput("image", "图标地址", "", false, 500, "图片 URL"));
		rules.add(quickRadio("pc_show", "PC端显示", 1, optionsHideShow()));
		rules.add(quickRadio("uni_show", "移动端显示", 1, optionsHideShow()));
		rules.add(quickSort(1));
		rules.add(quickRadio("status", "状态", 1, optionsStatus()));
		return new OaElFormVO("添加数据", "post", "/ent/config/quick", rules);
	}

	@Override
	public OaElFormVO buildEditForm(long entId, int id) {
		SystemQuick row = baseMapper.selectById(id);
		if (ObjectUtil.isNull(row)) {
			throw new IllegalArgumentException("修改的快捷入口数据不存在");
		}
		ArrayNode rules = objectMapper.createArrayNode();
		rules.add(quickCidSelect(entId, ObjectUtil.defaultIfNull(row.getCid(), 0).longValue()));
		rules.add(quickInput("name", "标题名称", StrUtil.nullToEmpty(row.getName()), true, 50, "请输入标题"));
		rules.add(quickInput("pc_url", "PC端地址", StrUtil.nullToEmpty(row.getPcUrl()), true, 120, "非正确路径不会显示"));
		rules.add(quickInput("uni_url", "移动端地址", StrUtil.nullToEmpty(row.getUniUrl()), false, 120, "非正确路径不会显示"));
		rules.add(quickInput("image", "图标地址", StrUtil.nullToEmpty(row.getImage()), false, 500, "图片 URL"));
		rules.add(quickRadio("pc_show", "PC端显示", ObjectUtil.defaultIfNull(row.getPcShow(), 1), optionsHideShow()));
		rules.add(quickRadio("uni_show", "移动端显示", ObjectUtil.defaultIfNull(row.getUniShow(), 1), optionsHideShow()));
		rules.add(quickSort(ObjectUtil.defaultIfNull(row.getSort(), 1)));
		rules.add(quickRadio("status", "状态", ObjectUtil.defaultIfNull(row.getStatus(), 1), optionsStatus()));
		return new OaElFormVO("修改数据", "put", "/ent/config/quick/" + id, rules);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateShowStatus(int id, int status) {
		SystemQuick row = baseMapper.selectById(id);
		if (ObjectUtil.isNull(row)) {
			throw new IllegalArgumentException("数据不存在");
		}
		row.setStatus(status);
		row.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(SystemQuick req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(SystemQuick req) {
		return super.update(req);
	}

	private ObjectNode quickCidSelect(long entId, long value) {
		ObjectNode r = objectMapper.createObjectNode();
		r.put("type", "select");
		r.put("field", "cid");
		r.put("title", "分类");
		r.put("value", value);
		r.put("col", 24);
		ArrayNode opts = objectMapper.createArrayNode();
		List<Category> cats = categoryService.listVisibleQuickCategories(entId);
		for (Category c : cats) {
			ObjectNode o = objectMapper.createObjectNode();
			o.put("label", StrUtil.nullToEmpty(c.getCateName()));
			o.put("value", c.getId() == null ? 0L : c.getId());
			opts.add(o);
		}
		ObjectNode p = objectMapper.createObjectNode();
		p.set("options", opts);
		r.set("props", p);
		ArrayNode val = objectMapper.createArrayNode();
		ObjectNode rule = objectMapper.createObjectNode();
		rule.put("required", true);
		rule.put("message", "请选择分类");
		rule.put("type", "number");
		val.add(rule);
		r.set("validate", val);
		return r;
	}

	private ObjectNode quickInput(String field, String title, String value, boolean required, int maxLen,
			String placeholder) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "input");
		n.put("field", field);
		n.put("title", title);
		n.put("value", value);
		ObjectNode p = objectMapper.createObjectNode();
		p.put("maxlength", maxLen);
		p.put("placeholder", placeholder);
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

	private ObjectNode quickSort(int value) {
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

	private ObjectNode quickRadio(String field, String title, int value, ArrayNode options) {
		ObjectNode r = objectMapper.createObjectNode();
		r.put("type", "radio");
		r.put("field", field);
		r.put("title", title);
		r.put("value", value);
		r.put("$required", true);
		r.set("options", options);
		r.put("col", 24);
		return r;
	}

	private ArrayNode optionsHideShow() {
		ArrayNode opts = objectMapper.createArrayNode();
		opts.add(radioOpt(0, "隐藏"));
		opts.add(radioOpt(1, "显示"));
		return opts;
	}

	private ArrayNode optionsStatus() {
		ArrayNode opts = objectMapper.createArrayNode();
		opts.add(radioOpt(1, "显示"));
		opts.add(radioOpt(0, "隐藏"));
		return opts;
	}

	private ObjectNode radioOpt(int value, String label) {
		ObjectNode o = objectMapper.createObjectNode();
		o.put("value", value);
		o.put("label", label);
		return o;
	}

}
