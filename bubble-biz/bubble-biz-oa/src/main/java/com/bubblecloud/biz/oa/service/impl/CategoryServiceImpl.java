package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CategoryMapper;
import com.bubblecloud.biz.oa.service.CategoryService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Category;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

/**
 * 快捷入口分类实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends UpServiceImpl<CategoryMapper, Category> implements CategoryService {

	private static final String TYPE_QUICK = "quickConfig";

	private final ObjectMapper objectMapper;

	@Override
	public List<Category> list(Long entId) {
		return baseMapper.selectList(Wrappers.lambdaQuery(Category.class)
			.eq(Category::getType, TYPE_QUICK)
			.eq(Category::getEntid, entId)
			.orderByDesc(Category::getSort)
			.orderByAsc(Category::getId));
	}

	@Override
	public List<Category> listVisibleQuickCategories(long entId) {
		return baseMapper.selectList(Wrappers.lambdaQuery(Category.class)
			.eq(Category::getType, TYPE_QUICK)
			.eq(Category::getEntid, (int) entId)
			.eq(Category::getIsShow, 1)
			.orderByDesc(Category::getSort)
			.orderByAsc(Category::getId));
	}

	@Override
	public OaElFormVO buildQuickCategoryCreateForm(long entId) {
		ArrayNode rules = objectMapper.createArrayNode();
		rules.add(hiddenInt(objectMapper, "pid", 0));
		rules.add(quickCateInput(objectMapper, "cate_name", "分类名称", "", true, 20));
		rules.add(hiddenStr(objectMapper, "type", TYPE_QUICK));
		rules.add(hiddenInt(objectMapper, "entid", (int) entId));
		rules.add(quickCateNumber(objectMapper, "sort", "排序", 0));
		return new OaElFormVO("创建配置分类", "post", "/ent/config/quickCate", rules);
	}

	private static ObjectNode hiddenInt(ObjectMapper om, String field, int value) {
		ObjectNode n = om.createObjectNode();
		n.put("type", "hidden");
		n.put("field", field);
		n.put("value", value);
		return n;
	}

	private static ObjectNode hiddenStr(ObjectMapper om, String field, String value) {
		ObjectNode n = om.createObjectNode();
		n.put("type", "hidden");
		n.put("field", field);
		n.put("value", value);
		return n;
	}

	private static ObjectNode quickCateInput(ObjectMapper om, String field, String title, String value,
			boolean required, int maxLen) {
		ObjectNode n = om.createObjectNode();
		n.put("type", "input");
		n.put("field", field);
		n.put("title", title);
		n.put("value", StrUtil.nullToEmpty(value));
		ObjectNode p = om.createObjectNode();
		p.put("maxlength", maxLen);
		p.put("placeholder", "请输入分类名称");
		n.set("props", p);
		if (required) {
			ArrayNode val = om.createArrayNode();
			ObjectNode rule = om.createObjectNode();
			rule.put("required", true);
			rule.put("message", "必填项不能为空");
			val.add(rule);
			n.set("validate", val);
		}
		n.put("col", 24);
		return n;
	}

	private static ObjectNode quickCateNumber(ObjectMapper om, String field, String title, int value) {
		ObjectNode n = om.createObjectNode();
		n.put("type", "inputNumber");
		n.put("field", field);
		n.put("title", title);
		n.put("value", value);
		ObjectNode p = om.createObjectNode();
		p.put("min", 0);
		p.put("max", 999999);
		n.set("props", p);
		n.put("col", 24);
		return n;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Category req) {
		req.setType(TYPE_QUICK);
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Category req) {
		return super.update(req);
	}

}
