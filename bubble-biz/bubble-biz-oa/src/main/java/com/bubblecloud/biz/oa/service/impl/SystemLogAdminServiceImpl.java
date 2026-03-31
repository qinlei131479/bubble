package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.EnterpriseLogMapper;
import com.bubblecloud.biz.oa.service.SystemLogAdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.EnterpriseLog;
import org.springframework.stereotype.Service;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统操作日志实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
public class SystemLogAdminServiceImpl extends UpServiceImpl<EnterpriseLogMapper, EnterpriseLog>
		implements SystemLogAdminService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(EnterpriseLog req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(EnterpriseLog req) {
		return super.update(req);
	}

}
