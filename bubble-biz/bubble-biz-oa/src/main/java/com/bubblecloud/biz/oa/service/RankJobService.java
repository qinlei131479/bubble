package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.JobSubordinateUpdateDTO;
import com.bubblecloud.oa.api.entity.RankJob;

/**
 * 岗位服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface RankJobService extends UpService<RankJob> {

	void updateJobStatus(Long id, Integer status);

	void updateSubordinate(Long id, JobSubordinateUpdateDTO dto);

}
