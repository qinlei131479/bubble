package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.EvaluationResultDetails;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评估结果明细 ORM映射层
 *
 * @author Rampart
 * @date 2026-02-13 16:52:13
 */
@Mapper
public interface EvaluationResultDetailsMapper extends UpMapper<EvaluationResultDetails> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(EvaluationResultDetails req);
}