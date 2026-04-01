package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.AssessAppealDTO;
import com.bubblecloud.oa.api.dto.hr.AssessCensusDTO;
import com.bubblecloud.oa.api.dto.hr.AssessEvalDTO;
import com.bubblecloud.oa.api.dto.hr.AssessSaveDTO;
import com.bubblecloud.oa.api.dto.hr.AssessTargetEvalDTO;
import com.bubblecloud.oa.api.entity.Assess;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.hr.AssessCensusVO;
import com.bubblecloud.oa.api.vo.hr.AssessDetailVO;

/**
 * 绩效考核服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface AssessService extends UpService<Assess> {

	SimplePageVO pageAssess(Pg<Assess> pg, Assess query);

	SimplePageVO pageAssessForHr(Pg<Assess> pg, Assess query);

	AssessDetailVO getAssessDetail(long id);

	void createAssess(AssessSaveDTO dto);

	void createAssessWithTemplate(AssessSaveDTO dto);

	void updateAssess(long id, AssessSaveDTO dto);

	void selfEval(long id, AssessEvalDTO dto);

	void superiorEval(long id, AssessEvalDTO dto);

	void examineEval(long id, AssessEvalDTO dto);

	void enableAssess(long id);

	void evalTarget(AssessTargetEvalDTO dto);

	List<Object> scoreRecord(long id);

	void deleteAssess(long id);

	List<Object> deleteRecord(Long entid);

	void appealOrReject(long id, AssessAppealDTO dto);

	AssessCensusVO census(AssessCensusDTO dto);

	AssessCensusVO censusBar(AssessCensusDTO dto);

	List<Assess> abnormalList(Long entid, Long planId);

	boolean isAbnormal(Long entid, Long planId);

}
