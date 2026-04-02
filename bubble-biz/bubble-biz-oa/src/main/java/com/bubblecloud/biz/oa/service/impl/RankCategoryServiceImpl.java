package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.RankCategoryMapper;
import com.bubblecloud.biz.oa.service.RankCategoryService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.RankCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 职级体系分类服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class RankCategoryServiceImpl extends UpServiceImpl<RankCategoryMapper, RankCategory>
		implements RankCategoryService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(RankCategory req) {
		req.setSort(ObjectUtil.defaultIfNull(req.getSort(), 0));
		req.setStatus(ObjectUtil.defaultIfNull(req.getStatus(), 1));
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(RankCategory req) {
		return super.update(req);
	}

}
