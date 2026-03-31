package com.bubblecloud.biz.oa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.EmployeeTrainMapper;
import com.bubblecloud.biz.oa.service.EmployeeTrainService;
import com.bubblecloud.oa.api.dto.EmployeeTrainUpdateDTO;
import com.bubblecloud.oa.api.entity.EmployeeTrain;

import lombok.RequiredArgsConstructor;

/**
 * 员工培训实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Service
@RequiredArgsConstructor
public class EmployeeTrainServiceImpl implements EmployeeTrainService {

	private static final String COMPANY_PROFILE = "company_profile";

	private static final String STRATEGIC_PLAN = "strategic_plan";

	private static final String ORGANIZATION_CHART = "organization_chart";

	private final EmployeeTrainMapper employeeTrainMapper;

	private static void validateType(String type) {
		if (!COMPANY_PROFILE.equals(type) && !STRATEGIC_PLAN.equals(type) && !ORGANIZATION_CHART.equals(type)) {
			throw new IllegalArgumentException("培训类型错误");
		}
	}

	@Override
	public EmployeeTrain getInfo(String type) {
		validateType(type);
		EmployeeTrain row = employeeTrainMapper
			.selectOne(Wrappers.lambdaQuery(EmployeeTrain.class).eq(EmployeeTrain::getType, type));
		if (row == null) {
			row = new EmployeeTrain();
			row.setType(type);
			row.setContent("");
		}
		return row;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTrain(String type, EmployeeTrainUpdateDTO dto) {
		validateType(type);
		String content = dto.getContent() == null ? "" : dto.getContent();
		EmployeeTrain existing = employeeTrainMapper
			.selectOne(Wrappers.lambdaQuery(EmployeeTrain.class).eq(EmployeeTrain::getType, type));
		if (existing == null) {
			EmployeeTrain e = new EmployeeTrain();
			e.setType(type);
			e.setContent(content);
			employeeTrainMapper.insert(e);
		}
		else {
			existing.setContent(content);
			employeeTrainMapper.updateById(existing);
		}
	}

}
