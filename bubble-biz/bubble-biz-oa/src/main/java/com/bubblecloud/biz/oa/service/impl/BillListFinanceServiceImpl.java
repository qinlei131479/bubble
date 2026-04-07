package com.bubblecloud.biz.oa.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.BillCategoryMapper;
import com.bubblecloud.biz.oa.mapper.BillListMapper;
import com.bubblecloud.biz.oa.mapper.ClientBillLogMapper;
import com.bubblecloud.biz.oa.mapper.EnterprisePaytypeMapper;
import com.bubblecloud.biz.oa.service.BillListFinanceService;
import com.bubblecloud.oa.api.dto.finance.BillListFinanceQuery;
import com.bubblecloud.oa.api.dto.finance.BillListSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.BillCategory;
import com.bubblecloud.oa.api.entity.BillList;
import com.bubblecloud.oa.api.entity.ClientBillLog;
import com.bubblecloud.oa.api.entity.EnterprisePaytype;
import com.bubblecloud.oa.api.vo.finance.BillListFinancePageVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 财务流水实现。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Service
@RequiredArgsConstructor
public class BillListFinanceServiceImpl implements BillListFinanceService {

	private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final BillListMapper billListMapper;

	private final BillCategoryMapper billCategoryMapper;

	private final EnterprisePaytypeMapper enterprisePaytypeMapper;

	private final ClientBillLogMapper clientBillLogMapper;

	private final AdminMapper adminMapper;

	private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

	@Override
	public BillListFinancePageVO postList(JsonNode body) {
		BillListFinanceQuery q = parseQuery(body);
		int page = body.path("page").asInt(1);
		int limit = body.path("limit").asInt(20);
		Page<BillList> pg = new Page<>(page, limit);
		billListMapper.selectFinancePage(pg, q);
		BillListFinanceQuery sumQ = copySumQuery(q);
		BigDecimal income = ObjectUtil.defaultIfNull(billListMapper.sumNum(sumQ, 1), BigDecimal.ZERO);
		BigDecimal expend = ObjectUtil.defaultIfNull(billListMapper.sumNum(sumQ, 0), BigDecimal.ZERO);
		return new BillListFinancePageVO(pg.getRecords(), pg.getTotal(), income, expend);
	}

	@Override
	public ObjectNode emptyChartShell() {
		ObjectNode root = objectMapper.createObjectNode();
		root.set("xAxis", objectMapper.createArrayNode());
		root.set("series", objectMapper.createArrayNode());
		root.set("incomeRank", objectMapper.createArrayNode());
		root.set("expendRank", objectMapper.createArrayNode());
		return root;
	}

	@Override
	public ObjectNode rankAnalysisStub() {
		return objectMapper.createObjectNode();
	}

