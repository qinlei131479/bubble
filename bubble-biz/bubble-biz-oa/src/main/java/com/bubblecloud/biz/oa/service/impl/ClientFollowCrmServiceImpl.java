package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.ClientFollowMapper;
import com.bubblecloud.biz.oa.service.ClientFollowCrmService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import java.time.LocalDateTime;

import com.bubblecloud.oa.api.entity.ClientFollow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 客户跟进实现。
 *
 * @author qinlei
 * @date 2026/4/3 14:00
 */
@Service
public class ClientFollowCrmServiceImpl extends UpServiceImpl<ClientFollowMapper, ClientFollow> implements ClientFollowCrmService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(ClientFollow dto) {
		return super.create(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(ClientFollow dto) {
		ClientFollow ex = getById(dto.getId());
		if (ObjectUtil.isNull(ex)) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		return super.update(dto);
	}

	@Override
	public void softDeleteById(Long id) {
		lambdaUpdate().eq(ClientFollow::getId, id).set(ClientFollow::getDeletedAt, LocalDateTime.now()).update();
	}

}
