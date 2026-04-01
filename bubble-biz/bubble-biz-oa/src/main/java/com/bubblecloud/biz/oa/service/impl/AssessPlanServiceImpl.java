package com.bubblecloud.biz.oa.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AssessPlanMapper;
import com.bubblecloud.biz.oa.service.AssessPlanService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.AssessPlanSaveDTO;
import com.bubblecloud.oa.api.entity.AssessPlan;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 绩效考核计划服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class AssessPlanServiceImpl extends UpServiceImpl<AssessPlanMapper, AssessPlan> implements AssessPlanService {

	private static final String NOT_EXISTS = "common.operation.noExists";

	@Override
	public SimplePageVO pagePlan(Pg<AssessPlan> pg, AssessPlan query) {
		Page<AssessPlan> r = findPg(pg, query);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createPlan(AssessPlanSaveDTO dto) {
		AssessPlan entity = new AssessPlan();
		entity.setName(dto.getName());
		entity.setCycleType(dto.getCycleType());
		entity.setStartDate(dto.getStartDate());
		entity.setEndDate(dto.getEndDate());
		entity.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		if (CollUtil.isNotEmpty(dto.getUserIds())) {
			entity.setUserIds(dto.getUserIds().stream()
					.map(String::valueOf).collect(Collectors.joining(",", "[", "]")));
		}
		save(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updatePlan(long id, AssessPlanSaveDTO dto) {
		AssessPlan existing = getById(id);
		assertExists(existing);
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (StrUtil.isNotBlank(dto.getCycleType())) {
			existing.setCycleType(dto.getCycleType());
		}
		if (ObjectUtil.isNotNull(dto.getStartDate())) {
			existing.setStartDate(dto.getStartDate());
		}
		if (ObjectUtil.isNotNull(dto.getEndDate())) {
			existing.setEndDate(dto.getEndDate());
		}
		if (ObjectUtil.isNotNull(dto.getStatus())) {
			existing.setStatus(dto.getStatus());
		}
		if (CollUtil.isNotEmpty(dto.getUserIds())) {
			existing.setUserIds(dto.getUserIds().stream()
					.map(String::valueOf).collect(Collectors.joining(",", "[", "]")));
		}
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removePlan(long id) {
		assertExists(getById(id));
		removeById(id);
	}

	@Override
	public List<AssessPlan> enabledPlans(Long entid) {
		return list(Wrappers.lambdaQuery(AssessPlan.class)
				.eq(ObjectUtil.isNotNull(entid), AssessPlan::getEntid, entid)
				.eq(AssessPlan::getStatus, 1)
				.isNull(AssessPlan::getDeletedAt)
				.orderByDesc(AssessPlan::getId));
	}

	@Override
	public List<Object> planUsers(long id) {
		AssessPlan plan = getById(id);
		if (ObjectUtil.isNull(plan) || StrUtil.isBlank(plan.getUserIds())) {
			return Collections.emptyList();
		}
		return Collections.singletonList(plan.getUserIds());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(AssessPlan req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(AssessPlan req) {
		return super.update(req);
	}

	private void assertExists(AssessPlan entity) {
		if (ObjectUtil.isNull(entity) || ObjectUtil.isNotNull(entity.getDeletedAt())) {
			throw new IllegalArgumentException(NOT_EXISTS);
		}
	}

}
