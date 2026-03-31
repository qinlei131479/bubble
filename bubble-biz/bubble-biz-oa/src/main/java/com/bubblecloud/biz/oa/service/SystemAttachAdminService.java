package com.bubblecloud.biz.oa.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.SystemAttach;

/**
 * 系统附件（eb_system_attach），对齐 PHP ent/system/attach。
 *
 * @author qinlei
 * @date 2026/3/31
 */
public interface SystemAttachAdminService extends UpService<SystemAttach> {

	/**
	 * 附件分页列表。
	 * @param entid 企业 ID
	 * @param page 页码（与 PHP 一致）
	 * @param limit 每页条数
	 */
	Page<SystemAttach> pageList(int entid, int page, int limit);

}