	@Override
	public List<ClientBillLog> listLogs(Long entid, Long billListId) {
		return clientBillLogMapper
			.selectList(com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery(ClientBillLog.class)
				.eq(ClientBillLog::getEntid, entid.intValue())
				.eq(ClientBillLog::getBillListId, billListId.intValue())
				.orderByDesc(ClientBillLog::getId));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void importRows(JsonNode body, Long operatorUserId) {
		JsonNode data = body.get("data");
		if (data == null || !data.isArray() || data.isEmpty()) {
			throw new IllegalArgumentException("导入内容不能为空");
		}
		Admin admin = adminMapper.selectById(operatorUserId);
		String uuid = admin != null ? StrUtil.nullToEmpty(admin.getUid()) : "";
		for (JsonNode row : data) {
			String typesLabel = row.path("types").asText("");
			int types = "收入".equals(typesLabel) ? 1 : 0;
			if (!"收入".equals(typesLabel) && !"支出".equals(typesLabel)) {
				continue;
			}
			BillListSaveDTO dto = new BillListSaveDTO();
			dto.setTypes(types);
			dto.setNum(new BigDecimal(row.path("num").asText("0")));
			dto.setMark(row.path("mark").asText(""));
			dto.setEntid(1L);
			dto.setCateId(row.path("cate_id").asInt(0));
			dto.setTypeId(0);
			String et = row.path("edit_time").asText("");
			if (StrUtil.isNotBlank(et) && et.length() >= 8) {
				try {
					dto.setEditTime(LocalDateTime.parse(et, DT));
				}
				catch (Exception e) {
					dto.setEditTime(null);
				}
			}
			createBill(dto, operatorUserId);
			// pay_type name mapping simplified: type_id stays 0 unless matched
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public BillList createBill(BillListSaveDTO dto, Long operatorUserId) {
		validateCateTypes(dto.getCateId(), dto.getTypes());
		String payName = resolvePayTypeName(dto.getTypeId(), dto.getEntid());
		Admin admin = adminMapper.selectById(operatorUserId);
		String uuid = admin != null ? StrUtil.nullToEmpty(admin.getUid()) : "";
		BillList e = new BillList();
		e.setEntid(ObjectUtil.defaultIfNull(dto.getEntid(), 1L));
		e.setUserId(admin != null ? admin.getId().intValue() : 0);
		e.setUid(uuid);
		e.setCateId(ObjectUtil.defaultIfNull(dto.getCateId(), 0));
		e.setNum(ObjectUtil.defaultIfNull(dto.getNum(), BigDecimal.ZERO));
		e.setEditTime(dto.getEditTime());
		e.setTypes(ObjectUtil.defaultIfNull(dto.getTypes(), 0));
		e.setTypeId(ObjectUtil.defaultIfNull(dto.getTypeId(), 0));
		e.setPayType(payName);
		e.setMark(StrUtil.nullToEmpty(dto.getMark()));
		e.setLinkId(0);
		e.setOrderId(0);
		e.setLinkCate("");
		e.setCreatedAt(LocalDateTime.now());
		e.setUpdatedAt(LocalDateTime.now());
		billListMapper.insert(e);
		appendLog(e.getEntid().intValue(), e.getId().intValue(),
				ObjectUtil.defaultIfNull(operatorUserId, 0L).intValue(), 0, "创建");
		return e;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateBill(Long id, BillListSaveDTO dto, Long operatorUserId) {
		BillList existing = billListMapper.selectById(id);
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException("数据不存在");
		}
		validateCateTypes(dto.getCateId(), dto.getTypes());
		String payName = resolvePayTypeName(dto.getTypeId(), dto.getEntid());
		existing.setCateId(ObjectUtil.defaultIfNull(dto.getCateId(), 0));
		existing.setNum(ObjectUtil.defaultIfNull(dto.getNum(), BigDecimal.ZERO));
		existing.setEditTime(dto.getEditTime());
		existing.setTypes(ObjectUtil.defaultIfNull(dto.getTypes(), 0));
		existing.setTypeId(ObjectUtil.defaultIfNull(dto.getTypeId(), 0));
		existing.setPayType(payName);
		existing.setMark(StrUtil.nullToEmpty(dto.getMark()));
		existing.setUpdatedAt(LocalDateTime.now());
		billListMapper.updateById(existing);
		appendLog(ObjectUtil.defaultIfNull(dto.getEntid(), existing.getEntid()).intValue(), id.intValue(),
				ObjectUtil.defaultIfNull(operatorUserId, 0L).intValue(), 1, "修改");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBill(Long id) {
		BillList existing = billListMapper.selectById(id);
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException("数据不存在");
		}
		billListMapper.deleteById(id);
	}

	private void appendLog(int entid, int billListId, int uid, int type, String op) {
		ClientBillLog log = new ClientBillLog();
		log.setEntid(entid);
		log.setBillListId(billListId);
		log.setUid(uid);
		log.setType(type);
		try {
			log.setOperation(objectMapper.writeValueAsString(objectMapper.createObjectNode().put("msg", op)));
		}
		catch (Exception e) {
			log.setOperation("{\"msg\":\"" + op + "\"}");
		}
		log.setCreatedAt(LocalDateTime.now());
		log.setUpdatedAt(LocalDateTime.now());
		clientBillLogMapper.insert(log);
	}

	private void validateCateTypes(Integer cateId, Integer types) {
		if (ObjectUtil.isNull(cateId) || cateId <= 0) {
			return;
		}
		BillCategory c = billCategoryMapper.selectById(cateId.longValue());
		if (ObjectUtil.isNull(c)) {
			throw new IllegalArgumentException("分类不存在");
		}
		if (!c.getTypes().equals(types)) {
			throw new IllegalArgumentException("请选择正确的分类");
		}
	}

	private String resolvePayTypeName(Integer typeId, Long entid) {
		if (ObjectUtil.isNull(typeId) || typeId <= 0) {
			return "";
		}
		EnterprisePaytype p = enterprisePaytypeMapper.selectById(typeId.longValue());
		if (ObjectUtil.isNull(p)) {
			return "";
		}
		if (ObjectUtil.isNotNull(entid) && p.getEntid() != null && !p.getEntid().equals(entid.intValue())) {
			return "";
		}
		return StrUtil.nullToEmpty(p.getName());
	}

	private BillListFinanceQuery parseQuery(JsonNode body) {
		BillListFinanceQuery q = new BillListFinanceQuery();
		q.setEntid(body.path("entid").asLong(1L));
		if (body.hasNonNull("types") && !StrUtil.isBlank(body.get("types").asText())) {
			q.setTypes(body.get("types").asInt());
		}
		if (body.hasNonNull("type_id") && StrUtil.isNotBlank(body.get("type_id").asText())) {
			q.setTypeId(body.get("type_id").asInt());
		}
		if (body.hasNonNull("name")) {
			q.setMarkLike(body.get("name").asText());
		}
		q.setCateIds(flattenCateIds(body.get("cate_id")));
		parseTimeRange(body.path("time").asText(""), q);
		return q;
	}

	private List<Integer> flattenCateIds(JsonNode node) {
		if (node == null || node.isNull()) {
			return null;
		}
		List<Integer> out = new ArrayList<>();
		if (node.isArray()) {
			for (JsonNode n : node) {
				if (n.isArray()) {
					for (JsonNode inner : n) {
						if (inner.isNumber()) {
							out.add(inner.intValue());
						}
					}
				}
				else if (n.isNumber()) {
					out.add(n.intValue());
				}
			}
		}
		return out.isEmpty() ? null : out;
	}

	private void parseTimeRange(String time, BillListFinanceQuery q) {
		if (StrUtil.isBlank(time)) {
			return;
		}
		String[] parts = time.split(",", 2);
		if (parts.length == 2) {
			try {
				q.setTimeStart(LocalDateTime.parse(parts[0].trim(), DT));
				q.setTimeEnd(LocalDateTime.parse(parts[1].trim(), DT));
			}
			catch (Exception ignored) {
			}
			return;
		}
		String[] sp = time.split("\\s+-\\s+", 2);
		if (sp.length == 2) {
			try {
				q.setTimeStart(LocalDateTime.parse(sp[0].trim(), DT));
				q.setTimeEnd(LocalDateTime.parse(sp[1].trim(), DT));
			}
			catch (Exception ignored) {
			}
		}
	}

	private BillListFinanceQuery copySumQuery(BillListFinanceQuery q) {
		BillListFinanceQuery n = new BillListFinanceQuery();
		BeanUtil.copyProperties(q, n);
		n.setTypes(null);
		return n;
	}

}
