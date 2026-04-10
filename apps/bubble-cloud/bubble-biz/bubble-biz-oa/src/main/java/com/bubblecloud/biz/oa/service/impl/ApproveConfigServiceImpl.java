package com.bubblecloud.biz.oa.service.impl;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.biz.oa.mapper.ApproveMapper;
import com.bubblecloud.biz.oa.service.ApproveConfigService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Approve;
import org.springframework.stereotype.Service;

/**
 * 审批配置实现。
 *
 * @author qinlei
 * @date 2026/4/2 15:00
 */
@Service
public class ApproveConfigServiceImpl extends UpServiceImpl<ApproveMapper, Approve> implements ApproveConfigService {

	@Override
	public List<Long> getSearchList(Integer types, Long adminId) {
		return Collections.emptyList();
	}

}
