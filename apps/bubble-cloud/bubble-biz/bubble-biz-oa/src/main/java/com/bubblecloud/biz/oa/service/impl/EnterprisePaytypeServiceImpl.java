package com.bubblecloud.biz.oa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.EnterprisePaytypeMapper;
import com.bubblecloud.biz.oa.service.EnterprisePaytypeService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.EnterprisePaytype;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;

/**
 * 企业支付方式实现。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Service
@RequiredArgsConstructor
public class EnterprisePaytypeServiceImpl extends UpServiceImpl<EnterprisePaytypeMapper, EnterprisePaytype>
		implements EnterprisePaytypeService {

	private final ObjectMapper objectMapper;

	@Override
	public OaElFormVO buildCreateForm() {
		ArrayNode rules = objectMapper.createArrayNode();
		ObjectNode name = objectMapper.createObjectNode();
		name.put("type", "input");
		name.put("field", "name");
		name.put("title", "支付方式");
		name.put("value", "");
		ObjectNode nameP = objectMapper.createObjectNode();
		nameP.put("required", true);
		name.set("props", nameP);
		rules.add(name);
		ObjectNode info = objectMapper.createObjectNode();
		info.put("type", "textarea");
		info.put("field", "info");
		info.put("title", "简介");
		info.put("value", "");
		ObjectNode infoP = objectMapper.createObjectNode();
		infoP.put("placeholder", "简介内容");
		infoP.put("rows", 3);
		info.set("props", infoP);
		rules.add(info);
		ObjectNode status = objectMapper.createObjectNode();
		status.put("type", "radio");
		status.put("field", "status");
		status.put("title", "是否开启");
		status.put("value", 1);
		ArrayNode sOpt = objectMapper.createArrayNode();
		sOpt.addObject().put("value", 1).put("label", "开启");
		sOpt.addObject().put("value", 0).put("label", "关闭");
		status.set("options", sOpt);
		rules.add(status);
		ObjectNode sort = objectMapper.createObjectNode();
		sort.put("type", "inputNumber");
		sort.put("field", "sort");
		sort.put("title", "排序");
		sort.put("value", 0);
		ObjectNode sortP = objectMapper.createObjectNode();
		sortP.put("min", 0);
		sortP.put("max", 999999);
		sortP.put("precision", 0);
		sort.set("props", sortP);
		rules.add(sort);
		return new OaElFormVO("支付方式", "POST", "/ent/pay_type", rules);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(EnterprisePaytype dto) {
		dto.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		dto.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		dto.setTypeId(ObjectUtil.defaultIfNull(dto.getTypeId(), 0));
		return super.create(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(EnterprisePaytype dto) {
		EnterprisePaytype existing = baseMapper.selectById(dto.getId());
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		return super.update(dto);
	}

}
