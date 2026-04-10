package com.bubblecloud.biz.oa.service;

/**
 * 系统默认数据包路径（对齐 PHP CommonController::initData）。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
public interface SystemBackupService {

	/**
	 * 按版本取最新一条备份的 path；无则空串。
	 * @param version 版本号，空则不限定版本
	 */
	String findLatestPathByVersion(String version);

}
