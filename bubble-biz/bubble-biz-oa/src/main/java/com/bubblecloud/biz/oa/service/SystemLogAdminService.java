package com.bubblecloud.biz.oa.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.EnterpriseLog;

/**
 * 系统操作日志（eb_enterprise_log_*），对齐 PHP ent/system/log。
 *
 * @author qinlei
 * @date 2026/3/31
 */
public interface SystemLogAdminService extends UpService<EnterpriseLog> {

	/**
	 * 日志分页。
	 */
	Page<EnterpriseLog> pageList(String userName, String path, String eventName, int entid, int page, int limit);

}
