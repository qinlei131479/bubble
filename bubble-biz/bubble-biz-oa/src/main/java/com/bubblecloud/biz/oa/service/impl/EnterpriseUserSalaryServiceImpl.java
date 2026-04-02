package com.bubblecloud.biz.oa.service.impl;

import java.math.BigDecimal;

import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import com.bubblecloud.biz.oa.mapper.EnterpriseUserSalaryMapper;
import com.bubblecloud.biz.oa.service.EnterpriseUserSalaryService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.EnterpriseUserSalary;
import cn.hutool.core.util.ObjectUtil;

/**
 * 调薪记录实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Service
public class EnterpriseUserSalaryServiceImpl extends UpServiceImpl<EnterpriseUserSalaryMapper, EnterpriseUserSalary>
		implements EnterpriseUserSalaryService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(EnterpriseUserSalary req) {
		req.setEntid(ObjectUtil.defaultIfNull(req.getEntid(), 1L));
		req.setCardId(ObjectUtil.defaultIfNull(req.getCardId(), 0L));
		req.setTotal(ObjectUtil.defaultIfNull(req.getTotal(), BigDecimal.ZERO));
		req.setContent(ObjectUtil.defaultIfBlank(req.getContent(), ""));
		req.setMark(ObjectUtil.defaultIfBlank(req.getMark(), ""));
		req.setLinkId(0);
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(EnterpriseUserSalary req) {
		return super.update(req);
	}

}
