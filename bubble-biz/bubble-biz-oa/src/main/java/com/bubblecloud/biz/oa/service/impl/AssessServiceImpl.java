package com.bubblecloud.biz.oa.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.AssessMapper;
import com.bubblecloud.biz.oa.mapper.AssessReplyMapper;
import com.bubblecloud.biz.oa.mapper.AssessTargetMapper;
import com.bubblecloud.biz.oa.mapper.AssessUserScoreMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.AssessScoreService;
import com.bubblecloud.biz.oa.service.AssessService;
import com.bubblecloud.biz.oa.service.SystemConfigService;
import com.bubblecloud.biz.oa.util.AssessPeriodWindow;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.AssessAppealDTO;
import com.bubblecloud.oa.api.dto.hr.AssessCensusDTO;
import com.bubblecloud.oa.api.dto.hr.AssessEvalDTO;
import com.bubblecloud.oa.api.dto.hr.AssessTargetEvalDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Assess;
import com.bubblecloud.oa.api.entity.AssessReply;
import com.bubblecloud.oa.api.entity.AssessScore;
import com.bubblecloud.oa.api.entity.AssessUserScore;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.bubblecloud.oa.api.vo.hr.AssessAbnormalUserVO;
import com.bubblecloud.oa.api.vo.hr.AssessCensusVO;
import com.bubblecloud.oa.api.vo.hr.AssessExplainVO;
import com.bubblecloud.oa.api.vo.hr.AssessInfoVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 绩效考核服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
@RequiredArgsConstructor
public class AssessServiceImpl extends UpServiceImpl<AssessMapper, Assess> implements AssessService {

	private static final String KEY_SCORE_MARK = "assess_score_mark";

	private static final List<Integer> ALL_PERIODS = List.of(1, 2, 3, 4, 5);

	private final AssessUserScoreMapper assessUserScoreMapper;

	private final AssessReplyMapper assessReplyMapper;

	private final AssessTargetMapper assessTargetMapper;

	private final AssessScoreService assessScoreService;

	private final SystemConfigService systemConfigService;

	private final AdminService adminService;

	private final ObjectMapper objectMapper;

	@Override
	public AssessInfoVO getAssessInfo(Long id) {
		Assess assess = getById(id);
		assertExists(assess);
		long ent = ObjectUtil.defaultIfNull(assess.getEntid(), 1L);
		ObjectNode node = objectMapper.valueToTree(assess);
		if (ObjectUtil.isNotNull(assess.getStartTime()) && assess.getStartTime().isAfter(LocalDateTime.now())) {
			node.put("status", 5);
		}
		String levelLabel;
		if (ObjectUtil.isNotNull(assess.getStatus()) && assess.getStatus() > 2) {
			levelLabel = resolveLevelLabel(ent, assess.getAssessGrade());
		}
		else {
			levelLabel = "暂无";
			node.put("score", "未评分");
		}
		node.put("level", levelLabel);
		AssessInfoVO vo = new AssessInfoVO();
		vo.setAssessInfo(node);
		vo.setInfo(objectMapper.createObjectNode());
		vo.setExplain(systemConfigService.getConfigRawValue(KEY_SCORE_MARK));
		return vo;
	}

	@Override
	public AssessExplainVO getAssessExplain(Long id) {
		Assess assess = getById(id);
		assertExists(assess);
		Long uid = OaSecurityUtil.currentUserId();
		List<AssessReply> reply = assessReplyMapper.selectList(Wrappers.lambdaQuery(AssessReply.class)
			.eq(AssessReply::getAssessId, id)
			.eq(AssessReply::getTypes, 0)
			.eq(AssessReply::getIsOwn, 0)
			.orderByAsc(AssessReply::getId));
		List<AssessReply> appeal = assessReplyMapper.selectList(Wrappers.lambdaQuery(AssessReply.class)
			.eq(AssessReply::getAssessId, id)
			.eq(AssessReply::getTypes, 1)
			.eq(AssessReply::getStatus, 0)
			.orderByDesc(AssessReply::getId));
		List<AssessReply> mark = new ArrayList<>();
		if (ObjectUtil.isNotNull(uid) && Objects.equals(assess.getCheckUid(), uid)) {
			AssessReply m = assessReplyMapper.selectOne(Wrappers.lambdaQuery(AssessReply.class)
				.eq(AssessReply::getAssessId, id)
				.eq(AssessReply::getIsOwn, 1)
				.last("LIMIT 1"));
			if (ObjectUtil.isNotNull(m)) {
				mark.add(m);
			}
		}
		AssessExplainVO vo = new AssessExplainVO();
		vo.setReply(reply);
		vo.setAppeal(appeal);
		vo.setMark(mark);
		vo.setExplain(systemConfigService.getConfigRawValue(KEY_SCORE_MARK));
		return vo;
	}

