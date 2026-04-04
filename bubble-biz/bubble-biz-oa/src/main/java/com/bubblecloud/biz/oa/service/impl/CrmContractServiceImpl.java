package com.bubblecloud.biz.oa.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.ClientContractSubscribeMapper;
import com.bubblecloud.biz.oa.mapper.ClientInvoiceMapper;
import com.bubblecloud.biz.oa.mapper.ContractMapper;
import com.bubblecloud.biz.oa.service.CrmContractService;
import com.bubblecloud.biz.oa.service.FormDataService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.ClientContractSubscribe;
import com.bubblecloud.oa.api.entity.ClientInvoice;
import com.bubblecloud.oa.api.entity.Contract;
import com.bubblecloud.oa.api.vo.ListCountVO;
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
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

/**
 * CRM 合同实现。
 *
 * @author qinlei
 * @date 2026/4/3 12:00
 */
@Service
@RequiredArgsConstructor
public class CrmContractServiceImpl extends UpServiceImpl<ContractMapper, Contract> implements CrmContractService {

	private final FormDataService formDataService;

	private final ObjectMapper objectMapper;

	private final ClientContractSubscribeMapper clientContractSubscribeMapper;

	private final ClientInvoiceMapper clientInvoiceMapper;

	@Override
	public ListCountVO<Contract> postList(JsonNode body, Long adminId) {
		int types = body.path("types").asInt(6);
		int page = body.path("page").asInt(1);
		int limit = body.path("limit").asInt(20);
		Contract q = new Contract();
		q.setQueryTypes(types);
		q.setContractName(body.path("contract_name").asText(""));
		if (types == 6) {
			q.setQueryUidList(Collections.singletonList(adminId.intValue()));
		}
		else {
			q.setQueryUidList(null);
		}
		Page<Contract> pg = new Page<>(page, limit);
		Page<Contract> res = baseMapper.findPg(pg, q);
		return ListCountVO.of(res.getRecords(), res.getTotal());
	}

