package com.bubblecloud.biz.oa.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.AssessAppealMapper;
import com.bubblecloud.biz.oa.mapper.AssessMapper;
import com.bubblecloud.biz.oa.mapper.AssessUserScoreMapper;
import com.bubblecloud.biz.oa.service.AssessService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.AssessAppealDTO;
import com.bubblecloud.oa.api.dto.hr.AssessCensusDTO;
import com.bubblecloud.oa.api.dto.hr.AssessEvalDTO;
import com.bubblecloud.oa.api.dto.hr.AssessTargetEvalDTO;
import com.bubblecloud.oa.api.entity.Assess;
import com.bubblecloud.oa.api.entity.AssessAppeal;
import com.bubblecloud.oa.api.entity.AssessUserScore;
import com.bubblecloud.oa.api.vo.hr.AssessCensusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 绩效考核服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
@RequiredArgsConstructor
public class AssessServiceImpl extends UpServiceImpl<AssessMapper, Assess> implements AssessService {

	private final AssessAppealMapper assessAppealMapper;

	private final AssessUserScoreMapper assessUserScoreMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void selfEval(Long id, AssessEvalDTO dto) {
		Assess assess = getById(id);
		assertExists(assess);
		assess.setSelfContent(dto.getContent());
		assess.setSelfScore(ObjectUtil.defaultIfNull(dto.getScore(), BigDecimal.ZERO));
		assess.setStatus(1);
		updateById(assess);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void superiorEval(Long id, AssessEvalDTO dto) {
		Assess assess = getById(id);
		assertExists(assess);
		assess.setSuperiorScore(ObjectUtil.defaultIfNull(dto.getScore(), BigDecimal.ZERO));
		assess.setStatus(2);
		updateById(assess);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void examineEval(Long id, AssessEvalDTO dto) {
		Assess assess = getById(id);
		assertExists(assess);
		assess.setExamineScore(ObjectUtil.defaultIfNull(dto.getScore(), BigDecimal.ZERO));
		assess.setFinalScore(ObjectUtil.defaultIfNull(dto.getScore(), BigDecimal.ZERO));
		assess.setStatus(3);
		updateById(assess);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void enableAssess(Long id, Integer status) {
		int s = ObjectUtil.defaultIfNull(status, 1);
		if (s != 0 && s != 1) {
			throw new IllegalArgumentException("无效的状态");
		}
		Assess assess = getById(id);
		assertExists(assess);
		assess.setIsShow(s);
		assess.setMakeStatus(s);
		assess.setStatus(s);
		updateById(assess);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void evalTarget(AssessTargetEvalDTO dto) {
		// 指标自评：更新自评分到 Assess 聚合分，具体多指标需引入明细表，此处先更新总自评分
		if (ObjectUtil.isNull(dto) || ObjectUtil.isNull(dto.getAssessId())) {
			throw new IllegalArgumentException("缺少必要参数");
		}
		Assess assess = getById(dto.getAssessId());
		assertExists(assess);
		if (ObjectUtil.isNotNull(dto.getScore())) {
			assess.setSelfScore(dto.getScore());
			updateById(assess);
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
		row.setCheckUid(toIntUid(assess.getCheckUid() != null ? assess.getCheckUid() : assess.getSuperiorId()));
		row.setTestUid(toIntUid(assess.getTestUid() != null ? assess.getTestUid() : assess.getUserId()));
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
		Assess assess = getById(id);
		assertExists(assess);
		if (ObjectUtil.isNull(dto.getResult())) {
			// 申诉
			AssessAppeal appeal = new AssessAppeal();
			appeal.setAssessId(id);
			appeal.setContent(dto.getContent());
			appeal.setResult(0);
			assessAppealMapper.insert(appeal);
			assess.setIsAppeal(1);
			assess.setStatus(4);
		}
		else {
			// 驳回或通过
			AssessAppeal appeal = assessAppealMapper.selectOne(Wrappers.lambdaQuery(AssessAppeal.class)
				.eq(AssessAppeal::getAssessId, id)
				.eq(AssessAppeal::getResult, 0)
				.last("LIMIT 1"));
			if (ObjectUtil.isNotNull(appeal)) {
				appeal.setResult(dto.getResult());
				appeal.setOpinion(dto.getOpinion());
				appeal.setHandleTime(LocalDateTime.now());
				assessAppealMapper.updateById(appeal);
			}
			assess.setStatus(dto.getResult() == 2 ? 5 : 3);
		}
		updateById(assess);
	}

	@Override
	public AssessCensusVO census(AssessCensusDTO dto) {
		AssessCensusVO vo = new AssessCensusVO();
		vo.setLevelStats(Collections.emptyList());
		vo.setTotal(0);
		return vo;
	}

	@Override
	public AssessCensusVO censusBar(AssessCensusDTO dto) {
		return census(dto);
	}

	@Override
	public List<Assess> abnormalList(Long entid, Long planId) {
		List<Long> abnormalIds = baseMapper.findAbnormalUsers(entid, planId);
		if (CollUtil.isEmpty(abnormalIds)) {
			return Collections.emptyList();
		}
		return baseMapper.selectList(Wrappers.lambdaQuery(Assess.class)
			.in(Assess::getUserId, abnormalIds)
			.eq(Assess::getEntid, entid)
			.isNull(Assess::getDeletedAt));
	}

	@Override
	public boolean isAbnormal(Long entid, Long planId) {
		return !baseMapper.findAbnormalUsers(entid, planId).isEmpty();
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
