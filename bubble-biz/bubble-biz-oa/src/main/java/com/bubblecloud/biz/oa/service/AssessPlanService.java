package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.AssessPlanSaveDTO;
import com.bubblecloud.oa.api.entity.AssessPlan;
import com.bubblecloud.oa.api.vo.SimplePageVO;

/**
 * 绩效考核计划服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface AssessPlanService extends UpService<AssessPlan> {

	SimplePageVO pagePlan(Pg<AssessPlan> pg, AssessPlan query);

	void createPlan(AssessPlanSaveDTO dto);

	void updatePlan(long id, AssessPlanSaveDTO dto);

	void removePlan(long id);

	List<AssessPlan> enabledPlans(Long entid);

	List<Object> planUsers(long id);

}
