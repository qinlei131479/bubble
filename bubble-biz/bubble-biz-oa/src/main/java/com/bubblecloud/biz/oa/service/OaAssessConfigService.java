package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.dto.hr.AssessScoreConfigSaveDTO;
import com.bubblecloud.oa.api.vo.hr.AssessScoreConfigVO;
import com.bubblecloud.oa.api.vo.hr.AssessVerifyConfigVO;

/**
 * 绩效系统配置（对齐 PHP {@code AssessConfigService}）。
 *
 * @author qinlei
 * @date 2026/4/7 13:00
 */
public interface OaAssessConfigService {

	AssessScoreConfigVO getScoreConfig(long entid);

	void saveScoreConfig(long entid, AssessScoreConfigSaveDTO dto);

	AssessVerifyConfigVO getVerifyConfig();

}
