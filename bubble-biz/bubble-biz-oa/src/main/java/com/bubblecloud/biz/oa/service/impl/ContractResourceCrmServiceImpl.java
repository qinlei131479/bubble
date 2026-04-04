package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.ContractResourceMapper;
import com.bubblecloud.biz.oa.service.ContractResourceCrmService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.ContractResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 合同附件备注实现。
 *
 * @author qinlei
 * @date 2026/4/3 13:00
 */
@Service
public class ContractResourceCrmServiceImpl extends UpServiceImpl<ContractResourceMapper, ContractResource>
		implements ContractResourceCrmService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(ContractResource dto) {
		return super.create(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(ContractResource dto) {
		ContractResource ex = getById(dto.getId());
		if (ObjectUtil.isNull(ex)) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		return super.update(dto);
	}

}
