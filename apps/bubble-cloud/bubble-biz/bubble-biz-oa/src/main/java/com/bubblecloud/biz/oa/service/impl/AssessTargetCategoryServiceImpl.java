package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.AssessTargetCategoryMapper;
import com.bubblecloud.biz.oa.service.AssessTargetCategoryService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.AssessTargetCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 绩效指标分类服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class AssessTargetCategoryServiceImpl extends UpServiceImpl<AssessTargetCategoryMapper, AssessTargetCategory>
		implements AssessTargetCategoryService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(AssessTargetCategory req) {
		req.setSort(ObjectUtil.defaultIfNull(req.getSort(), 0));
		req.setStatus(ObjectUtil.defaultIfNull(req.getStatus(), 1));
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(AssessTargetCategory req) {
		AssessTargetCategory existing = baseMapper.selectById(req.getId());
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		return super.update(req);
	}

}
