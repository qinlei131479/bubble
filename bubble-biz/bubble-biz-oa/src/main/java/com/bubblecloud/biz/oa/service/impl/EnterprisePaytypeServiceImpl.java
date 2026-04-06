package com.bubblecloud.biz.oa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.EnterprisePaytypeMapper;
import com.bubblecloud.biz.oa.service.EnterprisePaytypeService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.EnterprisePaytype;

import cn.hutool.core.util.ObjectUtil;

/**
 * 企业支付方式实现。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Service
public class EnterprisePaytypeServiceImpl extends UpServiceImpl<EnterprisePaytypeMapper, EnterprisePaytype>
		implements EnterprisePaytypeService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(EnterprisePaytype dto) {
		dto.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		dto.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		dto.setTypeId(ObjectUtil.defaultIfNull(dto.getTypeId(), 0));
		return super.create(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(EnterprisePaytype dto) {
		EnterprisePaytype existing = baseMapper.selectById(dto.getId());
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		return super.update(dto);
	}

}
