package com.bubblecloud.biz.oa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bubblecloud.common.core.util.R;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.EmployeeTrainMapper;
import com.bubblecloud.biz.oa.service.EmployeeTrainService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.EmployeeTrain;
import cn.hutool.core.util.ObjectUtil;

/**
 * 员工培训实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Service
public class EmployeeTrainServiceImpl extends UpServiceImpl<EmployeeTrainMapper, EmployeeTrain>
		implements EmployeeTrainService {

	private static final String COMPANY_PROFILE = "company_profile";

	private static final String STRATEGIC_PLAN = "strategic_plan";

	private static final String ORGANIZATION_CHART = "organization_chart";

	private static void validateType(String type) {
		if (!COMPANY_PROFILE.equals(type) && !STRATEGIC_PLAN.equals(type) && !ORGANIZATION_CHART.equals(type)) {
			throw new IllegalArgumentException("培训类型错误");
		}
	}

	@Override
	public EmployeeTrain getInfo(String type) {
		validateType(type);
		EmployeeTrain row = baseMapper
				.selectOne(Wrappers.lambdaQuery(EmployeeTrain.class).eq(EmployeeTrain::getType, type));
		if (ObjectUtil.isNull(row)) {
			row = new EmployeeTrain();
			row.setType(type);
			row.setContent("");
		}
		return row;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(EmployeeTrain req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(EmployeeTrain req) {
		validateType(req.getType());
		String content = ObjectUtil.isNull(req.getContent()) ? "" : req.getContent();
		EmployeeTrain existing = baseMapper
				.selectOne(Wrappers.lambdaQuery(EmployeeTrain.class).eq(EmployeeTrain::getType, req.getType()));
		if (ObjectUtil.isNull(existing)) {
			EmployeeTrain e = new EmployeeTrain();
			e.setType(req.getType());
			e.setContent(content);
			baseMapper.insert(e);
		} else {
			existing.setContent(content);
			baseMapper.updateById(existing);
		}
		return R.ok();
	}

}
