package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.AssessScoreMapper;
import com.bubblecloud.biz.oa.service.AssessScoreService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.AssessScore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 绩效评分级别/配置服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class AssessScoreServiceImpl extends UpServiceImpl<AssessScoreMapper, AssessScore>
		implements AssessScoreService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(AssessScore req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(AssessScore req) {
		return super.update(req);
	}

}
