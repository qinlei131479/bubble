package com.bubblecloud.biz.oa.service;

/**
 * 对齐 PHP {@code TaskService::update(['uniqued'=>...], ['name'=>...])}；无内置任务表时为空操作。
 *
 * @author qinlei
 * @date 2026/4/6 18:35
 */
public interface OaCrmTimerTaskNameSyncService {

	void updateNameByUniqued(String uniqued, String name);

}
