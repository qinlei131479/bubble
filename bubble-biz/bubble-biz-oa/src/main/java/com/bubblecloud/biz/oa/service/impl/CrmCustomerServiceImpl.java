package com.bubblecloud.biz.oa.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.ClientBillMapper;
import com.bubblecloud.biz.oa.mapper.ClientInvoiceMapper;
import com.bubblecloud.biz.oa.mapper.ClientLabelsMapper;
import com.bubblecloud.biz.oa.mapper.ClientSubscribeMapper;
import com.bubblecloud.biz.oa.mapper.ContractMapper;
import com.bubblecloud.biz.oa.mapper.CrmDashboardMapper;
import com.bubblecloud.biz.oa.mapper.CustomerLiaisonMapper;
import com.bubblecloud.biz.oa.mapper.CustomerMapper;
import com.bubblecloud.biz.oa.mapper.CustomerRecordMapper;
import com.bubblecloud.biz.oa.service.CrmCustomerService;
import com.bubblecloud.biz.oa.service.FormDataService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.ClientBill;
import com.bubblecloud.oa.api.entity.ClientInvoice;
import com.bubblecloud.oa.api.entity.ClientLabels;
import com.bubblecloud.oa.api.entity.ClientSubscribe;
import com.bubblecloud.oa.api.entity.Contract;
import com.bubblecloud.oa.api.entity.Customer;
import com.bubblecloud.oa.api.entity.CustomerLiaison;
import com.bubblecloud.oa.api.entity.CustomerRecord;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.crm.NamedCountVO;
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
 * CRM 客户实现。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Service
@RequiredArgsConstructor
public class CrmCustomerServiceImpl extends UpServiceImpl<CustomerMapper, Customer> implements CrmCustomerService {

	private static final int TYPE_CHARGE = 2;

	private static final int TYPE_HEIGHT_SEAS = 3;

	private final FormDataService formDataService;

	private final ObjectMapper objectMapper;

	private final CustomerRecordMapper customerRecordMapper;

	private final ClientLabelsMapper clientLabelsMapper;

	private final ClientSubscribeMapper clientSubscribeMapper;

	private final ClientBillMapper clientBillMapper;

	private final ContractMapper contractMapper;

	private final ClientInvoiceMapper clientInvoiceMapper;

	private final CustomerLiaisonMapper customerLiaisonMapper;

	private final AdminMapper adminMapper;

	private final CrmDashboardMapper crmDashboardMapper;

	@Override
	public ListCountVO<Customer> postCustomerList(JsonNode body, Long adminId) {
		int types = body.path("types").asInt(1);
		int page = body.path("page").asInt(1);
		int limit = body.path("limit").asInt(20);
		String nameLike = text(body, "customer_name");
		if (StrUtil.isBlank(nameLike)) {
			nameLike = text(body, "name");
		}
		Customer q = new Customer();
		q.setQueryTypes(types);
		q.setCustomerName(nameLike);
		if (types == TYPE_HEIGHT_SEAS) {
			q.setQueryUidList(Collections.emptyList());
		}
		else if (types == 2) {
			q.setQueryUidList(Collections.singletonList(adminId.intValue()));
		}
		else {
			q.setQueryUidList(parseIntList(body.get("uid")));
			if (ObjectUtil.isEmpty(q.getQueryUidList())) {
				q.setQueryUidList(null);
			}
		}
		List<Integer> labelIds = parseIntList(body.get("customer_label"));
		if (ObjectUtil.isNotEmpty(labelIds)) {
			q.setQueryLabelIds(labelIds);
		}
		Page<Customer> pg = new Page<>(page, limit);
		Page<Customer> res = baseMapper.findPg(pg, q);
		return ListCountVO.of(res.getRecords(), res.getTotal());
	}

	@Override
	public ObjectNode listStatistics(int types, Long adminId, List<Integer> uidScope) {
		if (ObjectUtil.isEmpty(uidScope) && types != TYPE_HEIGHT_SEAS) {
			uidScope = new ArrayList<>(Collections.singletonList(adminId.intValue()));
		}
		ObjectNode o = objectMapper.createObjectNode();
		o.put("total", countCustomers(types, uidScope));
		o.put("concern",
				clientSubscribeMapper.selectCount(Wrappers.lambdaQuery(ClientSubscribe.class)
					.eq(ClientSubscribe::getUid, adminId.intValue())
					.eq(ClientSubscribe::getSubscribeStatus, 1)));
		o.put("unsettled", countByStatusHint(types, uidScope, "0"));
		o.put("traded", countByStatusHint(types, uidScope, "1"));
		o.put("urgent_follow_up", countUrgentFollow(types, uidScope));
		o.put("lost", countByStatusHint(types, uidScope, "2"));
		return o;
	}

