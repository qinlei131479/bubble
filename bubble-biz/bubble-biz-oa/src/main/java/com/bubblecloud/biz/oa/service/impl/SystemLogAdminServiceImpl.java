package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.EnterpriseLogMapper;
import com.bubblecloud.biz.oa.service.SystemLogAdminService;
import com.bubblecloud.oa.api.entity.EnterpriseLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统操作日志实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
@RequiredArgsConstructor
public class SystemLogAdminServiceImpl implements SystemLogAdminService {

	private final EnterpriseLogMapper enterpriseLogMapper;

	@Override
	public Page<EnterpriseLog> pageList(String userName, String path, String eventName, int entid, int page, int limit) {
		var q = Wrappers.lambdaQuery(EnterpriseLog.class)
			.eq(EnterpriseLog::getEntid, entid)
			.orderByDesc(EnterpriseLog::getId);
		if (StringUtils.hasText(userName)) {
			q.like(EnterpriseLog::getUserName, userName);
		}
		if (StringUtils.hasText(path)) {
			q.like(EnterpriseLog::getPath, path);
		}
		if (StringUtils.hasText(eventName)) {
			q.like(EnterpriseLog::getEventName, eventName);
		}
		return enterpriseLogMapper.selectPage(new Page<>(page, limit), q);
	}

}
