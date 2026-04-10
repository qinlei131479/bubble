package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.bubblecloud.biz.oa.mapper.RankLevelMapper;
import com.bubblecloud.biz.oa.service.RankLevelService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.RankLevelBatchUpdateDTO;
import com.bubblecloud.oa.api.dto.hr.RankLevelSaveDTO;
import com.bubblecloud.oa.api.entity.Rank;
import com.bubblecloud.oa.api.entity.RankLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

/**
 * 职位等级服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class RankLevelServiceImpl extends UpServiceImpl<RankLevelMapper, RankLevel> implements RankLevelService {

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
		if (ObjectUtil.isNotNull(existing)) {
			existing.setRankId(rankId);
			baseMapper.updateById(existing);
		}
	}

	@Override
	public List<Rank> unrelatedRanks(Long jobId, Long entid) {
		return baseMapper.findUnrelatedRanks(jobId, entid);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(RankLevel req) {
		req.setSort(ObjectUtil.defaultIfNull(req.getSort(), 0));
		req.setStatus(ObjectUtil.defaultIfNull(req.getStatus(), 1));
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(RankLevel req) {
		return super.update(req);
	}

}
