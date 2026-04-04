package com.bubblecloud.biz.oa.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.ClientBillMapper;
import com.bubblecloud.biz.oa.mapper.ClientInvoiceMapper;
import com.bubblecloud.biz.oa.mapper.ContractMapper;
import com.bubblecloud.biz.oa.mapper.CustomerMapper;
import com.bubblecloud.biz.oa.mapper.SystemGroupDataMapper;
import com.bubblecloud.biz.oa.service.ClientBillCrmService;
import com.bubblecloud.oa.api.entity.ClientBill;
import com.bubblecloud.oa.api.entity.ClientInvoice;
import com.bubblecloud.oa.api.entity.Contract;
import com.bubblecloud.oa.api.entity.Customer;
import com.bubblecloud.oa.api.entity.SystemGroupData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.StrUtil;

/**
 * 客户资金记录（表级逻辑 + 合同回款字段同步，对齐 PHP 主路径）。
 *
 * @author qinlei
 * @date 2026/4/3 16:00
 */
@Service
public class ClientBillCrmServiceImpl implements ClientBillCrmService {

	private static final int RENEW_URGENT_DAYS = 30;

	private static final DateTimeFormatter END_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final ClientBillMapper clientBillMapper;
	private final ContractMapper contractMapper;
	private final ClientInvoiceMapper clientInvoiceMapper;
	private final CustomerMapper customerMapper;
	private final SystemGroupDataMapper systemGroupDataMapper;
	private final ObjectMapper objectMapper;

	public ClientBillCrmServiceImpl(ClientBillMapper clientBillMapper, ContractMapper contractMapper,
			ClientInvoiceMapper clientInvoiceMapper, CustomerMapper customerMapper,
			SystemGroupDataMapper systemGroupDataMapper, ObjectMapper objectMapper) {
		this.clientBillMapper = clientBillMapper;
		this.contractMapper = contractMapper;
		this.clientInvoiceMapper = clientInvoiceMapper;
		this.customerMapper = customerMapper;
		this.systemGroupDataMapper = systemGroupDataMapper;
		this.objectMapper = objectMapper;
	}

