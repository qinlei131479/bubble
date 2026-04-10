package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.AssessAppealDTO;
import com.bubblecloud.oa.api.dto.hr.AssessCensusDTO;
import com.bubblecloud.oa.api.dto.hr.AssessEvalDTO;
import com.bubblecloud.oa.api.dto.hr.AssessTargetEvalDTO;
import com.bubblecloud.oa.api.entity.Assess;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.bubblecloud.oa.api.vo.hr.AssessAbnormalUserVO;
import com.bubblecloud.oa.api.vo.hr.AssessCensusVO;
import com.bubblecloud.oa.api.vo.hr.AssessExplainVO;
import com.bubblecloud.oa.api.vo.hr.AssessInfoVO;

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

	/**
	 * 启用/停用（对齐 PHP {@code showAssess} 的 status 0/1）。
	 */
	void enableAssess(Long id, Integer status);

	void evalTarget(AssessTargetEvalDTO dto);

	List<Object> scoreRecord(Long assessId);

	List<Object> deleteRecord(Long entid);

	void appealOrReject(Long id, AssessAppealDTO dto);

	/**
	 * 删除绩效并写入删除流水（对齐 PHP {@code deleteAssess}，需删除原因）。
	 */
	void deleteAssess(Long id, String mark);

	AssessCensusVO census(AssessCensusDTO dto);

	AssessCensusVO censusBar(AssessCensusDTO dto);

	List<AssessAbnormalUserVO> abnormalList(Integer period, String time, Long entid);

	int abnormalCount(Integer period, String time, Long entid);

	AssessInfoVO getAssessInfo(Long id);

	AssessExplainVO getAssessExplain(Long id);

	OaElFormVO buildDeleteForm(Long id, Long entid);

}
