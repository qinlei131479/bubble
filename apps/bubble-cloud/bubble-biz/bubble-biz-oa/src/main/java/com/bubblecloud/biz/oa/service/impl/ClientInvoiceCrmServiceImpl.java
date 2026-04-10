package com.bubblecloud.biz.oa.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.ClientBillMapper;
import com.bubblecloud.biz.oa.mapper.ClientInvoiceLogMapper;
import com.bubblecloud.biz.oa.mapper.ClientInvoiceMapper;
import com.bubblecloud.biz.oa.mapper.ContractMapper;
import com.bubblecloud.biz.oa.service.ClientInvoiceCrmService;
import com.bubblecloud.oa.api.entity.ClientBill;
import com.bubblecloud.oa.api.entity.ClientInvoice;
import com.bubblecloud.oa.api.entity.ClientInvoiceLog;
import com.bubblecloud.oa.api.entity.Contract;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.StrUtil;

/**
 * 发票主流程实现。
 *
 * @author qinlei
 * @date 2026/4/3 16:00
 */
@Service
public class ClientInvoiceCrmServiceImpl implements ClientInvoiceCrmService {

	private final ClientInvoiceMapper clientInvoiceMapper;

	private final ClientBillMapper clientBillMapper;

	private final ClientInvoiceLogMapper clientInvoiceLogMapper;

	private final ContractMapper contractMapper;

	private final ObjectMapper objectMapper;

	public ClientInvoiceCrmServiceImpl(ClientInvoiceMapper clientInvoiceMapper, ClientBillMapper clientBillMapper,
			ClientInvoiceLogMapper clientInvoiceLogMapper, ContractMapper contractMapper, ObjectMapper objectMapper) {
		this.clientInvoiceMapper = clientInvoiceMapper;
		this.clientBillMapper = clientBillMapper;
		this.clientInvoiceLogMapper = clientInvoiceLogMapper;
		this.contractMapper = contractMapper;
		this.objectMapper = objectMapper;
	}

	private static Integer parseInt(Map<String, String> q, String k) {
		if (q == null || !q.containsKey(k)) {
			return null;
		}
		try {
			String s = q.get(k);
			if (s == null || s.isBlank()) {
				return null;
			}
			return Integer.parseInt(s.trim());
		}
		catch (Exception e) {
			return null;
		}
	}

	private static String fmt(BigDecimal v) {
		if (v == null) {
			return "0.00";
		}
		return v.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}

	private void applyInvoiceFilters(QueryWrapper<ClientInvoice> qw, int entid, Map<String, String> q,
			boolean financeList) {
		qw.eq("entid", entid);
		Integer eid = parseInt(q, "eid");
		if (eid != null) {
			qw.eq("eid", eid);
		}
		Integer cid = parseInt(q, "cid");
		if (cid != null) {
			qw.eq("cid", cid);
		}
		String types = q == null ? null : q.get("types");
		if (StrUtil.isNotBlank(types)) {
			qw.eq("types", types);
		}
		String statusStr = q == null ? null : q.get("status");
		if (financeList) {
			if (StrUtil.isBlank(statusStr)) {
				qw.in("status", 1, -1, 4, 5);
			}
			else {
				qw.eq("status", Integer.parseInt(statusStr.trim()));
			}
		}
		else {
			if (StrUtil.isNotBlank(statusStr)) {
				qw.eq("status", Integer.parseInt(statusStr.trim()));
			}
		}
		String name = q == null ? null : q.get("name");
		if (StrUtil.isNotBlank(name)) {
			qw.like("name", name.trim());
		}
		Integer uid = parseInt(q, "uid");
		if (uid != null) {
			qw.eq("uid", String.valueOf(uid));
		}
		Integer categoryId = parseInt(q, "category_id");
		if (categoryId != null) {
			qw.eq("category_id", categoryId);
		}
	}

