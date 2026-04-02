package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.RankLevelMapper;
import com.bubblecloud.biz.oa.service.RankLevelService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.RankLevelBatchUpdateDTO;
import com.bubblecloud.oa.api.dto.hr.RankLevelSaveDTO;
import com.bubblecloud.oa.api.entity.Rank;
import com.bubblecloud.oa.api.entity.RankLevel;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 职位等级服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class RankLevelServiceImpl extends UpServiceImpl<RankLevelMapper, RankLevel> implements RankLevelService {

	@Override
	public SimplePageVO pageRankLevel(Pg<RankLevel> pg, RankLevel query) {
		Page<RankLevel> r = findPg(pg, query);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createRankLevel(RankLevelSaveDTO dto) {
		RankLevel entity = new RankLevel();
		entity.setJobId(dto.getJobId());
		entity.setName(dto.getName());
		entity.setSalaryMin(dto.getSalaryMin());
		entity.setSalaryMax(dto.getSalaryMax());
		entity.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		entity.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		save(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRankLevel(Long id, RankLevelSaveDTO dto) {
		RankLevel existing = getById(id);
		assertExists(existing);
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (ObjectUtil.isNotNull(dto.getSalaryMin())) {
			existing.setSalaryMin(dto.getSalaryMin());
		}
		if (ObjectUtil.isNotNull(dto.getSalaryMax())) {
			existing.setSalaryMax(dto.getSalaryMax());
		}
		if (ObjectUtil.isNotNull(dto.getSort())) {
			existing.setSort(dto.getSort());
		}
		if (ObjectUtil.isNotNull(dto.getStatus())) {
			existing.setStatus(dto.getStatus());
		}
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchUpdateRankLevel(String batch, RankLevelBatchUpdateDTO dto) {
		if (ObjectUtil.isNull(dto) || CollUtil.isEmpty(dto.getList())) {
			return;
		}
		int sort = 1;
		for (RankLevelSaveDTO item : dto.getList()) {
			RankLevel entity = new RankLevel();
			entity.setJobId(item.getJobId());
			entity.setName(item.getName());
			entity.setSalaryMin(item.getSalaryMin());
			entity.setSalaryMax(item.getSalaryMax());
			entity.setSort(sort++);
			entity.setStatus(ObjectUtil.defaultIfNull(item.getStatus(), 1));
			entity.setBatch(batch);
			save(entity);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void relateRank(Long id, Long rankId) {
		RankLevel existing = getById(id);
		assertExists(existing);
		existing.setRankId(rankId);
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeRelateRank(Long id) {
		RankLevel existing = getById(id);
		assertExists(existing);
		existing.setRankId(null);
		updateById(existing);
	}

	@Override
	public List<Rank> unrelatedRanks(Long jobId, Long entid) {
		return baseMapper.findUnrelatedRanks(jobId, entid);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(RankLevel req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(RankLevel req) {
		return super.update(req);
	}

	private void assertExists(RankLevel entity) {
		if (ObjectUtil.isNull(entity) || ObjectUtil.isNotNull(entity.getDeletedAt())) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
	}

}
