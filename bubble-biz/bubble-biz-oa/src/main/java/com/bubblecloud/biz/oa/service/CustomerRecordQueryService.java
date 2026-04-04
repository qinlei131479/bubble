package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.oa.api.entity.CustomerRecord;

/**
 * 客户变更记录查询。
 *
 * @author qinlei
 * @date 2026/4/3 13:00
 */
public interface CustomerRecordQueryService {

	List<CustomerRecord> listByEid(int eid);

}
