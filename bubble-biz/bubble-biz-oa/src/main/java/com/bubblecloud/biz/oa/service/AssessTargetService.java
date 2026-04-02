package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.AssessTargetSaveDTO;
import com.bubblecloud.oa.api.entity.AssessTarget;
import com.bubblecloud.oa.api.vo.SimplePageVO;

/**
 * 绩效指标服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface AssessTargetService extends UpService<AssessTarget> {

	SimplePageVO pageTarget(Pg<AssessTarget> pg, AssessTarget query);

	void createTarget(AssessTargetSaveDTO dto);

	void updateTarget(Long id, AssessTargetSaveDTO dto);

	void removeTarget(Long id);

}
