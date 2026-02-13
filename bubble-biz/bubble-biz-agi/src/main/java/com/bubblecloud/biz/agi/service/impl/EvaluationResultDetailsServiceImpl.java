package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.EvaluationResultDetails;
import com.bubblecloud.biz.agi.mapper.EvaluationResultDetailsMapper;
import com.bubblecloud.biz.agi.service.EvaluationResultDetailsService;
import org.springframework.stereotype.Service;

/**
 * 评估结果明细
 *
 * @author Rampart
 * @date 2026-02-13 16:52:13
 */
@Service
public class EvaluationResultDetailsServiceImpl extends UpServiceImpl<EvaluationResultDetailsMapper, EvaluationResultDetails> implements EvaluationResultDetailsService {

}