	private long countCustomers(int types, List<Integer> uidScope) {
		LambdaQueryWrapper<Customer> w = Wrappers.lambdaQuery(Customer.class).isNull(Customer::getDeletedAt);
		if (types == TYPE_HEIGHT_SEAS) {
			w.eq(Customer::getUid, 0);
		}
		else if (ObjectUtil.isNotEmpty(uidScope)) {
			w.in(Customer::getUid, uidScope);
		}
		return baseMapper.selectCount(w);
	}

	private long countByStatusHint(int types, List<Integer> uidScope, String digit) {
		LambdaQueryWrapper<Customer> w = Wrappers.lambdaQuery(Customer.class).isNull(Customer::getDeletedAt);
		if (types == TYPE_HEIGHT_SEAS) {
			w.eq(Customer::getUid, 0);
		}
		else if (ObjectUtil.isNotEmpty(uidScope)) {
			w.in(Customer::getUid, uidScope);
		}
		w.and(x -> x.eq(Customer::getCustomerStatus, digit)
			.or()
			.like(Customer::getCustomerStatus, "\"" + digit + "\""));
		return baseMapper.selectCount(w);
	}

	private long countUrgentFollow(int types, List<Integer> uidScope) {
		LocalDateTime threshold = LocalDateTime.now().minusDays(30);
		LambdaQueryWrapper<Customer> w = Wrappers.lambdaQuery(Customer.class).isNull(Customer::getDeletedAt);
		if (types == TYPE_HEIGHT_SEAS) {
			w.eq(Customer::getUid, 0);
		}
		else if (ObjectUtil.isNotEmpty(uidScope)) {
			w.in(Customer::getUid, uidScope);
		}
		w.and(x -> x.isNull(Customer::getLastFollowUpTime).or().lt(Customer::getLastFollowUpTime, threshold));
		return baseMapper.selectCount(w);
	}

