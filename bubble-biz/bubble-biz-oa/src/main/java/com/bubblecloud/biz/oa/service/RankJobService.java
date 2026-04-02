package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.JobSaveDTO;
import com.bubblecloud.oa.api.dto.hr.JobSubordinateUpdateDTO;
import com.bubblecloud.oa.api.entity.RankJob;
import com.bubblecloud.oa.api.vo.SimplePageVO;

/**
 * 岗位服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface RankJobService extends UpService<RankJob> {

	SimplePageVO pageJob(Pg<RankJob> pg, RankJob query);

	void createJob(JobSaveDTO dto);

	void updateJob(Long id, JobSaveDTO dto);

	void removeJob(Long id);

	void updateJobStatus(Long id, Integer status);

	List<RankJob> selectList(Long entid);

	RankJob getSubordinateDetail(Long id);

	void updateSubordinate(Long id, JobSubordinateUpdateDTO dto);

}
