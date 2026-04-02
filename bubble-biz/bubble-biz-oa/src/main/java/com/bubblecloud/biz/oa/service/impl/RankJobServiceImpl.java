package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.RankJobMapper;
import com.bubblecloud.biz.oa.service.RankJobService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.JobSubordinateUpdateDTO;
import com.bubblecloud.oa.api.entity.RankJob;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 岗位服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class RankJobServiceImpl extends UpServiceImpl<RankJobMapper, RankJob> implements RankJobService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateJobStatus(Long id, Integer status) {
		RankJob existing = getById(id);
		assertExists(existing);
		existing.setStatus(status);
		baseMapper.updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateSubordinate(Long id, JobSubordinateUpdateDTO dto) {
		RankJob existing = getById(id);
		assertExists(existing);
		baseMapper.updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(RankJob req) {
		req.setStatus(ObjectUtil.defaultIfNull(req.getStatus(), 1));
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(RankJob req) {
		return super.update(req);
	}

	private void assertExists(RankJob entity) {
		if (ObjectUtil.isNull(entity)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
	}

}
