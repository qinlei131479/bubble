package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Approve;

/**
 * 审批配置（对齐 PHP ent/approve/config ApproveService）。
 *
 * @author qinlei
 * @date 2026/4/2 15:00
 */
public interface ApproveConfigService extends UpService<Approve> {

	/**
	 * 审批类型筛选列表（占位：后续接 ApproveService.getSearchList）。
	 */
	List<Long> getSearchList(Integer types, Long adminId);

}