	@Override
	public ArrayNode createForm() {
		return objectMapper.valueToTree(formDataService.listByTypes(2));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void store(JsonNode body, Long adminId) {
		Contract c = new Contract();
		applyJson(body, c);
		if (StrUtil.isBlank(c.getContractNo())) {
			c.setContractNo(RandomUtil.randomNumbers(6));
		}
		c.setCreatorUid(adminId.intValue());
		if (c.getUid() == null || c.getUid() == 0) {
			c.setUid(adminId.intValue());
		}
		baseMapper.insert(c);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(Long id, JsonNode body, Long adminId) {
		Contract existing = baseMapper.selectById(id);
		if (ObjectUtil.isNull(existing) || ObjectUtil.isNotNull(existing.getDeletedAt())) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		Contract patch = new Contract();
		applyJson(body, patch);
		patch.setId(id);
		BeanUtil.copyProperties(patch, existing, CopyOptions.create().ignoreNullValue().ignoreError());
		baseMapper.updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void softDelete(Long id) {
		LambdaUpdateWrapper<Contract> uw = Wrappers.lambdaUpdate(Contract.class)
			.eq(Contract::getId, id)
			.set(Contract::getDeletedAt, LocalDateTime.now());
		baseMapper.update(null, uw);
	}

	@Override
	public ObjectNode info(Long id) {
		Contract c = baseMapper.selectById(id);
		if (ObjectUtil.isNull(c) || ObjectUtil.isNotNull(c.getDeletedAt())) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		return objectMapper.valueToTree(c);
	}

	@Override
	public ArrayNode editForm(Long id) {
		Contract c = baseMapper.selectById(id);
		if (ObjectUtil.isNull(c) || ObjectUtil.isNotNull(c.getDeletedAt())) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		List<FormCateListItemVO> form = formDataService.listByTypes(2);
		for (FormCateListItemVO g : form) {
			if (g.getData() == null) {
				continue;
			}
			for (FormDataItemVO row : g.getData()) {
				row.setValue(fieldVal(c, row.getKey()));
			}
		}
		return objectMapper.valueToTree(form);
	}

	@Override
	public ObjectNode listStatistics(int types, Long adminId) {
		ObjectNode o = objectMapper.createObjectNode();
		o.put("total", baseMapper.selectCount(Wrappers.lambdaQuery(Contract.class).isNull(Contract::getDeletedAt)));
		o.put("self",
				baseMapper.selectCount(Wrappers.lambdaQuery(Contract.class)
					.isNull(Contract::getDeletedAt)
					.eq(Contract::getUid, adminId.intValue())));
		return o;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void subscribeContract(Long contractId, int status, Long adminId) {
		Contract c = baseMapper.selectById(contractId);
		if (ObjectUtil.isNull(c)) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		ClientContractSubscribe row = clientContractSubscribeMapper
			.selectOne(Wrappers.lambdaQuery(ClientContractSubscribe.class)
				.eq(ClientContractSubscribe::getUid, adminId.intValue())
				.eq(ClientContractSubscribe::getCid, contractId.intValue()));
		if (row == null) {
			row = new ClientContractSubscribe();
			row.setEntid(0);
			row.setUid(adminId.intValue());
			row.setEid(c.getEid());
			row.setCid(contractId.intValue());
			row.setSubscribeStatus(status);
			row.setCreatedAt(LocalDateTime.now());
			row.setUpdatedAt(LocalDateTime.now());
			clientContractSubscribeMapper.insert(row);
		}
		else {
			row.setSubscribeStatus(status);
			row.setUpdatedAt(LocalDateTime.now());
			clientContractSubscribeMapper.updateById(row);
		}
	}

	@Override
	public ArrayNode selectByCustomerIds(List<Integer> eids, Long adminId) {
		if (eids == null) {
			eids = Collections.emptyList();
		}
		ArrayNode out = objectMapper.createArrayNode();
		if (eids.isEmpty()) {
			return out;
		}
		List<Contract> list = baseMapper.selectList(Wrappers.lambdaQuery(Contract.class)
			.isNull(Contract::getDeletedAt)
			.in(Contract::getEid, eids)
			.orderByDesc(Contract::getId)
			.last("LIMIT 500"));
		for (Contract c : list) {
			ObjectNode row = objectMapper.createObjectNode();
			row.put("value", c.getId());
			row.put("label", StrUtil.nullToEmpty(c.getContractName()));
			out.add(row);
		}
		return out;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void abnormal(Long id, int status, Long adminId) {
		Contract c = baseMapper.selectById(id);
		if (ObjectUtil.isNull(c) || ObjectUtil.isNotNull(c.getDeletedAt())) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		c.setIsAbnormal(status);
		baseMapper.updateById(c);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void shift(JsonNode body) {
		List<Long> ids = new ArrayList<>();
		JsonNode data = body.get("data");
		if (data != null && data.isArray()) {
			data.forEach(n -> ids.add(n.asLong()));
		}
		int toUid = body.path("to_uid").asInt(0);
		int invoice = body.path("invoice").asInt(0);
		if (toUid <= 0 || ids.isEmpty()) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		Set<Integer> customerEids = new HashSet<>();
		for (Long id : ids) {
			Contract c = baseMapper.selectById(id);
			if (ObjectUtil.isNull(c) || ObjectUtil.isNotNull(c.getDeletedAt())) {
				continue;
			}
			if (c.getEid() != null) {
				customerEids.add(c.getEid());
			}
			c.setUid(toUid);
			baseMapper.updateById(c);
		}
		if (invoice == 1 && !customerEids.isEmpty()) {
			clientInvoiceMapper.update(null,
					Wrappers.lambdaUpdate(ClientInvoice.class)
						.in(ClientInvoice::getEid, customerEids)
						.set(ClientInvoice::getUid, String.valueOf(toUid)));
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchImport(JsonNode body, Long adminId) {
		JsonNode data = body.get("data");
		if (data == null || !data.isArray()) {
			return;
		}
		ObjectMapper snake = objectMapper.copy();
		snake.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		snake.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		for (JsonNode row : data) {
			try {
				Contract c = snake.treeToValue(row, Contract.class);
				c.setId(null);
				c.setCreatorUid(adminId.intValue());
				if (c.getUid() == null || c.getUid() == 0) {
					c.setUid(adminId.intValue());
				}
				baseMapper.insert(c);
			}
			catch (Exception ignored) {
				// skip bad row
			}
		}
	}

	private void applyJson(JsonNode body, Contract target) {
		ObjectNode o = body.deepCopy();
		o.remove("types");
		ObjectMapper om = objectMapper.copy();
		om.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			Contract patch = om.treeToValue(o, Contract.class);
			BeanUtil.copyProperties(patch, target, CopyOptions.create().ignoreNullValue().ignoreError());
		}
		catch (Exception e) {
			throw new IllegalArgumentException("参数解析失败");
		}
	}

	private static String fieldVal(Contract c, String formKey) {
		if (StrUtil.isBlank(formKey)) {
			return "";
		}
		for (Field f : Contract.class.getDeclaredFields()) {
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
			Field camel = Contract.class.getDeclaredField(StrUtil.toCamelCase(formKey));
			camel.setAccessible(true);
			Object v = camel.get(c);
			return v == null ? "" : String.valueOf(v);
		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			return "";
		}
	}

}
