package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.service.OaCrmTimerTaskNameSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

/**
 * 定时任务名称同步占位（PHP Swoole Task 内存表；Java 侧待接 eb_task 或等价组件）。
 *
 * @author qinlei
 * @date 2026/4/6 18:35
 */
@Service
public class OaCrmTimerTaskNameSyncServiceImpl implements OaCrmTimerTaskNameSyncService {

	private static final Logger log = LoggerFactory.getLogger(OaCrmTimerTaskNameSyncServiceImpl.class);

	@Override
	public void updateNameByUniqued(String uniqued, String name) {
		if (StrUtil.isBlank(uniqued)) {
			return;
		}
		log.trace("CRM timer task name sync skipped uniqued={} name={}", uniqued, name);
	}

}