	@Override
	public ArrayNode createForm() {
		return objectMapper.valueToTree(formDataService.listByTypes(1));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ObjectNode storeFromBody(JsonNode body, Long adminId) {
		int types = body.path("types").asInt(TYPE_CHARGE);
		Customer c = new Customer();
		applyJsonToCustomer(stripMeta(body), c);
		c.setCustomerNo(generateNo());
		c.setCreatorUid(adminId.intValue());
		if (types == TYPE_CHARGE) {
			c.setUid(adminId.intValue());
		}
		else {
			c.setUid(0);
		}
		if (StrUtil.isBlank(c.getCustomerStatus())) {
			c.setCustomerStatus("0");
		}
		baseMapper.insert(c);
		applyLabelsFromBody(body, c.getId());
		applySubscribeFromBody(body, c.getId(), adminId);
		insertRecord(c.getId(), 6, adminId.intValue(), ObjectUtil.defaultIfNull(c.getUid(), 0), c.getReturnNum(),
				"新添加客户「" + StrUtil.nullToEmpty(c.getCustomerName()) + "」");
		ObjectNode out = objectMapper.createObjectNode();
		out.put("id", c.getId());
		return out;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateFromBody(Long id, JsonNode body, Long adminId, int force) {
		Customer existing = baseMapper.selectById(id);
		if (ObjectUtil.isNull(existing) || ObjectUtil.isNotNull(existing.getDeletedAt())) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		Customer patch = new Customer();
		applyJsonToCustomer(stripMeta(body), patch);
		patch.setId(id);
		patch.setCustomerStatus(null);
		BeanUtil.copyProperties(patch, existing, CopyOptions.create().ignoreNullValue().ignoreError());
		if (StrUtil.isBlank(existing.getCustomerNo())) {
			existing.setCustomerNo(generateNo());
		}
		baseMapper.updateById(existing);
		applyLabelsFromBody(body, id);
		applySubscribeFromBody(body, id, adminId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void softDelete(Long id) {
		LambdaUpdateWrapper<Customer> uw = Wrappers.lambdaUpdate(Customer.class)
			.eq(Customer::getId, id)
			.set(Customer::getDeletedAt, LocalDateTime.now());
		baseMapper.update(null, uw);
	}

	@Override
	public ObjectNode customerInfo(Long id, Long adminId) {
		Customer c = baseMapper.selectById(id);
		if (ObjectUtil.isNull(c) || ObjectUtil.isNotNull(c.getDeletedAt())) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		ObjectNode node = objectMapper.valueToTree(c);
		int sub = subscribeStatus(adminId.intValue(), id.intValue());
		node.put("customer_followed", String.valueOf(sub));
		ArrayNode labelArr = objectMapper.createArrayNode();
		clientLabelsMapper.selectList(Wrappers.lambdaQuery(ClientLabels.class).eq(ClientLabels::getEid, id.intValue()))
			.forEach(cl -> labelArr.add(cl.getLabelId()));
		node.set("customer_label", labelArr);
		return node;
	}

	@Override
	public ArrayNode editForm(Long id, Long adminId) {
		Customer c = baseMapper.selectById(id);
		if (ObjectUtil.isNull(c) || ObjectUtil.isNotNull(c.getDeletedAt())) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		List<FormCateListItemVO> form = formDataService.listByTypes(1);
		for (FormCateListItemVO g : form) {
			if (g.getData() == null) {
				continue;
			}
			for (FormDataItemVO row : g.getData()) {
				row.setValue(fieldValueForFormKey(c, row.getKey()));
			}
		}
		int sub = subscribeStatus(adminId.intValue(), id.intValue());
		for (FormCateListItemVO g : form) {
			if (g.getData() == null) {
				continue;
			}
			for (FormDataItemVO row : g.getData()) {
				if ("customer_followed".equals(row.getKey())) {
					row.setValue(String.valueOf(sub));
				}
			}
		}
		return objectMapper.valueToTree(form);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void lost(List<Long> ids, Long adminId) {
		if (ObjectUtil.isEmpty(ids)) {
			return;
		}
		for (Long id : ids) {
			Customer c = baseMapper.selectById(id);
			if (ObjectUtil.isNull(c) || ObjectUtil.isNotNull(c.getDeletedAt())) {
				continue;
			}
			c.setCustomerStatus("2");
			baseMapper.updateById(c);
			insertRecord(id, 3, adminId.intValue(), ObjectUtil.defaultIfNull(c.getUid(), 0), c.getReturnNum(),
					"客户标记为流失");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void returnToHighSeas(List<Long> ids, String reason, Long adminId) {
		if (ObjectUtil.isEmpty(ids)) {
			return;
		}
		if (StrUtil.isBlank(reason)) {
			throw new IllegalArgumentException("请填写说明原因");
		}
		for (Long id : ids) {
			Customer c = baseMapper.selectById(id);
			if (ObjectUtil.isNull(c) || ObjectUtil.isNotNull(c.getDeletedAt()) || c.getUid() == null
					|| c.getUid() == 0) {
				continue;
			}
			int before = c.getUid();
			c.setBeforeUid(before);
			c.setUid(0);
			c.setReturnNum(ObjectUtil.defaultIfNull(c.getReturnNum(), 0) + 1);
			baseMapper.updateById(c);
			insertRecord(id, 1, adminId.intValue(), before, c.getReturnNum(), reason);
			clientSubscribeMapper
				.delete(Wrappers.lambdaQuery(ClientSubscribe.class).eq(ClientSubscribe::getEid, id.intValue()));
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void subscribe(Long customerId, int status, Long adminId) {
		ClientSubscribe row = clientSubscribeMapper.selectOne(Wrappers.lambdaQuery(ClientSubscribe.class)
			.eq(ClientSubscribe::getUid, adminId.intValue())
			.eq(ClientSubscribe::getEid, customerId.intValue()));
		if (row == null) {
			row = new ClientSubscribe();
			row.setEntid(0);
			row.setUid(adminId.intValue());
			row.setEid(customerId.intValue());
			row.setSubscribeStatus(status);
			row.setCreatedAt(LocalDateTime.now());
			row.setUpdatedAt(LocalDateTime.now());
			clientSubscribeMapper.insert(row);
		}
		else {
			row.setSubscribeStatus(status);
			row.setUpdatedAt(LocalDateTime.now());
			clientSubscribeMapper.updateById(row);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void cancelLost(Long id, Long adminId) {
		Customer c = baseMapper.selectById(id);
		if (ObjectUtil.isNull(c) || ObjectUtil.isNotNull(c.getDeletedAt())) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (c.getUid() != null && c.getUid() != 0) {
			throw new IllegalArgumentException("客户存在业务员, 不能进行取消流失操作");
		}
		if (!statusLooksLikeLost(c.getCustomerStatus())) {
			throw new IllegalArgumentException("客户状态异常, 不能进行取消流失操作");
		}
		long billCount = clientBillMapper.selectCount(Wrappers.lambdaQuery(ClientBill.class)
			.eq(ClientBill::getEid, id.intValue())
			.in(ClientBill::getTypes, 0, 1)
			.eq(ClientBill::getStatus, 1));
		String newStatus = billCount > 0 ? "1" : "0";
		c.setCustomerStatus(newStatus);
		baseMapper.updateById(c);
		insertRecord(id, 4, adminId.intValue(), 0, c.getReturnNum(), "取消流失，状态调整为 " + newStatus);
	}

	@Override
	public ArrayNode selectCustomers(Long adminId) {
		List<Customer> list = baseMapper.selectList(Wrappers.lambdaQuery(Customer.class)
			.isNull(Customer::getDeletedAt)
			.eq(Customer::getUid, adminId.intValue())
			.orderByDesc(Customer::getId)
			.last("LIMIT 500"));
		ArrayNode arr = objectMapper.createArrayNode();
		for (Customer c : list) {
			ObjectNode row = objectMapper.createObjectNode();
			row.put("value", c.getId());
			row.put("label", StrUtil.nullToEmpty(c.getCustomerName()));
			arr.add(row);
		}
		return arr;
	}

	@Override
	public ArrayNode salesmanOptions() {
		List<Admin> admins = adminMapper.selectList(Wrappers.lambdaQuery(Admin.class)
			.isNull(Admin::getDeletedAt)
			.orderByAsc(Admin::getId)
			.last("LIMIT 500"));
		ArrayNode arr = objectMapper.createArrayNode();
		for (Admin a : admins) {
			ObjectNode row = objectMapper.createObjectNode();
			row.put("value", a.getId());
			row.put("label", StrUtil.nullToEmpty(a.getName()));
			arr.add(row);
		}
		return arr;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void claim(List<Long> ids, Long adminId) {
		if (ObjectUtil.isEmpty(ids)) {
			return;
		}
		Admin me = adminMapper.selectById(adminId);
		String salesmanName = me == null ? "" : StrUtil.nullToEmpty(me.getName());
		for (Long id : ids) {
			Customer c = baseMapper.selectById(id);
			if (ObjectUtil.isNull(c) || ObjectUtil.isNotNull(c.getDeletedAt())) {
				continue;
			}
			if (c.getUid() != null && c.getUid() != 0) {
				continue;
			}
			if (statusLooksLikeLost(c.getCustomerStatus())) {
				continue;
			}
			long billCount = clientBillMapper.selectCount(Wrappers.lambdaQuery(ClientBill.class)
				.eq(ClientBill::getEid, id.intValue())
				.in(ClientBill::getTypes, 0, 1)
				.eq(ClientBill::getStatus, 1));
			c.setUid(adminId.intValue());
			c.setCustomerStatus(billCount > 0 ? "1" : "0");
			c.setCollectTime(LocalDateTime.now());
			baseMapper.updateById(c);
			insertRecord(id, 2, adminId.intValue(), adminId.intValue(), c.getReturnNum(),
					"「" + salesmanName + "」从公海领取");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchLabels(List<Long> customerIds, List<Integer> labelIds) {
		if (ObjectUtil.isEmpty(customerIds)) {
			return;
		}
		for (Long eid : customerIds) {
			clientLabelsMapper
				.delete(Wrappers.lambdaQuery(ClientLabels.class).eq(ClientLabels::getEid, eid.intValue()));
			if (ObjectUtil.isNotEmpty(labelIds)) {
				for (Integer lid : labelIds) {
					ClientLabels cl = new ClientLabels();
					cl.setEid(eid.intValue());
					cl.setLabelId(lid);
					cl.setCreatedAt(LocalDateTime.now());
					cl.setUpdatedAt(LocalDateTime.now());
					clientLabelsMapper.insert(cl);
				}
			}
			String labelJson;
			try {
				labelJson = ObjectUtil.isEmpty(labelIds) ? "[]" : objectMapper.writeValueAsString(labelIds);
			}
			catch (Exception e) {
				labelJson = "[]";
			}
			Customer patch = new Customer();
			patch.setId(eid);
			patch.setCustomerLabel(labelJson);
			baseMapper.updateById(patch);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void shift(List<Long> customerIds, int toUid, int invoice, int contract, Long adminId) {
		if (toUid <= 0) {
			throw new IllegalArgumentException("转移人ID不能为空");
		}
		if (ObjectUtil.isEmpty(customerIds)) {
			return;
		}
		Admin target = adminMapper.selectById((long) toUid);
		if (target == null) {
			throw new IllegalArgumentException("交接人员不存在");
		}
		String targetName = StrUtil.nullToEmpty(target.getName());
		for (Long id : customerIds) {
			Customer c = baseMapper.selectById(id);
			if (ObjectUtil.isNull(c) || ObjectUtil.isNotNull(c.getDeletedAt())) {
				continue;
			}
			if (statusLooksLikeLost(c.getCustomerStatus())) {
				continue;
			}
			String reason;
			if (c.getUid() == null || c.getUid() < 1) {
				reason = "此客户从公海移交给「" + targetName + "」负责";
			}
			else {
				Admin before = adminMapper.selectById(c.getUid().longValue());
				String bn = before == null ? "" : StrUtil.nullToEmpty(before.getName());
				reason = "此客户从「" + bn + "」负责移交给「" + targetName + "」负责";
			}
			c.setBeforeUid(c.getUid());
			c.setUid(toUid);
			long billCount = clientBillMapper.selectCount(Wrappers.lambdaQuery(ClientBill.class)
				.eq(ClientBill::getEid, id.intValue())
				.in(ClientBill::getTypes, 0, 1)
				.eq(ClientBill::getStatus, 1));
			c.setCustomerStatus(billCount > 0 ? "1" : "0");
			baseMapper.updateById(c);
			insertRecord(id, 5, adminId.intValue(), toUid, c.getReturnNum(), reason);
		}
		List<Integer> eids = customerIds.stream().map(Long::intValue).collect(Collectors.toList());
		clientSubscribeMapper.delete(Wrappers.lambdaQuery(ClientSubscribe.class).in(ClientSubscribe::getEid, eids));
		if (contract == 1) {
			contractMapper.update(null,
					Wrappers.lambdaUpdate(Contract.class).in(Contract::getEid, eids).set(Contract::getUid, toUid));
		}
		if (invoice == 1) {
			clientInvoiceMapper.update(null,
					Wrappers.lambdaUpdate(ClientInvoice.class)
						.in(ClientInvoice::getEid, eids)
						.set(ClientInvoice::getUid, String.valueOf(toUid)));
		}
		customerLiaisonMapper.update(null,
				Wrappers.lambdaUpdate(CustomerLiaison.class)
					.in(CustomerLiaison::getEid, eids)
					.set(CustomerLiaison::getUid, toUid));
	}

	@Override
	public ObjectNode performanceStatistics(JsonNode body) {
		ObjectNode root = objectMapper.createObjectNode();
		ObjectNode newCustomer = objectMapper.createObjectNode();
		newCustomer.put("count",
				baseMapper.selectCount(Wrappers.lambdaQuery(Customer.class).isNull(Customer::getDeletedAt)));
		newCustomer.put("ratio", 0);
		root.set("new_customer", newCustomer);
		root.set("bill", objectMapper.valueToTree(crmDashboardMapper.billIncomeByMonth()));
		root.set("contract", objectMapper.valueToTree(crmDashboardMapper.contractCategoryRank()));
		return root;
	}

	@Override
	public ArrayNode contractCategoryRank(JsonNode body) {
		List<NamedCountVO> rows = crmDashboardMapper.contractCategoryRank();
		return objectMapper.valueToTree(rows);
	}

	@Override
	public ArrayNode salesmanRanking(JsonNode body) {
		List<NamedCountVO> rows = crmDashboardMapper.customerCountBySalesman();
		return objectMapper.valueToTree(rows);
	}

	@Override
	public ArrayNode trendStatistics() {
		List<NamedCountVO> rows = crmDashboardMapper.billIncomeByMonth();
		return objectMapper.valueToTree(rows);
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
			if (!row.isObject()) {
				continue;
			}
			try {
				Customer c = snake.treeToValue(row, Customer.class);
				c.setId(null);
				c.setCustomerNo(generateNo());
				c.setCreatorUid(adminId.intValue());
				c.setUid(adminId.intValue());
				if (StrUtil.isBlank(c.getCustomerStatus())) {
					c.setCustomerStatus("0");
				}
				baseMapper.insert(c);
			}
			catch (Exception ignored) {
				// 跳过无法解析的行，避免整批失败
			}
		}
	}

	private void insertRecord(Long eid, int type, int creatorUid, int uid, Integer returnNum, String reason) {
		CustomerRecord r = new CustomerRecord();
		r.setEid(eid.intValue());
		r.setType(type);
		r.setUid(uid);
		r.setCreatorUid(creatorUid);
		r.setRecordVersion(ObjectUtil.defaultIfNull(returnNum, 0));
		r.setReason(reason);
		r.setCreatedAt(LocalDateTime.now());
		r.setUpdatedAt(LocalDateTime.now());
		customerRecordMapper.insert(r);
	}

	private int subscribeStatus(int adminUid, int eid) {
		ClientSubscribe s = clientSubscribeMapper.selectOne(Wrappers.lambdaQuery(ClientSubscribe.class)
			.eq(ClientSubscribe::getUid, adminUid)
			.eq(ClientSubscribe::getEid, eid));
		return s == null ? 0 : ObjectUtil.defaultIfNull(s.getSubscribeStatus(), 0);
	}

	private boolean statusLooksLikeLost(String customerStatus) {
		if (StrUtil.isBlank(customerStatus)) {
			return false;
		}
		return "2".equals(customerStatus) || customerStatus.contains("\"2\"");
	}

	private String generateNo() {
		for (int i = 0; i < 20; i++) {
			String no = RandomUtil.randomString(6);
			Long c = baseMapper.selectCount(Wrappers.lambdaQuery(Customer.class).eq(Customer::getCustomerNo, no));
			if (c == 0) {
				return no;
			}
		}
		return RandomUtil.randomString(8);
	}

	private JsonNode stripMeta(JsonNode body) {
		if (!(body instanceof ObjectNode obj)) {
			return body;
		}
		ObjectNode c = obj.deepCopy();
		c.remove("types");
		c.remove("force");
		c.remove("data");
		c.remove("customer_label");
		c.remove("label");
		return c;
	}

	private void applyLabelsFromBody(JsonNode body, Long customerId) {
		JsonNode labels = body.get("customer_label");
		if (labels == null || !labels.isArray()) {
			return;
		}
		List<Integer> ids = new ArrayList<>();
		labels.forEach(n -> ids.add(n.asInt()));
		batchLabels(Collections.singletonList(customerId), ids);
	}

	private void applySubscribeFromBody(JsonNode body, Long customerId, Long adminId) {
		if (!body.has("customer_followed")) {
			return;
		}
		int st = body.get("customer_followed").asInt(0) < 1 ? 0 : 1;
		subscribe(customerId, st, adminId);
	}

	private void applyJsonToCustomer(JsonNode body, Customer target) {
		ObjectMapper om = objectMapper.copy();
		om.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			Customer patch = om.treeToValue(body, Customer.class);
			BeanUtil.copyProperties(patch, target, CopyOptions.create().ignoreNullValue().ignoreError());
		}
		catch (Exception e) {
			throw new IllegalArgumentException("参数解析失败");
		}
	}

	private static String text(JsonNode n, String field) {
		if (n == null || n.get(field) == null || n.get(field).isNull()) {
			return "";
		}
		return n.get(field).asText("");
	}

	private static List<Integer> parseIntList(JsonNode arr) {
		if (arr == null || !arr.isArray() || arr.isEmpty()) {
			return Collections.emptyList();
		}
		List<Integer> out = new ArrayList<>();
		arr.forEach(x -> {
			if (x.isInt() || x.isLong()) {
				out.add(x.intValue());
			}
		});
		return out;
	}

	private static String fieldValueForFormKey(Customer c, String formKey) {
		if (StrUtil.isBlank(formKey)) {
			return "";
		}
		for (Field f : Customer.class.getDeclaredFields()) {
			f.setAccessible(true);
			com.baomidou.mybatisplus.annotation.TableField tf = f
				.getAnnotation(com.baomidou.mybatisplus.annotation.TableField.class);
			if (tf != null && !tf.exist()) {
				continue;
			}
			String columnOrProp = f.getName();
			if (tf != null && StrUtil.isNotBlank(tf.value())) {
				columnOrProp = tf.value().replace("`", "");
			}
			if (formKey.equals(columnOrProp) || formKey.equals(f.getName())) {
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
			Field camel = Customer.class.getDeclaredField(StrUtil.toCamelCase(formKey));
			camel.setAccessible(true);
			Object v = camel.get(c);
			return v == null ? "" : String.valueOf(v);
		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			return "";
		}
	}

}
