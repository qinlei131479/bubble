package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.EnterpriseLogMapper;
import com.bubblecloud.biz.oa.service.SystemLogAdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.EnterpriseLog;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

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
	public Page<EnterpriseLog> pageList(String userName, String path, String eventName, int entid, int page, int limit) {
		var q = Wrappers.lambdaQuery(EnterpriseLog.class)
			.eq(EnterpriseLog::getEntid, entid)
			.orderByDesc(EnterpriseLog::getId);
		if (StrUtil.isNotBlank(userName)) {
			q.like(EnterpriseLog::getUserName, userName);
		}
		if (StrUtil.isNotBlank(path)) {
			q.like(EnterpriseLog::getPath, path);
		}
		if (StrUtil.isNotBlank(eventName)) {
			q.like(EnterpriseLog::getEventName, eventName);
		}
		return baseMapper.selectPage(new Page<>(page, limit), q);
	}

}
