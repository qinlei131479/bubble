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

	void selfEval(Long id, AssessEvalDTO dto);

	void superiorEval(Long id, AssessEvalDTO dto);

	void examineEval(Long id, AssessEvalDTO dto);

	void enableAssess(Long id);

	void evalTarget(AssessTargetEvalDTO dto);

	List<Object> scoreRecord(Long id);

	List<Object> deleteRecord(Long entid);

	void appealOrReject(Long id, AssessAppealDTO dto);

	AssessCensusVO census(AssessCensusDTO dto);

	AssessCensusVO censusBar(AssessCensusDTO dto);

	List<Assess> abnormalList(Long entid, Long planId);

	boolean isAbnormal(Long entid, Long planId);

}
