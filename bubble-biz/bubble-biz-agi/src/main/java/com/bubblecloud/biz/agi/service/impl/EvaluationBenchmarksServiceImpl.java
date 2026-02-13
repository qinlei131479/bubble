package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.EvaluationBenchmarks;
import com.bubblecloud.biz.agi.mapper.EvaluationBenchmarksMapper;
import com.bubblecloud.biz.agi.service.EvaluationBenchmarksService;
import org.springframework.stereotype.Service;

/**
 * 评估基准
 *
 * @author Rampart
 * @date 2026-02-13 16:51:12
 */
@Service
public class EvaluationBenchmarksServiceImpl extends UpServiceImpl<EvaluationBenchmarksMapper, EvaluationBenchmarks> implements EvaluationBenchmarksService {

}