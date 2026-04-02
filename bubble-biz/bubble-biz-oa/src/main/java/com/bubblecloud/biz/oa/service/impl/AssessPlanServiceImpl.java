package com.bubblecloud.biz.oa.service.impl;


import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.AssessPlanMapper;
import com.bubblecloud.biz.oa.service.AssessPlanService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.AssessPlan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 绩效考核计划服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class AssessPlanServiceImpl extends UpServiceImpl<AssessPlanMapper, AssessPlan> implements AssessPlanService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(AssessPlan dto) {
		dto.setName(dto.getName());
		dto.setCycleType(dto.getCycleType());
		dto.setStartDate(dto.getStartDate());
		dto.setEndDate(dto.getEndDate());
		dto.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		return super.create(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(AssessPlan dto) {
		AssessPlan existing = getById(dto.getId());
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		return super.update(existing);
	}


}
