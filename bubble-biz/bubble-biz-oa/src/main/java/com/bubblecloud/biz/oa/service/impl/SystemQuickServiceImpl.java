package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.bubblecloud.biz.oa.mapper.SystemQuickMapper;
import com.bubblecloud.biz.oa.service.SystemQuickService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.SystemQuick;
import org.springframework.stereotype.Service;

/**
 * 快捷入口实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
public class SystemQuickServiceImpl extends UpServiceImpl<SystemQuickMapper, SystemQuick>
		implements SystemQuickService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(SystemQuick req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(SystemQuick req) {
		return super.update(req);
	}

}
