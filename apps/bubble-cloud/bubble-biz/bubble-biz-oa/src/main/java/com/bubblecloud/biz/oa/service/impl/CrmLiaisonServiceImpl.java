package com.bubblecloud.biz.oa.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CustomerLiaisonMapper;
import com.bubblecloud.biz.oa.service.CrmLiaisonService;
import com.bubblecloud.biz.oa.service.FormDataService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.CustomerLiaison;
import com.bubblecloud.oa.api.vo.form.FormCateListItemVO;
import com.bubblecloud.oa.api.vo.form.FormDataItemVO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 客户联系人实现。
 *
 * @author qinlei
 * @date 2026/4/3 13:00
 */
@Service
@RequiredArgsConstructor
public class CrmLiaisonServiceImpl extends UpServiceImpl<CustomerLiaisonMapper, CustomerLiaison>
		implements CrmLiaisonService {

	private final FormDataService formDataService;

	private final ObjectMapper objectMapper;

	@Override
	public List<CustomerLiaison> listByEid(int eid) {
		return baseMapper.selectList(Wrappers.lambdaQuery(CustomerLiaison.class)
			.eq(CustomerLiaison::getEid, eid)
			.isNull(CustomerLiaison::getDeletedAt)
			.orderByDesc(CustomerLiaison::getId));
	}

	@Override
	public ArrayNode createForm() {
		return objectMapper.valueToTree(formDataService.listByTypes(3));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long store(int eid, JsonNode body, Long adminId) {
		CustomerLiaison row = new CustomerLiaison();
		applyJson(body, row);
		row.setEid(eid);
		row.setUid(adminId.intValue());
		row.setCreatorUid(adminId.intValue());
		baseMapper.insert(row);
		return row.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(Long id, JsonNode body) {
		CustomerLiaison existing = baseMapper.selectById(id);
		if (ObjectUtil.isNull(existing) || ObjectUtil.isNotNull(existing.getDeletedAt())) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		CustomerLiaison patch = new CustomerLiaison();
		applyJson(body, patch);
		patch.setId(id);
		BeanUtil.copyProperties(patch, existing, CopyOptions.create().ignoreNullValue().ignoreError());
		baseMapper.updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void softDelete(Long id) {
		LambdaUpdateWrapper<CustomerLiaison> uw = Wrappers.lambdaUpdate(CustomerLiaison.class)
			.eq(CustomerLiaison::getId, id)
			.set(CustomerLiaison::getDeletedAt, LocalDateTime.now());
		baseMapper.update(null, uw);
	}

	@Override
	public CustomerLiaison info(Long id) {
		CustomerLiaison row = baseMapper.selectById(id);
		if (ObjectUtil.isNull(row) || ObjectUtil.isNotNull(row.getDeletedAt())) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		return row;
	}

	@Override
	public ArrayNode editForm(Long id) {
		CustomerLiaison row = info(id);
		List<FormCateListItemVO> form = formDataService.listByTypes(3);
		for (FormCateListItemVO g : form) {
			if (g.getData() == null) {
				continue;
			}
			for (FormDataItemVO item : g.getData()) {
				item.setValue(fieldVal(row, item.getKey()));
			}
		}
		return objectMapper.valueToTree(form);
	}

	private void applyJson(JsonNode body, CustomerLiaison target) {
		ObjectNode o = body.deepCopy();
		o.remove("eid");
		ObjectMapper om = objectMapper.copy();
		om.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			CustomerLiaison patch = om.treeToValue(o, CustomerLiaison.class);
			BeanUtil.copyProperties(patch, target, CopyOptions.create().ignoreNullValue().ignoreError());
		}
		catch (Exception e) {
			throw new IllegalArgumentException("参数解析失败");
		}
	}

	private static String fieldVal(CustomerLiaison c, String formKey) {
		if (StrUtil.isBlank(formKey)) {
			return "";
		}
		for (Field f : CustomerLiaison.class.getDeclaredFields()) {
			f.setAccessible(true);
			com.baomidou.mybatisplus.annotation.TableField tf = f
				.getAnnotation(com.baomidou.mybatisplus.annotation.TableField.class);
			if (tf != null && !tf.exist()) {
				continue;
			}
			String col = f.getName();
			if (tf != null && StrUtil.isNotBlank(tf.value())) {
				col = tf.value().replace("`", "");
			}
			if (formKey.equals(col) || formKey.equals(f.getName())) {
				try {
					Object v = f.get(c);
					return v == null ? "" : String.valueOf(v);
				}
				catch (IllegalAccessException e) {
					return "";
				}
			}
		}
		try {
			Field camel = CustomerLiaison.class.getDeclaredField(StrUtil.toCamelCase(formKey));
			camel.setAccessible(true);
			Object v = camel.get(c);
			return v == null ? "" : String.valueOf(v);
		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			return "";
		}
	}

}
