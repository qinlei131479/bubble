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
import com.bubblecloud.biz.oa.mapper.AssessScoreMapper;
import com.bubblecloud.biz.oa.service.AssessService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.AssessAppealDTO;
import com.bubblecloud.oa.api.dto.hr.AssessCensusDTO;
import com.bubblecloud.oa.api.dto.hr.AssessEvalDTO;
import com.bubblecloud.oa.api.dto.hr.AssessTargetEvalDTO;
import com.bubblecloud.oa.api.entity.Assess;
import com.bubblecloud.oa.api.entity.AssessAppeal;
import com.bubblecloud.oa.api.entity.AssessScore;
import com.bubblecloud.oa.api.vo.hr.AssessCensusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

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

	private final AssessScoreMapper assessScoreMapper;

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
	public void enableAssess(Long id) {
		Assess assess = getById(id);
		assertExists(assess);
		assess.setStatus(ObjectUtil.defaultIfNull(assess.getStatus(), 0) == 0 ? 1 : 0);
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
	public List<Object> scoreRecord(Long id) {
		List<AssessScore> scores = assessScoreMapper.selectList(Wrappers.lambdaQuery(AssessScore.class)
			.eq(AssessScore::getUserId, id)
			.orderByAsc(AssessScore::getLevel));
		return new ArrayList<>(scores);
	}

	@Override
	public List<Object> deleteRecord(Long entid) {
		List<Assess> deleted = baseMapper.selectList(Wrappers.lambdaQuery(Assess.class)
			.eq(ObjectUtil.isNotNull(entid), Assess::getEntid, entid)
			.isNotNull(Assess::getDeletedAt));
		return new ArrayList<>(deleted);
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