	@Override
	public Map<String, Object> index(int entid, Map<String, String> query, int page, int limit) {
		QueryWrapper<ClientInvoice> qw = Wrappers.query();
		applyInvoiceFilters(qw, entid, query, false);
		qw.orderByDesc("created_at").orderByDesc("id");
		Page<ClientInvoice> pg = new Page<>(Math.max(page, 1), Math.max(limit, 1));
		Page<ClientInvoice> res = clientInvoiceMapper.selectPage(pg, qw);
		Map<String, Object> out = new HashMap<>();
		out.put("list", res.getRecords());
		out.put("count", res.getTotal());
		return out;
	}

	@Override
	public Map<String, Object> listFinance(int entid, Map<String, String> query, int page, int limit) {
		QueryWrapper<ClientInvoice> qw = Wrappers.query();
		applyInvoiceFilters(qw, entid, query, true);
		qw.orderByDesc("created_at").orderByDesc("id");
		Page<ClientInvoice> pg = new Page<>(Math.max(page, 1), Math.max(limit, 1));
		Page<ClientInvoice> res = clientInvoiceMapper.selectPage(pg, qw);
		Map<String, Object> out = new HashMap<>();
		out.put("list", res.getRecords());
		out.put("count", res.getTotal());
		return out;
	}

	private ClientInvoice fromJson(JsonNode n) {
		try {
			ClientInvoice inv = objectMapper.treeToValue(n, ClientInvoice.class);
			JsonNode typesNode = n.get("types");
			if (typesNode != null && !typesNode.isNull() && inv.getTypes() == null) {
				inv.setTypes(typesNode.asText());
			}
			return inv;
		}
		catch (Exception e) {
			throw new IllegalArgumentException("参数解析失败");
		}
	}

