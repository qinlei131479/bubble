package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.JobSubordinateUpdateDTO;
import com.bubblecloud.oa.api.entity.RankJob;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.hr.JobSubordinateDetailVO;

/**
 * 岗位服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface RankJobService extends UpService<RankJob> {

	void updateJobStatus(Long id, Integer status);

	/**
	 * 下拉列表（对齐 PHP {@code getSelectList}，按企业过滤）。
	 */
	List<RankJob> listSelect(Long entid);

	/**
	 * 下级成员分页（对齐 PHP {@code subordinate}，需 entid）。
	 */
	SimplePageVO subordinatePage(Pg pg, Long entid, String name);

	/**
	 * 职责详情：路径 id 为员工 id（对齐 PHP {@code subordinate/{id}}）。
	 */
	JobSubordinateDetailVO subordinateDetail(Long userId);

	/**
	 * 修改职责：路径 id 为员工 id（对齐 PHP {@code subordinateUpdate}）。
	 */
	void updateSubordinate(Long userId, JobSubordinateUpdateDTO dto);

}
