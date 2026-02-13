package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.EvaluationResults;
import com.bubblecloud.biz.agi.mapper.EvaluationResultsMapper;
import com.bubblecloud.biz.agi.service.EvaluationResultsService;
import org.springframework.stereotype.Service;

/**
 * 评估结果
 *
 * @author Rampart
 * @date 2026-02-13 16:53:41
 */
@Service
public class EvaluationResultsServiceImpl extends UpServiceImpl<EvaluationResultsMapper, EvaluationResults> implements EvaluationResultsService {

}