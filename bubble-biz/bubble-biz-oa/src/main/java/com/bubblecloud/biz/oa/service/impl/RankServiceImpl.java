package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.RankMapper;
import com.bubblecloud.biz.oa.service.RankService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Rank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 职级服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class RankServiceImpl extends UpServiceImpl<RankMapper, Rank> implements RankService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Rank req) {
		req.setSort(ObjectUtil.defaultIfNull(req.getSort(), 0));
		req.setStatus(ObjectUtil.defaultIfNull(req.getStatus(), 1));
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Rank req) {
		return super.update(req);
	}


}