	private static Integer parseInt(Map<String, String> q, String k) {
		if (q == null || !q.containsKey(k)) {
			return null;
		}
		String s = q.get(k);
		if (s == null || s.isBlank()) {
			return null;
		}
		try {
			return Integer.parseInt(s.trim());
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	private static String fmtMoney(BigDecimal v) {
		if (v == null) {
			return "0.00";
		}
		return v.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}

	private BigDecimal sumMaps(QueryWrapper<ClientBill> qw) {
		qw.select("COALESCE(SUM(num),0) AS t");
		List<Map<String, Object>> maps = clientBillMapper.selectMaps(qw);
		if (maps == null || maps.isEmpty() || maps.get(0) == null || maps.get(0).get("t") == null) {
			return BigDecimal.ZERO;
		}
		Object t = maps.get(0).get("t");
		if (t instanceof BigDecimal b) {
			return b;
		}
		if (t instanceof Number n) {
			return BigDecimal.valueOf(n.doubleValue());
		}
		return new BigDecimal(t.toString());
	}

	private void applyIndexFilters(QueryWrapper<ClientBill> qw, int entid, Map<String, String> q, boolean withStatus) {
		qw.eq("entid", entid);
		Integer eid = parseInt(q, "eid");
		if (eid != null) {
			qw.eq("eid", eid);
		}
		Integer cid = parseInt(q, "cid");
		if (cid != null) {
			qw.eq("cid", cid);
		}
		Integer cateId = parseInt(q, "cate_id");
		if (cateId != null) {
			qw.eq("cate_id", cateId);
		}
		if (withStatus) {
			Integer status = parseInt(q, "status");
			if (status != null) {
				qw.eq("status", status);
			}
		}
		Integer types = parseInt(q, "types");
		if (types != null) {
			qw.eq("types", types);
		}
		Integer invoiceId = parseInt(q, "invoice_id");
		if (invoiceId != null) {
			qw.eq("invoice_id", invoiceId);
		}
		Integer typeId = parseInt(q, "type_id");
		if (typeId != null && typeId > 0) {
			qw.eq("type_id", typeId);
		}
		String name = q == null ? null : q.get("name");
		if (StrUtil.isNotBlank(name)) {
			List<Customer> cs = customerMapper.selectList(Wrappers.lambdaQuery(Customer.class)
				.like(Customer::getCustomerName, name.trim()));
			List<Long> eids = cs.stream().map(Customer::getId).filter(Objects::nonNull).toList();
			if (eids.isEmpty()) {
				qw.eq("id", -1);
			}
			else {
				qw.in("eid", eids);
			}
		}
		String uidRaw = q == null ? null : q.get("salesman_id");
		if (StrUtil.isBlank(uidRaw)) {
			uidRaw = q == null ? null : q.get("uid");
		}
		if (StrUtil.isNotBlank(uidRaw)) {
			String[] parts = uidRaw.split(",");
			if (parts.length == 1) {
				qw.eq("uid", parts[0].trim());
			}
			else {
				qw.in("uid", java.util.Arrays.stream(parts).map(String::trim).filter(s -> !s.isEmpty()).toList());
			}
		}
		String timeData = q == null ? null : q.get("time");
		String timeField = q == null ? "date" : StrUtil.blankToDefault(q.get("time_field"), "date");
		if (StrUtil.isNotBlank(timeData) && timeData.contains("-")) {
			String[] ab = timeData.split("-", 2);
			if (ab.length == 2 && StrUtil.isNotBlank(ab[0]) && StrUtil.isNotBlank(ab[1])) {
				String col = "date".equals(timeField) ? "date" : "date";
				qw.between(col, ab[0].trim() + " 00:00:00", ab[1].trim() + " 23:59:59");
			}
		}
		String dateRange = q == null ? null : q.get("date");
		if (StrUtil.isNotBlank(dateRange) && dateRange.contains("-")) {
			String[] ab = dateRange.split("-", 2);
			if (ab.length == 2) {
				qw.between("date", ab[0].trim() + " 00:00:00", ab[1].trim() + " 23:59:59");
			}
		}
	}

	private Map<String, String> censusFor(int entid, Map<String, String> q) {
		Map<String, String> c = new HashMap<>();
		QueryWrapper<ClientBill> w1 = Wrappers.query();
		applyIndexFilters(w1, entid, q, false);
		w1.eq("bill_types", 1).eq("status", 1);
		c.put("income", fmtMoney(sumMaps(w1)));
		QueryWrapper<ClientBill> w2 = Wrappers.query();
		applyIndexFilters(w2, entid, q, false);
		w2.eq("bill_types", 0).eq("status", 1);
		c.put("expend", fmtMoney(sumMaps(w2)));
		QueryWrapper<ClientBill> w3 = Wrappers.query();
		applyIndexFilters(w3, entid, q, false);
		w3.eq("bill_types", 1).eq("status", 0);
		c.put("review_income", fmtMoney(sumMaps(w3)));
		QueryWrapper<ClientBill> w4 = Wrappers.query();
		applyIndexFilters(w4, entid, q, false);
		w4.eq("bill_types", 0).eq("status", 0);
		c.put("review_expend", fmtMoney(sumMaps(w4)));
		return c;
	}

	@Override
	public Map<String, Object> index(int entid, Map<String, String> query, int page, int limit) {
		QueryWrapper<ClientBill> qw = Wrappers.query();
		applyIndexFilters(qw, entid, query, true);
		String sort = query == null ? null : query.get("sort");
		if (StrUtil.isNotBlank(sort) && sort.contains(" ")) {
			String[] sp = sort.trim().split("\\s+");
			if (sp.length >= 2) {
				if ("desc".equalsIgnoreCase(sp[1])) {
					qw.orderByDesc(sp[0]);
				}
				else {
					qw.orderByAsc(sp[0]);
				}
			}
		}
		else {
			qw.orderByDesc("date").orderByDesc("id");
		}
		Page<ClientBill> pg = new Page<>(Math.max(page, 1), Math.max(limit, 1));
		Page<ClientBill> res = clientBillMapper.selectPage(pg, qw);
		Map<String, Object> out = new HashMap<>();
		out.put("list", res.getRecords());
		out.put("count", res.getTotal());
		out.put("census", censusFor(entid, query));
		Integer types = parseInt(query, "types");
		Integer cidQ = parseInt(query, "cid");
		if (types != null && types == 1 && cidQ != null && cidQ > 0) {
			out.put("renew_census", buildRenewCensus(cidQ, entid));
		}
		else {
			out.put("renew_census", Collections.emptyList());
		}
		return out;
	}

	@Override
	public Map<String, Object> billList(int entid, Map<String, String> query, int page, int limit) {
		Map<String, String> q = query == null ? new HashMap<>() : new HashMap<>(query);
		Integer eid = parseInt(q, "eid");
		if (eid != null && eid > 0) {
			List<Contract> contracts = contractMapper.selectList(Wrappers.lambdaQuery(Contract.class)
				.eq(Contract::getEid, eid));
			List<Integer> cids = contracts.stream().map(Contract::getId).filter(Objects::nonNull).map(Long::intValue).toList();
			q.remove("eid");
			if (cids.isEmpty()) {
				Map<String, Object> out = new HashMap<>();
				out.put("list", Collections.emptyList());
				out.put("count", 0L);
				return out;
			}
			q.put("__cid_in", cids.stream().map(String::valueOf).collect(Collectors.joining(",")));
		}
		QueryWrapper<ClientBill> qw = Wrappers.query();
		qw.eq("entid", entid);
		String cidIn = q.remove("__cid_in");
		if (StrUtil.isNotBlank(cidIn)) {
			qw.in("cid", java.util.Arrays.stream(cidIn.split(",")).map(Integer::parseInt).toList());
		}
		applyIndexFilters(qw, entid, q, true);
		qw.orderByDesc("date").orderByDesc("id");
		Page<ClientBill> pg = new Page<>(Math.max(page, 1), Math.max(limit, 1));
		Page<ClientBill> res = clientBillMapper.selectPage(pg, qw);
		Map<String, Object> out = new HashMap<>();
		out.put("list", res.getRecords());
		out.put("count", res.getTotal());
		return out;
	}

	private String genBillNo(int entid) {
		String day = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
		for (int i = 0; i < 30; i++) {
			String no = day + "_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6);
			Long c = clientBillMapper.selectCount(Wrappers.lambdaQuery(ClientBill.class)
				.eq(ClientBill::getEntid, entid).eq(ClientBill::getBillNo, no));
			if (c != null && c == 0) {
				return no;
			}
		}
		return day + "_" + System.currentTimeMillis();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ClientBill store(int entid, ClientBill body, String uidStr) {
		body.setEntid(entid);
		body.setUid(StrUtil.blankToDefault(uidStr, ""));
		body.setStatus(0);
		if (body.getBillNo() == null || body.getBillNo().isEmpty()) {
			body.setBillNo(genBillNo(entid));
		}
		if (body.getBillTypes() == null) {
			body.setBillTypes(body.getTypes() != null && body.getTypes() == 2 ? 0 : 1);
		}
		if (StrUtil.isBlank(body.getPayType())) {
			body.setPayType("");
		}
		clientBillMapper.insert(body);
		if (body.getCid() != null && body.getCid() > 0) {
			syncContractFinance(body.getCid());
		}
		if (body.getTypes() != null && body.getTypes() == 1 && body.getCid() != null && body.getCid() > 0) {
			Contract ct = contractMapper.selectById(body.getCid().longValue());
			if (ct != null) {
				ct.setRenew(1);
				contractMapper.updateById(ct);
			}
		}
		return body;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateBill(long id, int entid, ClientBill body, String uidStr, boolean finance) {
		ClientBill ex = clientBillMapper.selectOne(Wrappers.lambdaQuery(ClientBill.class)
			.eq(ClientBill::getId, id).eq(ClientBill::getEntid, entid));
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (!finance && ex.getStatus() != null && ex.getStatus() == 1) {
			throw new IllegalArgumentException("付款单已审核，无法修改");
		}
		body.setId(id);
		body.setEntid(entid);
		if (!finance) {
			body.setStatus(0);
		}
		if (StrUtil.isBlank(body.getBillNo())) {
			body.setBillNo(ex.getBillNo());
		}
		if (body.getPayType() == null) {
			body.setPayType(ex.getPayType());
		}
		clientBillMapper.updateById(body);
		syncContractFinance(ex.getCid());
		syncInvoicePrice(ex.getInvoiceId(), entid);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void destroy(long id, int entid) {
		ClientBill ex = clientBillMapper.selectOne(Wrappers.lambdaQuery(ClientBill.class)
			.eq(ClientBill::getId, id).eq(ClientBill::getEntid, entid));
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (ex.getStatus() == null || ex.getStatus() != 2) {
			throw new IllegalArgumentException("common.operation.noPermission");
		}
		clientBillMapper.deleteById(id);
		syncContractFinance(ex.getCid());
		syncInvoicePrice(ex.getInvoiceId(), entid);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void statusUpdate(long id, int entid, JsonNode body, String uuidForAttach) {
		ClientBill ex = clientBillMapper.selectOne(Wrappers.lambdaQuery(ClientBill.class)
			.eq(ClientBill::getId, id).eq(ClientBill::getEntid, entid));
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		int status = body.path("status").asInt(0);
		if (status != -1 && status != 1 && status != 2) {
			throw new IllegalArgumentException("审核状态异常");
		}
		if (status == 2 && StrUtil.isBlank(text(body, "fail_msg"))) {
			throw new IllegalArgumentException("common.empty.attr");
		}
		if (status == -1 && ex.getInvoiceId() != null && ex.getInvoiceId() > 0) {
			throw new IllegalArgumentException("已经关联发票的付款审核不可撤回");
		}
		if (status == 1) {
			int billCateId = body.path("bill_cate_id").asInt(0);
			if (billCateId < 1) {
				throw new IllegalArgumentException("请选择账目分类");
			}
			ex.setStatus(1);
			ex.setNum(decimal(body, "num", ex.getNum()));
			ex.setMark(text(body, "mark"));
			ex.setDate(parseDate(body, "date", ex.getDate()));
			ex.setTypes(body.path("types").asInt(ex.getTypes() == null ? 0 : ex.getTypes()));
			ex.setCateId(body.path("cate_id").asInt(ex.getCateId() == null ? 0 : ex.getCateId()));
			ex.setTypeId(body.path("type_id").asInt(ex.getTypeId() == null ? 0 : ex.getTypeId()));
			ex.setBillCateId(billCateId);
			if (StrUtil.isNotBlank(text(body, "pay_type"))) {
				ex.setPayType(text(body, "pay_type"));
			}
			clientBillMapper.updateById(ex);
		}
		else if (status == 2) {
			ex.setStatus(2);
			ex.setFailMsg(text(body, "fail_msg"));
			clientBillMapper.updateById(ex);
		}
		else {
			ex.setStatus(0);
			ex.setFailMsg("");
			clientBillMapper.updateById(ex);
		}
		syncContractFinance(ex.getCid());
		syncInvoicePrice(ex.getInvoiceId(), entid);
		reloadCustomerStatus(ex.getEid());
	}

	private static String text(JsonNode n, String k) {
		JsonNode v = n.path(k);
		return v.isMissingNode() || v.isNull() ? "" : v.asText("");
	}

	private static BigDecimal decimal(JsonNode n, String k, BigDecimal def) {
		JsonNode v = n.path(k);
		if (v.isMissingNode() || v.isNull()) {
			return def;
		}
		try {
			return new BigDecimal(v.asText());
		}
		catch (Exception e) {
			return def;
		}
	}

	private static LocalDateTime parseDate(JsonNode n, String k, LocalDateTime def) {
		String s = text(n, k);
		if (StrUtil.isBlank(s)) {
			return def;
		}
		try {
			return LocalDateTime.parse(s.replace(" ", "T"));
		}
		catch (Exception e1) {
			try {
				return java.time.LocalDate.parse(s).atStartOfDay();
			}
			catch (Exception e2) {
				return def;
			}
		}
	}

	@Override
	public void setMark(long id, String mark) {
		ClientBill ex = clientBillMapper.selectById(id);
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		ex.setMark(mark == null ? "" : mark);
		clientBillMapper.updateById(ex);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void withdraw(long id, int entid) {
		ClientBill ex = clientBillMapper.selectOne(Wrappers.lambdaQuery(ClientBill.class)
			.eq(ClientBill::getId, id).eq(ClientBill::getEntid, entid));
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (ex.getStatus() == null || ex.getStatus() != 0) {
			throw new IllegalArgumentException("撤回状态异常");
		}
		ex.setStatus(-1);
		clientBillMapper.updateById(ex);
		syncContractFinance(ex.getCid());
	}

	@Override
	public Map<String, String> priceStatistics(int entid, int eid) {
		QueryWrapper<ClientBill> audit = Wrappers.query();
		audit.eq("entid", entid);
		if (eid > 0) {
			audit.eq("eid", eid);
		}
		audit.eq("status", 0);
		BigDecimal auditPrice = sumMaps(audit);
		QueryWrapper<ClientBill> pay = Wrappers.query();
		pay.eq("entid", entid);
		if (eid > 0) {
			pay.eq("eid", eid);
		}
		pay.eq("status", 1);
		BigDecimal paymentPrice = sumMaps(pay);
		QueryWrapper<ClientBill> ret = Wrappers.query();
		ret.eq("entid", entid);
		if (eid > 0) {
			ret.eq("eid", eid);
		}
		ret.eq("types", 0).eq("status", 1);
		BigDecimal returnPrice = sumMaps(ret);
		Map<String, String> m = new HashMap<>();
		m.put("payment_price", fmtMoney(paymentPrice));
		m.put("audit_price", fmtMoney(auditPrice));
		m.put("unpaid_price", "0.00");
		return m;
	}

	@Override
	public List<ClientBill> unInvoicedList(int entid, int eid, Integer invoiceId, int page, int limit) {
		Page<ClientBill> pg = new Page<>(Math.max(page, 1), Math.max(limit, 1));
		return clientBillMapper.selectUnInvoicedPage(pg, entid, eid, invoiceId).getRecords();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void financeUpdate(long id, int entid, ClientBill body, String uidStr) {
		updateBill(id, entid, body, uidStr, true);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void financeDelete(long id, int entid) {
		ClientBill ex = clientBillMapper.selectOne(Wrappers.lambdaQuery(ClientBill.class)
			.eq(ClientBill::getId, id).eq(ClientBill::getEntid, entid));
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (ex.getStatus() == null || (ex.getStatus() != 1 && ex.getStatus() != 2)) {
			throw new IllegalArgumentException("common.operation.noPermission");
		}
		clientBillMapper.deleteById(id);
		syncContractFinance(ex.getCid());
	}

	@Override
	public ClientBill getInfo(long id, int entid) {
		ClientBill ex = clientBillMapper.selectOne(Wrappers.lambdaQuery(ClientBill.class)
			.eq(ClientBill::getId, id).eq(ClientBill::getEntid, entid));
		if (ex == null) {
			throw new IllegalArgumentException("付款记录不存在");
		}
		return ex;
	}

	@Override
	public Map<String, String> contractStatistics(int cid, int entid) {
		QueryWrapper<ClientBill> base = Wrappers.query();
		base.eq("entid", entid).eq("cid", cid);
		QueryWrapper<ClientBill> wPay = base.clone();
		wPay.in("types", 0, 1).eq("status", 1);
		BigDecimal paymentPrice = sumMaps(wPay);
		QueryWrapper<ClientBill> wExp = base.clone();
		wExp.eq("types", 2).eq("status", 1);
		BigDecimal expensePrice = sumMaps(wExp);
		QueryWrapper<ClientBill> wRet = base.clone();
		wRet.eq("types", 0).eq("status", 1);
		BigDecimal returnPrice = sumMaps(wRet);
		Contract c = contractMapper.selectById((long) cid);
		BigDecimal contractPrice = c != null && c.getContractPrice() != null ? c.getContractPrice() : BigDecimal.ZERO;
		BigDecimal unpaid = contractPrice.subtract(returnPrice);
		if (unpaid.compareTo(BigDecimal.ZERO) < 0) {
			unpaid = BigDecimal.ZERO;
		}
		Map<String, String> m = new HashMap<>();
		m.put("payment_price", fmtMoney(paymentPrice));
		m.put("expense_price", fmtMoney(expensePrice));
		m.put("unpaid_price", fmtMoney(unpaid));
		return m;
	}

	@Override
	public Map<String, String> customerStatistics(int eid, int entid) {
		QueryWrapper<ClientBill> base = Wrappers.query();
		base.eq("entid", entid).eq("eid", eid);
		QueryWrapper<ClientBill> wPay = base.clone();
		wPay.in("types", 0, 1).eq("status", 1);
		QueryWrapper<ClientBill> wExp = base.clone();
		wExp.eq("types", 2).eq("status", 1);
		QueryWrapper<ClientBill> wAi = base.clone();
		wAi.in("types", 0, 1).eq("status", 0);
		QueryWrapper<ClientBill> wAe = base.clone();
		wAe.eq("types", 2).eq("status", 0);
		Map<String, String> m = new HashMap<>();
		m.put("payment_price", fmtMoney(sumMaps(wPay)));
		m.put("expense_price", fmtMoney(sumMaps(wExp)));
		m.put("audit_income_price", fmtMoney(sumMaps(wAi)));
		m.put("audit_expense_price", fmtMoney(sumMaps(wAe)));
		return m;
	}

	@Override
	public Object getRenewCensus(int cid, int entid) {
		return buildRenewCensus(cid, entid);
	}

	/**
	 * 合同续费到期摘要：按续费类型 {@code cate_id} 聚合，取已审核记录中 {@code end_date} 最晚的一条；名称来自
	 * {@code eb_system_group_data.value} 的 JSON {@code title}。字段对齐模板
	 * {@code contractRenew.vue} / {@code renewCensus.vue}。
	 */
	private List<Map<String, Object>> buildRenewCensus(int cid, int entid) {
		List<ClientBill> bills = clientBillMapper.selectList(Wrappers.lambdaQuery(ClientBill.class)
			.eq(ClientBill::getEntid, entid)
			.eq(ClientBill::getCid, cid)
			.eq(ClientBill::getTypes, 1)
			.eq(ClientBill::getStatus, 1)
			.gt(ClientBill::getCateId, 0));
		if (bills.isEmpty()) {
			return Collections.emptyList();
		}
		Map<Integer, ClientBill> best = new LinkedHashMap<>();
		for (ClientBill b : bills) {
			int cate = b.getCateId();
			ClientBill cur = best.get(cate);
			if (cur == null) {
				best.put(cate, b);
				continue;
			}
			int cmp = compareRenewEnd(b.getEndDate(), cur.getEndDate());
			if (cmp > 0 || (cmp == 0 && b.getId() != null && cur.getId() != null && b.getId() > cur.getId())) {
				best.put(cate, b);
			}
		}
		Map<Integer, String> titles = loadGroupDataTitles(best.keySet());
		LocalDate today = LocalDate.now(ZoneId.systemDefault());
		List<Map<String, Object>> rows = new ArrayList<>();
		for (Map.Entry<Integer, ClientBill> e : best.entrySet()) {
			ClientBill b = e.getValue();
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("cate_id", e.getKey());
			Map<String, String> renew = new HashMap<>(2);
			renew.put("title", titles.getOrDefault(e.getKey(), ""));
			row.put("renew", renew);
			LocalDateTime end = b.getEndDate();
			String endStr = formatRenewEndForApi(end);
			row.put("end_date", endStr);
			row.put("date", endStr);
			row.put("renew_status", renewStatus(end, today));
			rows.add(row);
		}
		rows.sort(Comparator.comparing(r -> (Integer) r.get("cate_id")));
		return rows;
	}

	private Map<Integer, String> loadGroupDataTitles(Iterable<Integer> cateIds) {
		List<Integer> ids = new ArrayList<>();
		for (Integer id : cateIds) {
			if (id != null && id > 0) {
				ids.add(id);
			}
		}
		if (ids.isEmpty()) {
			return Collections.emptyMap();
		}
		List<SystemGroupData> list = systemGroupDataMapper.selectList(Wrappers.lambdaQuery(SystemGroupData.class)
			.in(SystemGroupData::getId, ids.stream().map(Integer::longValue).toList()));
		Map<Integer, String> out = new HashMap<>();
		for (SystemGroupData g : list) {
			if (g.getId() != null) {
				out.put(g.getId().intValue(), parseTitleFromGroupValue(g.getValue()));
			}
		}
		return out;
	}

	private String parseTitleFromGroupValue(String json) {
		if (StrUtil.isBlank(json)) {
			return "";
		}
		try {
			JsonNode n = objectMapper.readTree(json);
			JsonNode t = n.get("title");
			return t != null && t.isTextual() ? t.asText() : "";
		}
		catch (JsonProcessingException e) {
			return "";
		}
	}

	private static boolean validRenewEnd(LocalDateTime end) {
		return end != null && end.getYear() > 1971;
	}

	/** 越大越优（更晚的到期日优先）。 */
	private static int compareRenewEnd(LocalDateTime a, LocalDateTime b) {
		boolean av = validRenewEnd(a);
		boolean bv = validRenewEnd(b);
		if (av && !bv) {
			return 1;
		}
		if (!av && bv) {
			return -1;
		}
		if (!av && !bv) {
			return 0;
		}
		return a.compareTo(b);
	}

	private static String formatRenewEndForApi(LocalDateTime end) {
		if (!validRenewEnd(end)) {
			return "0000-00-00 00:00:00";
		}
		return end.format(END_TS);
	}

	/** 0 正常，1 30 天内到期，2 已过期或日期无效。 */
	private static int renewStatus(LocalDateTime end, LocalDate today) {
		if (!validRenewEnd(end)) {
			return 2;
		}
		long days = ChronoUnit.DAYS.between(today, end.toLocalDate());
		if (days < 0) {
			return 2;
		}
		if (days <= RENEW_URGENT_DAYS) {
			return 1;
		}
		return 0;
	}

	private void syncContractFinance(Integer cid) {
		if (cid == null || cid <= 0) {
			return;
		}
		Contract c = contractMapper.selectById(cid.longValue());
		if (c == null) {
			return;
		}
		QueryWrapper<ClientBill> wRec = Wrappers.query();
		wRec.eq("cid", cid).eq("status", 1).in("types", 0, 1);
		BigDecimal received = sumMaps(wRec);
		QueryWrapper<ClientBill> wRet = Wrappers.query();
		wRet.eq("cid", cid).eq("status", 1).eq("types", 0);
		BigDecimal returnOnly = sumMaps(wRet);
		c.setReceived(received);
		BigDecimal cp = c.getContractPrice() != null ? c.getContractPrice() : BigDecimal.ZERO;
		BigDecimal surplus = cp.subtract(returnOnly);
		if (surplus.compareTo(BigDecimal.ZERO) < 0) {
			surplus = BigDecimal.ZERO;
		}
		c.setSurplus(surplus);
		contractMapper.updateById(c);
	}

	private void syncInvoicePrice(Integer invoiceId, int entid) {
		if (invoiceId == null || invoiceId <= 0) {
			return;
		}
		ClientInvoice inv = clientInvoiceMapper.selectOne(Wrappers.lambdaQuery(ClientInvoice.class)
			.eq(ClientInvoice::getId, invoiceId.longValue()).eq(ClientInvoice::getEntid, entid));
		if (inv == null) {
			return;
		}
		QueryWrapper<ClientBill> w = Wrappers.query();
		w.eq("invoice_id", invoiceId).eq("entid", entid).eq("status", 1);
		BigDecimal sum = sumMaps(w);
		if (inv.getPrice() == null || inv.getPrice().compareTo(sum) != 0) {
			inv.setPrice(sum);
			clientInvoiceMapper.updateById(inv);
		}
	}

	private void reloadCustomerStatus(Integer eid) {
		if (eid == null || eid <= 0) {
			return;
		}
		Customer cu = customerMapper.selectById(eid.longValue());
		if (cu == null || "2".equals(cu.getCustomerStatus())) {
			return;
		}
		long traded = clientBillMapper.selectCount(Wrappers.lambdaQuery(ClientBill.class)
			.eq(ClientBill::getEid, eid).in(ClientBill::getTypes, 0, 1).eq(ClientBill::getStatus, 1));
		cu.setCustomerStatus(traded > 0 ? "1" : "0");
		customerMapper.updateById(cu);
	}

}
