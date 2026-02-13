package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.EvaluationResults;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评估结果 ORM映射层
 *
 * @author Rampart
 * @date 2026-02-13 16:53:41
 */
@Mapper
public interface EvaluationResultsMapper extends UpMapper<EvaluationResults> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(EvaluationResults req);
}