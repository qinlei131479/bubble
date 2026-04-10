package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.oa.api.dto.jobanalysis.JobAnalysisUpdateDTO;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.jobanalysis.JobAnalysisDetailVO;
import com.bubblecloud.oa.api.vo.jobanalysis.JobAnalysisListItemVO;
import com.bubblecloud.oa.api.vo.jobanalysis.JobAnalysisMineVO;

/**
 * 工作分析（对齐 PHP {@code ent/company/job_analysis}）。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
public interface JobAnalysisService {

	ListCountVO<JobAnalysisListItemVO> list(long entid, long currentAdminId, Integer frameId, String name,
			List<Integer> types, Integer jobId, int page, int limit);

	Object info(long entid, long targetAdminId);

	JobAnalysisMineVO mine(long entid, long currentAdminId);

	void update(long entid, long targetAdminId, JobAnalysisUpdateDTO dto);

}
