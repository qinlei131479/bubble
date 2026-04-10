package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.service.OaCrmAsyncNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * CRM 异步通知占位实现（对齐 PHP Task 投递语义，便于后续接 MQ）。
 *
 * @author qinlei
 * @date 2026/4/6 18:30
 */
@Service
public class OaCrmAsyncNotifyServiceImpl implements OaCrmAsyncNotifyService {

	private static final Logger log = LoggerFactory.getLogger(OaCrmAsyncNotifyServiceImpl.class);

	@Override
	public void clientRemindDeleted(int contractCid) {
		log.debug("CRM notify CLIENT_REMIND_NOTICE delete linkId={}", contractCid);
	}

	@Override
	public void clientFollowDeleted(int customerEid) {
		log.debug("CRM notify CLIENT_FOLLOW_NOTICE delete eid={}", customerEid);
	}

}
