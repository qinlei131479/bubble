package com.bubblecloud.biz.oa.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.BillCategoryMapper;
import com.bubblecloud.biz.oa.mapper.BillListMapper;
import com.bubblecloud.biz.oa.mapper.ClientBillLogMapper;
import com.bubblecloud.biz.oa.mapper.EnterprisePaytypeMapper;
import com.bubblecloud.biz.oa.service.BillCategoryService;
import com.bubblecloud.biz.oa.service.BillListFinanceService;
import com.bubblecloud.biz.oa.util.FinanceRatioUtil;
import com.bubblecloud.oa.api.dto.finance.BillListFinanceQuery;
import com.bubblecloud.oa.api.dto.finance.BillListSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.BillCategory;
import com.bubblecloud.oa.api.entity.BillList;
import com.bubblecloud.oa.api.entity.ClientBillLog;
import com.bubblecloud.oa.api.entity.EnterprisePaytype;
import com.bubblecloud.oa.api.vo.finance.BillListFinancePageVO;
import com.bubblecloud.oa.api.vo.finance.BillTrendRowVO;
import com.bubblecloud.oa.api.vo.finance.FinanceBillRankRowVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
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

	private static final Pattern CHART_RANGE = Pattern
		.compile("(\\d{4}[/\\-]\\d{1,2}[/\\-]\\d{1,2})\\s*-\\s*(\\d{4}[/\\-]\\d{1,2}[/\\-]\\d{1,2})");

	private static final List<DateTimeFormatter> DATE_FORMATS = List.of(DateTimeFormatter.ofPattern("yyyy/M/d"),
			DateTimeFormatter.ofPattern("yyyy/MM/dd"), DateTimeFormatter.ISO_LOCAL_DATE);

	private final BillListMapper billListMapper;

	private final BillCategoryMapper billCategoryMapper;

	private final BillCategoryService billCategoryService;

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
	public ObjectNode chart(JsonNode body) {
		ObjectNode b = objectMapper.createObjectNode();
		b.set("type", body.path("type"));
		b.set("time", body.path("time"));
		b.set("cate_id", body.path("cate_id"));
		b.put("entid", body.path("entid").asLong(1L));
		b.put("income", "1");
		b.put("expend", "1");
		return buildTrend(b, true);
	}

	@Override
	public ObjectNode chartPart(JsonNode body) {
		return buildTrend(body, false);
	}

	@Override
	public List<FinanceBillRankRowVO> rankAnalysis(JsonNode body) {
		String time = body.path("time").asText("");
		int rootCateId = body.path("cate_id").asInt(0);
		int types = body.path("types").asInt(1) > 0 ? 1 : 0;
		List<Integer> cateIdsFilter = flattenCateIds(body.get("cate_ids"));
		long entid = body.path("entid").asLong(1L);
		return rankAnalysisCore(time, rootCateId, types, cateIdsFilter, entid);
	}

	@Override
	public List<ClientBillLog> listLogs(Long entid, Long billListId) {
		return clientBillLogMapper.selectList(Wrappers.lambdaQuery(ClientBillLog.class)
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
		long entid = 1L;
		List<EnterprisePaytype> payList = enterprisePaytypeMapper
			.selectList(Wrappers.lambdaQuery(EnterprisePaytype.class)
				.eq(EnterprisePaytype::getEntid, (int) entid)
				.eq(EnterprisePaytype::getStatus, 1));
		var payNameToId = payList.stream()
			.collect(Collectors.toMap(EnterprisePaytype::getName, EnterprisePaytype::getId, (a, b) -> a));
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
			dto.setEntid(entid);
			String payLabel = row.path("pay_type").asText("");
			if (StrUtil.isNotBlank(payLabel)) {
				Long tid = payNameToId.get(payLabel);
				if (tid != null) {
					dto.setTypeId(tid.intValue());
				}
			}
			JsonNode cateNode = row.get("cate_id");
			if (cateNode != null && cateNode.isNumber()) {
				dto.setCateId(cateNode.asInt());
			}
			else {
				String cateName = cateNode != null ? cateNode.asText("") : "";
				if (StrUtil.isNotBlank(cateName)) {
					dto.setCateId(resolveOrCreateCateByName(types, cateName.trim(), entid));
				}
			}
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
		e.setPayType(StrUtil.nullToEmpty(payName));
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

	@Override
	public OaElFormVO buildBillCreateForm(long entid) {
		ArrayNode rules = objectMapper.createArrayNode();
		ObjectNode typesField = objectMapper.createObjectNode();
		typesField.put("type", "radio");
		typesField.put("field", "types");
		typesField.put("title", "账目类型");
		typesField.put("value", 0);
		ArrayNode topts = objectMapper.createArrayNode();
		topts.addObject().put("value", 1).put("label", "收入");
		topts.addObject().put("value", 0).put("label", "支出");
		typesField.set("options", topts);
		ArrayNode tcontrol = objectMapper.createArrayNode();
		tcontrol.add(billTypeControlBranch(0, entid, billCategoryService.buildBillCascaderOptions(0, 0L, entid), 0));
		tcontrol.add(billTypeControlBranch(1, entid, billCategoryService.buildBillCascaderOptions(1, 0L, entid), 0));
		typesField.set("control", tcontrol);
		rules.add(typesField);
		rules.add(billNumberField("num", "账目金额", BigDecimal.ZERO));
		rules.add(billPayTypeSelect(entid, 0, ""));
		rules.add(billUploadField());
		rules.add(billDateTimeField("edit_time", "日期", LocalDateTime.now().format(DT)));
		ObjectNode mark = objectMapper.createObjectNode();
		mark.put("type", "textarea");
		mark.put("field", "mark");
		mark.put("title", "备注信息");
		mark.put("value", "");
		mark.put("placeholder", "请输入备注信息");
		rules.add(mark);
		return new OaElFormVO("添加", "POST", "/ent/bill", rules);
	}

	@Override
	public OaElFormVO buildBillEditForm(long id, long entid) {
		BillList bill = billListMapper.selectById(id);
		if (ObjectUtil.isNull(bill) || bill.getEntid() == null || bill.getEntid().longValue() != entid) {
			throw new IllegalArgumentException("数据不存在");
		}
		boolean typesDisabled = bill.getLinkId() != null && bill.getLinkId() > 0 && "client".equals(bill.getLinkCate());
		ArrayNode rules = objectMapper.createArrayNode();
		ObjectNode typesField = objectMapper.createObjectNode();
		typesField.put("type", "radio");
		typesField.put("field", "types");
		typesField.put("title", "账目类型");
		typesField.put("value", bill.getTypes() == null ? 0 : bill.getTypes());
		typesField.put("disabled", typesDisabled);
		ArrayNode topts = objectMapper.createArrayNode();
		topts.addObject().put("value", 1).put("label", "收入");
		topts.addObject().put("value", 0).put("label", "支出");
		typesField.set("options", topts);
		ArrayNode tcontrol = objectMapper.createArrayNode();
		int t0 = bill.getTypes() == null ? 0 : bill.getTypes();
		tcontrol.add(billTypeControlBranch(0, entid, billCategoryService.buildBillCascaderOptions(0, 0L, entid),
				t0 == 0 ? ObjectUtil.defaultIfNull(bill.getCateId(), 0) : 0));
		tcontrol.add(billTypeControlBranch(1, entid, billCategoryService.buildBillCascaderOptions(1, 0L, entid),
				t0 == 1 ? ObjectUtil.defaultIfNull(bill.getCateId(), 0) : 0));
		typesField.set("control", tcontrol);
		rules.add(typesField);
		rules.add(billNumberField("num", "账目金额", bill.getNum() == null ? BigDecimal.ZERO : bill.getNum()));
		rules.add(billPayTypeSelect(entid, ObjectUtil.defaultIfNull(bill.getTypeId(), 0), bill.getPayType()));
		rules.add(billUploadField());
		String et = bill.getEditTime() == null ? LocalDateTime.now().format(DT) : bill.getEditTime().format(DT);
		rules.add(billDateTimeField("edit_time", "日期", et));
		ObjectNode mark = objectMapper.createObjectNode();
		mark.put("type", "textarea");
		mark.put("field", "mark");
		mark.put("title", "备注信息");
		mark.put("value", StrUtil.nullToEmpty(bill.getMark()));
		mark.put("placeholder", "请输入备注信息");
		rules.add(mark);
		return new OaElFormVO("编辑", "PUT", "/ent/bill/" + id, rules);
	}

	private ObjectNode billTypeControlBranch(int typeVal, long entid, ArrayNode options, int cateValue) {
		ObjectNode branch = objectMapper.createObjectNode();
		branch.put("value", typeVal);
		ArrayNode innerRules = objectMapper.createArrayNode();
		ObjectNode cascader = objectMapper.createObjectNode();
		cascader.put("type", "cascader");
		cascader.put("field", "cate_id");
		cascader.put("title", "账目分类");
		cascader.put("value", cateValue);
		cascader.set("options", options);
		ObjectNode props = objectMapper.createObjectNode();
		ObjectNode inner = objectMapper.createObjectNode();
		inner.put("checkStrictly", true);
		inner.put("emitPath", false);
		props.set("props", inner);
		cascader.set("props", props);
		innerRules.add(cascader);
		branch.set("rule", innerRules);
		return branch;
	}

	private ObjectNode billNumberField(String field, String title, BigDecimal value) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "inputNumber");
		n.put("field", field);
		n.put("title", title);
		n.put("value", value);
		ObjectNode p = objectMapper.createObjectNode();
		p.put("min", 0.01);
		p.put("precision", 2);
		p.put("required", true);
		n.set("props", p);
		return n;
	}

	private ObjectNode billPayTypeSelect(long entid, int typeId, String payTypeFallback) {
		List<EnterprisePaytype> list = enterprisePaytypeMapper.selectList(Wrappers.lambdaQuery(EnterprisePaytype.class)
			.eq(EnterprisePaytype::getEntid, (int) entid)
			.eq(EnterprisePaytype::getStatus, 1)
			.orderByAsc(EnterprisePaytype::getSort)
			.orderByAsc(EnterprisePaytype::getId));
		ArrayNode opts = objectMapper.createArrayNode();
		for (EnterprisePaytype p : list) {
			opts.addObject().put("value", p.getId().intValue()).put("label", p.getName());
		}
		if (typeId > 0 && list.stream().noneMatch(x -> x.getId().intValue() == typeId)) {
			opts.addObject().put("value", typeId).put("label", StrUtil.nullToEmpty(payTypeFallback));
		}
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "select");
		n.put("field", "type_id");
		n.put("title", "支付方式");
		if (typeId > 0) {
			n.put("value", typeId);
		}
		else {
			n.put("value", "");
		}
		n.set("options", opts);
		ObjectNode p = objectMapper.createObjectNode();
		p.put("required", true);
		n.set("props", p);
		return n;
	}

	private ObjectNode billUploadField() {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "upload");
		n.put("field", "file_id");
		n.put("title", "支付凭证");
		n.put("value", "");
		ObjectNode p = objectMapper.createObjectNode();
		p.put("action", "/api/ent/common/upload");
		p.put("limit", 1);
		n.set("props", p);
		return n;
	}

	private ObjectNode billDateTimeField(String field, String title, String value) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("type", "datePicker");
		n.put("field", field);
		n.put("title", title);
		n.put("value", value);
		ObjectNode p = objectMapper.createObjectNode();
		p.put("type", "datetime");
		p.put("required", true);
		n.set("props", p);
		return n;
	}

	private ObjectNode buildTrend(JsonNode body, boolean all) {
		String type = body.path("type").asText("");
		String time = body.path("time").asText("");
		List<Integer> cateSearch = flattenCateIds(body.get("cate_id"));
		String incomeStr = body.path("income").asText("");
		String expendStr = body.path("expend").asText("");
		long entid = body.path("entid").asLong(1L);
		LocalDate d0;
		LocalDate d1;
		try {
			LocalDate[] dr = parseChartDates(time);
			d0 = dr[0];
			d1 = dr[1];
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("参数错误");
		}
		LocalDateTime start;
		LocalDateTime end;
		if (d0.equals(d1)) {
			start = d0.atStartOfDay();
			end = d0.atTime(23, 59, 59);
		}
		else {
			start = d0.atStartOfDay();
			end = d1.atTime(23, 59, 59);
		}
		long dayCount = ChronoUnit.DAYS.between(d0, d1) + 1;
		int step;
		String sqlPattern;
		if (dayCount == 1) {
			step = 0;
			sqlPattern = "%H";
		}
		else if (dayCount <= 31) {
			step = 1;
			sqlPattern = "%m-%d";
		}
		else if (dayCount <= 92) {
			step = 3;
			sqlPattern = "%m-%d";
		}
		else {
			step = 30;
			sqlPattern = "%Y-%m";
		}
		ObjectNode root = objectMapper.createObjectNode();
		List<Integer> pivotPids = distinctParentIds(cateSearch, entid);
		if (all) {
			ArrayNode xAxis = buildXAxis(d0, d1, step);
			root.set("xAxis", xAxis);
			java.util.Map<String, BigDecimal> incomeMap = (StrUtil.isBlank(type) || "1".equals(type))
					? trendMap(entid, 1, start, end, cateSearch, sqlPattern) : Collections.emptyMap();
			java.util.Map<String, BigDecimal> expendMap = (StrUtil.isBlank(type) || "0".equals(type))
					? trendMap(entid, 0, start, end, cateSearch, sqlPattern) : Collections.emptyMap();
			ArrayNode series = objectMapper.createArrayNode();
			ArrayNode incData = objectMapper.createArrayNode();
			ArrayNode expData = objectMapper.createArrayNode();
			for (int i = 0; i < xAxis.size(); i++) {
				String key = xAxis.get(i).asText();
				incData.add(incomeMap.getOrDefault(key, BigDecimal.ZERO).doubleValue());
				expData.add(expendMap.getOrDefault(key, BigDecimal.ZERO).doubleValue());
			}
			ObjectNode s1 = objectMapper.createObjectNode();
			s1.put("name", "收入金额");
			s1.set("data", incData);
			s1.put("type", "bar");
			s1.put("yAxisIndex", 1);
			series.add(s1);
			ObjectNode s2 = objectMapper.createObjectNode();
			s2.put("name", "支出金额");
			s2.set("data", expData);
			s2.put("type", "bar");
			s2.put("yAxisIndex", 1);
			series.add(s2);
			root.set("series", series);
		}
		int incLv = parseLevel(incomeStr);
		int expLv = parseLevel(expendStr);
		ArrayNode incomeRank = objectMapper.createArrayNode();
		ArrayNode expendRank = objectMapper.createArrayNode();
		if (incLv >= 0 && (StrUtil.isBlank(type) || "1".equals(type))) {
			for (FinanceBillRankRowVO r : buildRankForChart(1, start, end, entid, cateSearch, pivotPids, incLv)) {
				incomeRank.add(objectMapper.valueToTree(r));
			}
		}
		if (expLv >= 0 && (StrUtil.isBlank(type) || "0".equals(type))) {
			for (FinanceBillRankRowVO r : buildRankForChart(0, start, end, entid, cateSearch, pivotPids, expLv)) {
				expendRank.add(objectMapper.valueToTree(r));
			}
		}
		root.set("incomeRank", incomeRank);
		root.set("expendRank", expendRank);
		return root;
	}

	private int parseLevel(String s) {
		if (StrUtil.isBlank(s)) {
			return -1;
		}
		try {
			return Integer.parseInt(s.trim());
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	private java.util.Map<String, BigDecimal> trendMap(long entid, int types, LocalDateTime start, LocalDateTime end,
			List<Integer> cateIds, String pattern) {
		List<BillTrendRowVO> rows = billListMapper.selectTrendAgg(entid, types, start, end, cateIds, pattern);
		java.util.Map<String, BigDecimal> m = new java.util.HashMap<>();
		if (rows != null) {
			for (BillTrendRowVO r : rows) {
				if (r.getDays() != null) {
					m.put(r.getDays(), r.getNum() == null ? BigDecimal.ZERO : r.getNum());
				}
			}
		}
		return m;
	}

	private ArrayNode buildXAxis(LocalDate d0, LocalDate d1, int step) {
		ArrayNode ax = objectMapper.createArrayNode();
		if (step == 0) {
			for (int h = 0; h < 24; h++) {
				ax.add(String.format("%02d", h));
			}
			return ax;
		}
		LocalDate cur = d0;
		if (step == 30) {
			while (!cur.isAfter(d1)) {
				ax.add(cur.format(DateTimeFormatter.ofPattern("yyyy-MM")));
				cur = cur.plusMonths(1);
			}
		}
		else {
			while (!cur.isAfter(d1)) {
				ax.add(cur.format(DateTimeFormatter.ofPattern("MM-dd")));
				cur = cur.plusDays(step == 0 ? 1 : step);
			}
		}
		return ax;
	}

	private List<FinanceBillRankRowVO> buildRankForChart(int types, LocalDateTime start, LocalDateTime end, long entid,
			List<Integer> cateSearch, List<Integer> pivotPids, int levelFilter) {
		List<BillCategory> cats = searchCateMulti(pivotPids, types, entid);
		if (CollUtil.isEmpty(cateSearch) && levelFilter > 0) {
			cats = cats.stream().filter(c -> ObjectUtil.equal(c.getLevel(), levelFilter)).collect(Collectors.toList());
		}
		List<Integer> allIds = billCategoryService.subtreeInclusiveIds(0, entid, types);
		BigDecimal denom = ObjectUtil.defaultIfNull(billListMapper.sumNumInRange(entid, types, start, end, allIds),
				BigDecimal.ZERO);
		List<FinanceBillRankRowVO> rows = new ArrayList<>();
		for (BillCategory v : cats) {
			if (CollUtil.isNotEmpty(cateSearch) && !cateSearch.contains(v.getId().intValue())) {
				continue;
			}
			List<Integer> billCateIds = rankBillCateIds(v, cateSearch, types, entid);
			if (CollUtil.isEmpty(billCateIds)) {
				continue;
			}
			BigDecimal sum = ObjectUtil
				.defaultIfNull(billListMapper.sumNumSubtree(entid, types, start, end, billCateIds), BigDecimal.ZERO);
			FinanceBillRankRowVO row = new FinanceBillRankRowVO();
			row.setCateId(v.getId().intValue());
			row.setName(v.getName());
			row.setSum(sum);
			rows.add(row);
		}
		return FinanceRatioUtil.calcRatio(rows, denom);
	}

	private List<Integer> rankBillCateIds(BillCategory v, List<Integer> cateSearch, int types, long entid) {
		List<Integer> sub = billCategoryService.subtreeInclusiveIds(v.getId().intValue(), entid, types);
		if (CollUtil.isNotEmpty(cateSearch)) {
			return sub.stream().filter(cateSearch::contains).collect(Collectors.toList());
		}
		return sub;
	}

	private List<BillCategory> searchCateMulti(List<Integer> pivotPids, int types, long entid) {
		if (CollUtil.isEmpty(pivotPids)) {
			return billCategoryService.searchCate(0, types, entid);
		}
		List<BillCategory> byPid = billCategoryMapper.selectList(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getEntid, entid)
			.eq(BillCategory::getTypes, types)
			.in(BillCategory::getPid, pivotPids)
			.orderByAsc(BillCategory::getSort)
			.orderByAsc(BillCategory::getId));
		if (CollUtil.isNotEmpty(byPid)) {
			return byPid;
		}
		return billCategoryMapper.selectList(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getEntid, entid)
			.eq(BillCategory::getTypes, types)
			.in(BillCategory::getId, pivotPids)
			.orderByAsc(BillCategory::getSort)
			.orderByAsc(BillCategory::getId));
	}

	private List<Integer> distinctParentIds(List<Integer> cateIds, long entid) {
		if (CollUtil.isEmpty(cateIds)) {
			return List.of();
		}
		LinkedHashSet<Integer> pids = new LinkedHashSet<>();
		for (Integer id : cateIds) {
			BillCategory c = billCategoryMapper.selectById(id.longValue());
			if (c != null && c.getPid() != null) {
				pids.add(c.getPid());
			}
		}
		return new ArrayList<>(pids);
	}

	private List<FinanceBillRankRowVO> rankAnalysisCore(String time, int rootCateId, int types,
			List<Integer> cateIdsFilter, long entid) {
		LocalDate[] dr = parseChartDates(time);
		LocalDate d0 = dr[0];
		LocalDate d1 = dr[1];
		LocalDateTime start = d0.atStartOfDay();
		LocalDateTime end = d1.equals(d0) ? d0.atTime(23, 59, 59) : d1.atTime(23, 59, 59);
		List<Integer> denomIds = billCategoryService.subtreeInclusiveIds(rootCateId, entid, types);
		BigDecimal denom = ObjectUtil.defaultIfNull(billListMapper.sumNumInRange(entid, types, start, end, denomIds),
				BigDecimal.ZERO);
		List<BillCategory> cats = billCategoryService.searchCate(rootCateId, types, entid);
		List<FinanceBillRankRowVO> rows = new ArrayList<>();
		for (BillCategory v : cats) {
			if (CollUtil.isNotEmpty(cateIdsFilter) && !cateIdsFilter.contains(v.getId().intValue())) {
				continue;
			}
			List<Integer> billCateIds;
			if (rootCateId > 0 && rootCateId == v.getId().intValue()) {
				billCateIds = List.of(rootCateId);
			}
			else {
				List<Integer> sub = billCategoryService.subtreeInclusiveIds(v.getId().intValue(), entid, types);
				if (CollUtil.isNotEmpty(cateIdsFilter)) {
					billCateIds = sub.stream().filter(cateIdsFilter::contains).collect(Collectors.toList());
				}
				else {
					billCateIds = sub;
				}
			}
			if (CollUtil.isEmpty(billCateIds)) {
				continue;
			}
			BigDecimal sum = ObjectUtil
				.defaultIfNull(billListMapper.sumNumSubtree(entid, types, start, end, billCateIds), BigDecimal.ZERO);
			FinanceBillRankRowVO row = new FinanceBillRankRowVO();
			row.setCateId(v.getId().intValue());
			row.setName(v.getName());
			row.setSum(sum);
			rows.add(row);
		}
		return FinanceRatioUtil.calcRatio(rows, denom);
	}

	private LocalDate[] parseChartDates(String time) {
		if (StrUtil.isBlank(time)) {
			throw new IllegalArgumentException("参数错误");
		}
		Matcher m = CHART_RANGE.matcher(time.trim());
		if (!m.find()) {
			throw new IllegalArgumentException("参数错误");
		}
		return new LocalDate[] { parseDateFlexible(m.group(1)), parseDateFlexible(m.group(2)) };
	}

	private LocalDate parseDateFlexible(String raw) {
		String s = raw.trim();
		for (DateTimeFormatter f : DATE_FORMATS) {
			try {
				return LocalDate.parse(s, f);
			}
			catch (DateTimeParseException ignored) {
			}
		}
		throw new IllegalArgumentException("参数错误");
	}

	private int resolveOrCreateCateByName(int types, String name, long entid) {
		long cnt = billCategoryMapper.selectCount(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getEntid, entid)
			.eq(BillCategory::getTypes, types)
			.eq(BillCategory::getName, name));
		if (cnt > 0) {
			BillCategory one = billCategoryMapper.selectOne(Wrappers.lambdaQuery(BillCategory.class)
				.eq(BillCategory::getEntid, entid)
				.eq(BillCategory::getTypes, types)
				.eq(BillCategory::getName, name)
				.last("LIMIT 1"));
			return one.getId().intValue();
		}
		BillCategory e = new BillCategory();
		e.setName(name);
		e.setTypes(types);
		e.setEntid(entid);
		e.setSort(0);
		e.setPath("");
		e.setPid(0);
		billCategoryService.create(e);
		if (e.getId() != null) {
			return e.getId().intValue();
		}
		BillCategory saved = billCategoryMapper.selectOne(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getEntid, entid)
			.eq(BillCategory::getTypes, types)
			.eq(BillCategory::getName, name)
			.orderByDesc(BillCategory::getId)
			.last("LIMIT 1"));
		if (ObjectUtil.isNull(saved)) {
			throw new IllegalArgumentException("分类创建失败");
		}
		return saved.getId().intValue();
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
