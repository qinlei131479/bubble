package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.AssessTarget;

/**
 * eb_assess_target Mapper。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Mapper
public interface AssessTargetMapper extends UpMapper<AssessTarget> {

	/**
	 * 指标自评：更新完成情况（对齐 PHP {@code selfEvalTarget}）。
	 */
	int updateFinishBySpace(@Param("targetId") long targetId, @Param("spaceId") long spaceId,
			@Param("finishInfo") String finishInfo, @Param("finishRatio") int finishRatio);

}
