package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.AssessTargetMapper;
import com.bubblecloud.biz.oa.service.AssessTargetService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.AssessTarget;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 绩效指标服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class AssessTargetServiceImpl extends UpServiceImpl<AssessTargetMapper, AssessTarget>
		implements AssessTargetService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(AssessTarget req) {
		req.setWeight(ObjectUtil.defaultIfNull(req.getWeight(), 0));
		req.setSort(ObjectUtil.defaultIfNull(req.getSort(), 0));
		req.setStatus(ObjectUtil.defaultIfNull(req.getStatus(), 1));
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(AssessTarget req) {
		AssessTarget existing = baseMapper.selectById(req.getId());
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		return super.update(req);
	}


}