	private List<Integer> billIds(JsonNode n) {
		JsonNode arr = n.get("bill_id");
		if (arr == null || !arr.isArray()) {
			return List.of();
		}
		List<Integer> ids = new ArrayList<>();
		for (JsonNode x : arr) {
			if (x.isInt()) {
				ids.add(x.asInt());
			}
			else if (x.isTextual()) {
				try {
					ids.add(Integer.parseInt(x.asText()));
				}
				catch (NumberFormatException ignored) {
				}
			}
		}
		return ids;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ClientInvoice store(int entid, JsonNode body, String uidStr) {
		ClientInvoice inv = fromJson(body);
		inv.setEntid(entid);
		inv.setUid(StrUtil.blankToDefault(uidStr, ""));
		inv.setCreator(StrUtil.blankToDefault(uidStr, ""));
		inv.setInvoiceUnique(UUID.randomUUID().toString().replace("-", ""));
		if (inv.getStatus() == null) {
			inv.setStatus(0);
		}
		List<Integer> bIds = billIds(body);
		BigDecimal price = BigDecimal.ZERO;
		if (!bIds.isEmpty()) {
			for (Integer bid : bIds) {
				ClientBill b = clientBillMapper.selectById(bid.longValue());
				if (b == null || !java.util.Objects.equals(b.getEntid(), entid) || b.getStatus() == null
						|| b.getStatus() != 1) {
					throw new IllegalArgumentException("申请失败,请核对付款单");
				}
				price = price.add(b.getNum() == null ? BigDecimal.ZERO : b.getNum());
			}
		}
		else if (inv.getCid() != null && inv.getCid() > 0) {
			Contract ct = contractMapper.selectById(inv.getCid().longValue());
			if (ct != null && ct.getContractPrice() != null) {
				price = ct.getContractPrice();
			}
		}
		inv.setPrice(price);
		try {
			inv.setLinkBill(objectMapper.writeValueAsString(bIds));
		}
		catch (Exception e) {
			inv.setLinkBill("[]");
		}
		clientInvoiceMapper.insert(inv);
		if (!bIds.isEmpty()) {
			for (Integer bid : bIds) {
				ClientBill b = clientBillMapper.selectById(bid.longValue());
				b.setInvoiceId(inv.getId().intValue());
				clientBillMapper.updateById(b);
			}
		}
		return inv;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(long id, int entid, JsonNode body) {
		ClientInvoice ex = clientInvoiceMapper.selectOne(Wrappers.lambdaQuery(ClientInvoice.class)
			.eq(ClientInvoice::getId, id)
			.eq(ClientInvoice::getEntid, entid));
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (ex.getStatus() != null && ex.getStatus() == 1) {
			throw new IllegalArgumentException("common.operation.noPermission");
		}
		ClientInvoice inv = fromJson(body);
		inv.setId(id);
		inv.setEntid(entid);
		List<Integer> bIds = billIds(body);
		if (!bIds.isEmpty()) {
			BigDecimal price = BigDecimal.ZERO;
			for (Integer bid : bIds) {
				ClientBill b = clientBillMapper.selectById(bid.longValue());
				if (b == null || !java.util.Objects.equals(b.getEntid(), entid) || b.getStatus() == null
						|| b.getStatus() != 1) {
					throw new IllegalArgumentException("申请失败,请核对付款单");
				}
				price = price.add(b.getNum() == null ? BigDecimal.ZERO : b.getNum());
			}
			inv.setPrice(price);
		}
		if (!bIds.isEmpty()) {
			try {
				inv.setLinkBill(objectMapper.writeValueAsString(bIds));
			}
			catch (Exception e) {
				inv.setLinkBill("[]");
			}
		}
		clientInvoiceMapper.updateById(inv);
		if (!bIds.isEmpty()) {
			clientBillMapper.update(null,
					Wrappers.lambdaUpdate(ClientBill.class)
						.eq(ClientBill::getInvoiceId, id)
						.set(ClientBill::getInvoiceId, 0));
			for (Integer bid : bIds) {
				ClientBill b = clientBillMapper.selectById(bid.longValue());
				b.setInvoiceId((int) id);
				clientBillMapper.updateById(b);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void destroy(long id) {
		ClientInvoice ex = clientInvoiceMapper.selectById(id);
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (ex.getStatus() != null && ex.getStatus() == 1) {
			throw new IllegalArgumentException("common.operation.noPermission");
		}
		clientInvoiceMapper.deleteById(id);
	}

	@Override
	public List<ClientBill> billList(long invoiceId) {
		ClientInvoice inv = clientInvoiceMapper.selectById(invoiceId);
		if (inv == null) {
			return List.of();
		}
		if (StrUtil.isNotBlank(inv.getLinkBill())) {
			try {
				JsonNode node = objectMapper.readTree(inv.getLinkBill());
				if (node.isArray()) {
					List<Long> ids = new ArrayList<>();
					for (JsonNode x : node) {
						ids.add(x.asLong());
					}
					if (!ids.isEmpty()) {
						return clientBillMapper
							.selectList(Wrappers.lambdaQuery(ClientBill.class).in(ClientBill::getId, ids));
					}
				}
			}
			catch (Exception ignored) {
			}
		}
		return clientBillMapper
			.selectList(Wrappers.lambdaQuery(ClientBill.class).eq(ClientBill::getInvoiceId, invoiceId));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void statusAudit(long id, int entid, JsonNode body, int status) {
		if (status != 1 && status != 2) {
			throw new IllegalArgumentException("参数错误");
		}
		ClientInvoice inv = clientInvoiceMapper.selectOne(Wrappers.lambdaQuery(ClientInvoice.class)
			.eq(ClientInvoice::getId, id)
			.eq(ClientInvoice::getEntid, entid));
		if (inv == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (status == 1) {
			inv.setRealDate(LocalDate.now());
			inv.setStatus(5);
			if (StrUtil.isNotBlank(body.path("remark").asText(null))) {
				inv.setRemark(body.path("remark").asText());
			}
		}
		else {
			if (StrUtil.isBlank(body.path("remark").asText())) {
				throw new IllegalArgumentException("common.empty.attr");
			}
			inv.setStatus(2);
			inv.setRemark(body.path("remark").asText());
		}
		clientInvoiceMapper.updateById(inv);
	}

	@Override
	public void setMark(long id, String mark) {
		ClientInvoice ex = clientInvoiceMapper.selectById(id);
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		ex.setMark(mark == null ? "" : mark);
		clientInvoiceMapper.updateById(ex);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void shift(JsonNode body) {
		JsonNode data = body.get("data");
		if (data == null || !data.isArray() || data.isEmpty()) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		int toUid = body.path("to_uid").asInt(0);
		if (toUid <= 0) {
			throw new IllegalArgumentException("common.empty.attr");
		}
		String uidStr = String.valueOf(toUid);
		List<Long> ids = new ArrayList<>();
		for (JsonNode x : data) {
			ids.add(x.asLong());
		}
		clientInvoiceMapper.update(null,
				Wrappers.lambdaUpdate(ClientInvoice.class)
					.in(ClientInvoice::getId, ids)
					.set(ClientInvoice::getUid, uidStr));
	}

	@Override
	public List<Map<String, Object>> invalidForm(long id) {
		if (id <= 0) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		List<Map<String, Object>> rows = new ArrayList<>();
		Map<String, Object> h = new HashMap<>();
		h.put("field", "remark");
		h.put("label", "作废原因");
		h.put("type", "textarea");
		h.put("required", true);
		rows.add(h);
		return rows;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void invalidApply(long id, int invalid, String remark) {
		ClientInvoice inv = clientInvoiceMapper.selectById(id);
		if (inv == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (invalid == 1) {
			if (inv.getStatus() == null || (inv.getStatus() != 1 && inv.getStatus() != 5 && inv.getStatus() != 6)) {
				throw new IllegalArgumentException("当年发票不能申请作废, 请核对发票状态");
			}
			if (StrUtil.isBlank(remark)) {
				throw new IllegalArgumentException("请填写作废原因");
			}
			inv.setStatus(3);
			inv.setCardRemark(remark);
		}
		else {
			if (inv.getStatus() == null || inv.getStatus() != 3) {
				throw new IllegalArgumentException("发票状态异常, 请稍后再试");
			}
			inv.setStatus(-1);
		}
		clientInvoiceMapper.updateById(inv);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void invalidReview(long id, int invalid, String remark) {
		if (invalid != 2 && StrUtil.isBlank(remark)) {
			throw new IllegalArgumentException("请填写作废原因");
		}
		ClientInvoice inv = clientInvoiceMapper.selectById(id);
		if (inv == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (inv.getStatus() == null || inv.getStatus() != 5) {
			throw new IllegalArgumentException("当前发票不能操作作废, 请核对发票状态");
		}
		if (invalid == 2) {
			inv.setStatus(-1);
			inv.setFinanceRemark(remark);
		}
		else {
			inv.setStatus(5);
			inv.setCardRemark(remark);
		}
		clientInvoiceMapper.updateById(inv);
	}

	@Override
	public Map<String, String> priceStatistics(int entid, Integer eid, Integer cid) {
		QueryWrapper<ClientInvoice> w1 = Wrappers.query();
		w1.eq("entid", entid);
		if (eid != null && eid > 0) {
			w1.eq("eid", eid);
		}
		if (cid != null && cid > 0) {
			w1.eq("cid", cid);
		}
		w1.eq("status", 5);
		BigDecimal cum = sumAmount(w1);
		QueryWrapper<ClientInvoice> w2 = Wrappers.query();
		w2.eq("entid", entid);
		if (eid != null && eid > 0) {
			w2.eq("eid", eid);
		}
		if (cid != null && cid > 0) {
			w2.eq("cid", cid);
		}
		w2.eq("status", 0);
		BigDecimal audit = sumAmount(w2);
		QueryWrapper<ClientBill> wb = Wrappers.query();
		wb.eq("entid", entid);
		if (eid != null && eid > 0) {
			wb.eq("eid", eid);
		}
		if (cid != null && cid > 0) {
			wb.eq("cid", cid);
		}
		wb.in("types", 0, 1);
		wb.select("COALESCE(SUM(num),0) AS t");
		List<Map<String, Object>> maps = clientBillMapper.selectMaps(wb);
		BigDecimal pay = extractSum(maps);
		Map<String, String> m = new HashMap<>();
		m.put("cumulative_invoiced_price", fmt(cum));
		m.put("audit_invoiced_price", fmt(audit));
		m.put("cumulative_payment_price", fmt(pay));
		return m;
	}

	private BigDecimal sumAmount(QueryWrapper<ClientInvoice> qw) {
		qw.select("COALESCE(SUM(amount),0) AS t");
		List<Map<String, Object>> maps = clientInvoiceMapper.selectMaps(qw);
		return extractSum(maps);
	}

	private static BigDecimal extractSum(List<Map<String, Object>> maps) {
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void withdraw(long id, String remark) {
		ClientInvoice inv = clientInvoiceMapper.selectById(id);
		if (inv == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (inv.getStatus() != null && inv.getStatus() == 1) {
			throw new IllegalArgumentException("common.operation.noPermission");
		}
		if (StrUtil.isBlank(remark)) {
			throw new IllegalArgumentException("common.empty.attr");
		}
		if (inv.getStatus() == null || inv.getStatus() != 0) {
			throw new IllegalArgumentException("发票撤回失败, 请核对发票状态");
		}
		inv.setStatus(-1);
		inv.setRemark(remark);
		inv.setRealDate(null);
		clientInvoiceMapper.updateById(inv);
	}

	@Override
	public ClientInvoice info(long id, int entid) {
		ClientInvoice inv = clientInvoiceMapper.selectOne(Wrappers.lambdaQuery(ClientInvoice.class)
			.eq(ClientInvoice::getId, id)
			.eq(ClientInvoice::getEntid, entid));
		if (inv == null) {
			throw new IllegalArgumentException("发票不存在");
		}
		return inv;
	}

	@Override
	public Map<String, Object> invoiceUri(long id) {
		ClientInvoice inv = clientInvoiceMapper.selectById(id);
		if (inv == null) {
			throw new IllegalArgumentException("发票不存在");
		}
		inv.setInvoiceUnique(UUID.randomUUID().toString().replace("-", ""));
		clientInvoiceMapper.updateById(inv);
		Map<String, Object> m = new HashMap<>();
		m.put("uri", "");
		m.put("tip", "Java 端未对接第三方在线开票网关，请使用原 PHP 服务或后续接入开票 SDK。");
		m.put("unique", inv.getInvoiceUnique());
		return m;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void callback(long id, String invoiceNum, String serialNumber) {
		if (StrUtil.isBlank(serialNumber)) {
			throw new IllegalArgumentException("缺少发票号码");
		}
		ClientInvoice inv = clientInvoiceMapper.selectById(id);
		if (inv == null) {
			throw new IllegalArgumentException("缺少发票信息");
		}
		inv.setSerialNumber(serialNumber);
		if (StrUtil.isNotBlank(invoiceNum)) {
			inv.setNum(invoiceNum);
		}
		inv.setStatus(5);
		clientInvoiceMapper.updateById(inv);
	}

	@Override
	public List<ClientInvoiceLog> record(long invoiceId, int entid) {
		return clientInvoiceLogMapper.selectList(Wrappers.lambdaQuery(ClientInvoiceLog.class)
			.eq(ClientInvoiceLog::getInvoiceId, (int) invoiceId)
			.eq(ClientInvoiceLog::getEntid, entid)
			.orderByAsc(ClientInvoiceLog::getId));
	}

}
