package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.RankJobMapper;
import com.bubblecloud.biz.oa.service.RankJobService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.JobSaveDTO;
import com.bubblecloud.oa.api.dto.hr.JobSubordinateUpdateDTO;
import com.bubblecloud.oa.api.entity.RankJob;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 岗位服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class RankJobServiceImpl extends UpServiceImpl<RankJobMapper, RankJob> implements RankJobService {

	private static final String NOT_EXISTS = "common.operation.noExists";

	@Override
	public SimplePageVO pageJob(Pg<RankJob> pg, RankJob query) {
		Page<RankJob> r = findPg(pg, query);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createJob(JobSaveDTO dto) {
		RankJob entity = new RankJob();
		entity.setName(dto.getName());
		entity.setDescribe(dto.getDescribe());
		entity.setDuty(dto.getDuty());
		entity.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		save(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateJob(Long id, JobSaveDTO dto) {
		RankJob existing = getById(id);
		assertExists(existing);
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (StrUtil.isNotBlank(dto.getDescribe())) {
			existing.setDescribe(dto.getDescribe());
		}
		if (StrUtil.isNotBlank(dto.getDuty())) {
			existing.setDuty(dto.getDuty());
		}
		if (ObjectUtil.isNotNull(dto.getStatus())) {
			existing.setStatus(dto.getStatus());
		}
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeJob(Long id) {
		assertExists(getById(id));
		removeById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateJobStatus(Long id, Integer status) {
		RankJob existing = getById(id);
		assertExists(existing);
		existing.setStatus(status);
		updateById(existing);
	}

	@Override
	public List<RankJob> selectList(Long entid) {
		return list(Wrappers.lambdaQuery(RankJob.class)
				.eq(ObjectUtil.isNotNull(entid), RankJob::getEntid, entid)
				.eq(RankJob::getStatus, 1)
				.orderByAsc(RankJob::getId));
	}

	@Override
	public RankJob getSubordinateDetail(Long id) {
		RankJob job = getById(id);
		assertExists(job);
		return job;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateSubordinate(Long id, JobSubordinateUpdateDTO dto) {
		RankJob existing = getById(id);
		assertExists(existing);
		if (StrUtil.isNotBlank(dto.getDuty())) {
			existing.setDuty(dto.getDuty());
		}
		if (StrUtil.isNotBlank(dto.getDescribe())) {
			existing.setDescribe(dto.getDescribe());
		}
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(RankJob req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(RankJob req) {
		return super.update(req);
	}

	private void assertExists(RankJob entity) {
		if (ObjectUtil.isNull(entity)) {
			throw new IllegalArgumentException(NOT_EXISTS);
		}
	}

}
