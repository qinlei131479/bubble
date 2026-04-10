package com.bubblecloud.biz.oa.service;

import java.math.BigDecimal;
import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.AssessScore;

/**
 * 绩效评分级别/配置服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface AssessScoreService extends UpService<AssessScore> {

	/**
	 * 企业下等级区间列表（按 level 升序）。
	 */
	List<AssessScore> listByEntidOrderByLevel(long entid);

	/**
	 * 删除并重建企业全部等级区间（对齐 PHP {@code bachAddOrSave}）。
	 */
	void replaceAllByEntid(long entid, List<AssessScore> rows);

	/**
	 * 将分数映射为等级 level。
	 */
	Integer resolveGrade(long entid, BigDecimal score);

}
