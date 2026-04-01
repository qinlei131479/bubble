package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.AssessConfigSaveDTO;
import com.bubblecloud.oa.api.entity.AssessScore;
import com.bubblecloud.oa.api.vo.SimplePageVO;

/**
 * 绩效评分级别/配置服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface AssessScoreService extends UpService<AssessScore> {

	SimplePageVO pageScoreConfig(Pg<AssessScore> pg);

	void saveScoreConfig(AssessConfigSaveDTO dto);

	Object getExamineConfig();

}
