package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.EvaluationBenchmarks;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评估基准 ORM映射层
 *
 * @author Rampart
 * @date 2026-02-13 16:51:12
 */
@Mapper
public interface EvaluationBenchmarksMapper extends UpMapper<EvaluationBenchmarks> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(EvaluationBenchmarks req);
}