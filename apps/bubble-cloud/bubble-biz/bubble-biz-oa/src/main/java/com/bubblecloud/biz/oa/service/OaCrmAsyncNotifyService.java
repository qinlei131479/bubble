package com.bubblecloud.biz.oa.service;

/**
 * 对齐 PHP {@code StatusChangeTask} 投递点；默认仅打日志，可替换为消息队列/WebSocket 实现。
 *
 * @author qinlei
 * @date 2026/4/6 18:30
 */
public interface OaCrmAsyncNotifyService {

	void clientRemindDeleted(int contractCid);

	void clientFollowDeleted(int customerEid);

}
