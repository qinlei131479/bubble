package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.ClientFollow;

/**
 * 客户跟进。
 *
 * @author qinlei
 * @date 2026/4/3 14:00
 */
public interface ClientFollowCrmService extends UpService<ClientFollow> {

	void softDeleteById(Long id);

}