	@Override
	public OaElFormVO buildDeleteForm(Long id, Long entid) {
		Assess assess = getById(id);
		assertExists(assess);
		Long e = ObjectUtil.defaultIfNull(entid, 1L);
		if (!Objects.equals(assess.getEntid(), e)) {
			throw new IllegalArgumentException("未找到考核记录");
		}
		ArrayNode rules = objectMapper.createArrayNode();
		ObjectNode field = objectMapper.createObjectNode();
		field.put("type", "textarea");
		field.put("field", "mark");
		field.put("title", "删除原因");
		field.put("value", "");
		field.put("placeholder", "请输入删除原因");
		ObjectNode props = objectMapper.createObjectNode();
		props.put("required", true);
		field.set("props", props);
		rules.add(field);
		return new OaElFormVO("删除绩效考核", "DELETE", "/ent/assess/delete/" + id, rules);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void selfEval(Long id, AssessEvalDTO dto) {
		Assess assess = getById(id);
		assertExists(assess);
		boolean submit = ObjectUtil.isNotNull(dto) && Integer.valueOf(1).equals(dto.getIsSubmit());
		String selfReply = dto == null ? "" : StrUtil.nullToEmpty(dto.getMark());
		assess.setSelfReply(selfReply);
		if (submit) {
			if (ObjectUtil.isNull(assess.getStatus()) || assess.getStatus() < 2) {
				assess.setStatus(2);
			}
			assess.setTestStatus(1);
		}
		else {
			assess.setTestStatus(2);
		}
		updateById(assess);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void superiorEval(Long id, AssessEvalDTO dto) {
		Assess assess = getById(id);
		assertExists(assess);
		long ent = ObjectUtil.defaultIfNull(assess.getEntid(), 1L);
		boolean submit = ObjectUtil.isNotNull(dto) && Integer.valueOf(1).equals(dto.getIsSubmit());
		if (ObjectUtil.isNotNull(dto)) {
			assess.setReply(StrUtil.nullToEmpty(dto.getReplyMark()));
			assess.setHideReply(StrUtil.nullToEmpty(dto.getHideMark()));
		}
		BigDecimal score = ObjectUtil.isNotNull(dto) && ObjectUtil.isNotNull(dto.getScore()) ? dto.getScore()
				: ObjectUtil.defaultIfNull(assess.getAssessScore(), BigDecimal.ZERO);
		Integer grade = assessScoreService.resolveGrade(ent, score);
		if (submit) {
			assess.setAssessScore(score);
			assess.setAssessGrade(grade);
			if (ObjectUtil.isNull(assess.getStatus()) || assess.getStatus() < 3) {
				assess.setStatus(3);
			}
			assess.setCheckStatus(1);
			insertScoreSnapshot(assess, ent);
		}
		else {
			assess.setCheckStatus(2);
		}
		updateById(assess);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void examineEval(Long id, AssessEvalDTO dto) {
		Assess assess = getById(id);
		assertExists(assess);
		long ent = ObjectUtil.defaultIfNull(assess.getEntid(), 1L);
		boolean submit = ObjectUtil.isNotNull(dto) && Integer.valueOf(1).equals(dto.getIsSubmit());
		BigDecimal score = ObjectUtil.isNotNull(dto) && ObjectUtil.isNotNull(dto.getScore()) ? dto.getScore()
				: ObjectUtil.defaultIfNull(assess.getAssessScore(), BigDecimal.ZERO);
		Integer grade = assessScoreService.resolveGrade(ent, score);
		if (submit) {
			assess.setAssessScore(score);
			assess.setAssessGrade(grade);
			if (ObjectUtil.isNull(assess.getStatus()) || assess.getStatus() < 4) {
				assess.setStatus(4);
			}
			assess.setVerifyStatus(1);
			insertScoreSnapshot(assess, ent);
		}
		else {
			assess.setVerifyStatus(2);
		}
		updateById(assess);
	}

	private void insertScoreSnapshot(Assess assess, long entid) {
		Long op = OaSecurityUtil.currentUserId();
		AssessUserScore row = new AssessUserScore();
		row.setEntid(entid);
		row.setAssessid(assess.getId().intValue());
		row.setUserid(ObjectUtil.isNull(op) ? 0 : op.intValue());
		row.setCheckUid(toIntUid(assess.getCheckUid()));
		row.setTestUid(toIntUid(assess.getTestUid()));
		row.setScore(ObjectUtil.defaultIfNull(assess.getAssessScore(), BigDecimal.ZERO));
		row.setTotal(ObjectUtil.defaultIfNull(assess.getAssessTotal(), BigDecimal.ZERO));
		row.setGrade(ObjectUtil.defaultIfNull(assess.getAssessGrade(), 0));
		row.setInfo("{}");
		row.setMark("");
		row.setTypes(0);
		row.setCreatedAt(LocalDateTime.now());
		row.setUpdatedAt(LocalDateTime.now());
		assessUserScoreMapper.insert(row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void enableAssess(Long id, Integer status) {
		int s = ObjectUtil.defaultIfNull(status, 1);
		if (s != 0 && s != 1) {
			throw new IllegalArgumentException("无效的状态！");
		}
		Assess assess = getById(id);
		assertExists(assess);
		if (ObjectUtil.isNotNull(assess.getStatus()) && assess.getStatus() > 1) {
			throw new IllegalArgumentException("考核已完成自评，无法修改状态！");
		}
		if (s == 1 && !Integer.valueOf(1).equals(assess.getIntact())) {
			throw new IllegalArgumentException("考核已启失败，请按规范完善内容！");
		}
		assess.setIsShow(s);
		assess.setMakeStatus(s);
		assess.setStatus(s);
		updateById(assess);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void evalTarget(AssessTargetEvalDTO dto) {
		if (ObjectUtil.isNull(dto) || ObjectUtil.isNull(dto.getAssessId()) || ObjectUtil.isNull(dto.getTargetId())
				|| ObjectUtil.isNull(dto.getSpaceId())) {
			throw new IllegalArgumentException("缺少必要参数");
		}
		Assess assess = getById(dto.getAssessId());
		assertExists(assess);
		Long uid = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(uid) || !Objects.equals(assess.getTestUid(), uid)) {
			throw new IllegalArgumentException("您暂无权限修改该考核信息");
		}
		long ent = ObjectUtil.defaultIfNull(assess.getEntid(), 1L);
		int n = baseMapper.countAssessSpace(ent, dto.getAssessId(), dto.getSpaceId());
		if (n <= 0) {
			throw new IllegalArgumentException("未找到相关考核信息");
		}
		String fi = StrUtil.nullToEmpty(dto.getFinishInfo());
		int fr = dto.getFinishRatio() == null ? 0 : dto.getFinishRatio().intValue();
		int u = assessTargetMapper.updateFinishBySpace(dto.getTargetId(), dto.getSpaceId(), fi, fr);
		if (u <= 0) {
			throw new IllegalArgumentException("无效的考核信息");
		}
	}

	@Override
	public List<Object> scoreRecord(Long assessId) {
		List<AssessUserScore> rows = assessUserScoreMapper.selectList(Wrappers.lambdaQuery(AssessUserScore.class)
			.eq(AssessUserScore::getAssessid, assessId.intValue())
			.eq(AssessUserScore::getTypes, 0)
			.orderByAsc(AssessUserScore::getId));
		return new ArrayList<>(rows);
	}

	@Override
	public List<Object> deleteRecord(Long entid) {
		List<AssessUserScore> rows = assessUserScoreMapper.selectList(Wrappers.lambdaQuery(AssessUserScore.class)
			.eq(ObjectUtil.isNotNull(entid), AssessUserScore::getEntid, entid)
			.eq(AssessUserScore::getTypes, 1)
			.orderByDesc(AssessUserScore::getId));
		return new ArrayList<>(rows);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteAssess(Long id, String mark) {
		if (StrUtil.isBlank(mark)) {
			throw new IllegalArgumentException("删除失败，您填写删除原因！");
		}
		Assess assess = getById(id);
		assertExists(assess);
		Long op = OaSecurityUtil.currentUserId();
		AssessUserScore row = new AssessUserScore();
		row.setEntid(assess.getEntid());
		row.setAssessid(id.intValue());
		row.setUserid(ObjectUtil.isNull(op) ? 0 : op.intValue());
		row.setCheckUid(toIntUid(assess.getCheckUid()));
		row.setTestUid(toIntUid(assess.getTestUid()));
		row.setScore(ObjectUtil.defaultIfNull(assess.getAssessScore(), BigDecimal.ZERO));
		row.setTotal(ObjectUtil.defaultIfNull(assess.getAssessTotal(), BigDecimal.ZERO));
		row.setGrade(ObjectUtil.defaultIfNull(assess.getAssessGrade(), 0));
		row.setInfo("{}");
		row.setMark(mark);
		row.setTypes(1);
		row.setCreatedAt(LocalDateTime.now());
		row.setUpdatedAt(LocalDateTime.now());
		assessUserScoreMapper.insert(row);
		removeById(id);
	}

	private static int toIntUid(Long v) {
		return ObjectUtil.isNull(v) ? 0 : v.intValue();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void appealOrReject(Long id, AssessAppealDTO dto) {
		if (ObjectUtil.isNull(dto) || StrUtil.isBlank(dto.getContent())) {
			throw new IllegalArgumentException("请填写内容");
		}
		Assess assess = getById(id);
		assertExists(assess);
		Long ent = ObjectUtil.defaultIfNull(assess.getEntid(), 1L);
		Long op = OaSecurityUtil.currentUserId();
		boolean reject = ObjectUtil.isNotNull(dto.getTypes()) && dto.getTypes() != 0;
		if (reject) {
			assessReplyMapper.update(null,
					Wrappers.lambdaUpdate(AssessReply.class)
						.eq(AssessReply::getAssessId, id)
						.eq(AssessReply::getUserId, assess.getTestUid())
						.eq(AssessReply::getTypes, 1)
						.set(AssessReply::getStatus, 1));
			AssessReply created = new AssessReply();
			created.setAssessId(id);
			created.setEntid(ent);
			created.setUserId(ObjectUtil.defaultIfNull(op, 0L));
			created.setContent(dto.getContent());
			created.setTypes(1);
			created.setStatus(2);
			created.setIsOwn(0);
			created.setCreatedAt(LocalDateTime.now());
			created.setUpdatedAt(LocalDateTime.now());
			assessReplyMapper.insert(created);
		}
		else {
			if (ObjectUtil.isNull(op) || !Objects.equals(assess.getTestUid(), op)) {
				throw new IllegalArgumentException("您暂无权限操作该考核记录");
			}
			if (!Integer.valueOf(1).equals(assess.getCheckStatus())) {
				throw new IllegalArgumentException("申诉失败，上级未评价打分");
			}
			if (Integer.valueOf(4).equals(assess.getStatus())) {
				throw new IllegalArgumentException("申诉失败，考核已结束");
			}
			AssessReply created = new AssessReply();
			created.setAssessId(id);
			created.setEntid(ent);
			created.setUserId(assess.getTestUid());
			created.setContent(dto.getContent());
			created.setTypes(1);
			created.setStatus(0);
			created.setIsOwn(0);
			created.setCreatedAt(LocalDateTime.now());
			created.setUpdatedAt(LocalDateTime.now());
			assessReplyMapper.insert(created);
		}
	}

	@Override
	public AssessCensusVO census(AssessCensusDTO dto) {
		AssessCensusVO vo = new AssessCensusVO();
		vo.setSeries(objectMapper.createArrayNode());
		vo.setXAxis(Collections.emptyList());
		vo.setLevelStats(Collections.emptyList());
		vo.setTotal(0);
		return vo;
	}

	@Override
	public AssessCensusVO censusBar(AssessCensusDTO dto) {
		long entid = ObjectUtil.defaultIfNull(dto == null ? null : dto.getEntid(), 1L);
		List<AssessScore> bands = assessScoreService.listByEntidOrderByLevel(entid);
		ArrayNode arr = objectMapper.createArrayNode();
		int sum = 0;
		for (AssessScore b : bands) {
			long c = count(Wrappers.lambdaQuery(Assess.class)
				.eq(Assess::getEntid, entid)
				.eq(Assess::getAssessGrade, b.getLevel()));
			sum += c;
			ObjectNode o = objectMapper.createObjectNode();
			o.put("min", ObjectUtil.defaultIfNull(b.getMin(), 0));
			o.put("max", ObjectUtil.defaultIfNull(b.getMax(), 0));
			o.put("name", StrUtil.nullToEmpty(b.getName()));
			o.put("level", ObjectUtil.defaultIfNull(b.getLevel(), 0));
			o.put("count", c);
			arr.add(o);
		}
		AssessCensusVO vo = new AssessCensusVO();
		vo.setScore(arr);
		vo.setFrame(objectMapper.createArrayNode());
		vo.setCount(sum);
		return vo;
	}

	@Override
	public List<AssessAbnormalUserVO> abnormalList(Integer period, String time, Long entid) {
		if (ObjectUtil.isNull(period) || ObjectUtil.isNull(entid)) {
			return Collections.emptyList();
		}
		LocalDateTime anchor = parseAnchorTime(time);
		AssessPeriodWindow w = AssessPeriodWindow.of(period, anchor);
		List<Long> ids = baseMapper.findAbnormalTestUids(entid, period, w.getStart(), w.getEnd());
		if (CollUtil.isEmpty(ids)) {
			return Collections.emptyList();
		}
		List<AssessAbnormalUserVO> out = new ArrayList<>();
		for (Long uid : ids) {
			Admin adm = adminService.getById(uid);
			if (ObjectUtil.isNull(adm) || ObjectUtil.isNotNull(adm.getDeletedAt())) {
				continue;
			}
			AssessAbnormalUserVO v = new AssessAbnormalUserVO();
			v.setId(adm.getId());
			v.setName(adm.getName());
			v.setAvatar(adm.getAvatar());
			v.setJob(adm.getJob());
			v.setTitle(w.getTitle());
			out.add(v);
		}
		return out;
	}

	@Override
	public int abnormalCount(Integer period, String time, Long entid) {
		if (ObjectUtil.isNull(entid)) {
			return 0;
		}
		LocalDateTime anchor = parseAnchorTime(time);
		List<Integer> loop = ObjectUtil.isNull(period) ? ALL_PERIODS : List.of(period);
		int total = 0;
		for (Integer p : loop) {
			AssessPeriodWindow w = AssessPeriodWindow.of(p, anchor);
			List<Long> ids = baseMapper.findAbnormalTestUids(entid, p, w.getStart(), w.getEnd());
			if (CollUtil.isNotEmpty(ids)) {
				total += ids.size();
			}
		}
		return total;
	}

	private static LocalDateTime parseAnchorTime(String time) {
		if (StrUtil.isBlank(time)) {
			return LocalDateTime.now();
		}
		try {
			return DateUtil.parseLocalDateTime(time);
		}
		catch (Exception e) {
			return LocalDateTime.now();
		}
	}

	private String resolveLevelLabel(long entid, Integer grade) {
		if (ObjectUtil.isNull(grade) || grade == 0) {
			return "无";
		}
		List<AssessScore> bands = assessScoreService.listByEntidOrderByLevel(entid);
		for (AssessScore b : bands) {
			if (Objects.equals(b.getLevel(), grade)) {
				return StrUtil.nullToEmpty(b.getName());
			}
		}
		return "无";
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Assess req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Assess req) {
		return super.update(req);
	}

	private void assertExists(Assess entity) {
		if (ObjectUtil.isNull(entity) || ObjectUtil.isNotNull(entity.getDeletedAt())) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
	}

}
