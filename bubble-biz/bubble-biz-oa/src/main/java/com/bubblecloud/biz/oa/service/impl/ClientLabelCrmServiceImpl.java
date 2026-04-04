package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.ClientLabelMapper;
import com.bubblecloud.biz.oa.service.ClientLabelCrmService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.ClientLabel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 客户标签实现。
 *
 * @author qinlei
 * @date 2026/4/3 14:00
 */
@Service
public class ClientLabelCrmServiceImpl extends UpServiceImpl<ClientLabelMapper, ClientLabel> implements ClientLabelCrmService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(ClientLabel dto) {
		return super.create(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(ClientLabel dto) {
		if (ObjectUtil.isNull(getById(dto.getId()))) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		return super.update(dto);
	}

}
