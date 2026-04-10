package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CustomerRecordMapper;
import com.bubblecloud.biz.oa.service.CustomerRecordQueryService;
import com.bubblecloud.oa.api.entity.CustomerRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 客户变更记录查询实现。
 *
 * @author qinlei
 * @date 2026/4/3 13:00
 */
@Service
@RequiredArgsConstructor
public class CustomerRecordQueryServiceImpl implements CustomerRecordQueryService {

	private final CustomerRecordMapper customerRecordMapper;

	@Override
	public List<CustomerRecord> listByEid(int eid) {
		return customerRecordMapper.selectList(Wrappers.lambdaQuery(CustomerRecord.class)
			.eq(CustomerRecord::getEid, eid)
			.orderByDesc(CustomerRecord::getId));
	}

}
