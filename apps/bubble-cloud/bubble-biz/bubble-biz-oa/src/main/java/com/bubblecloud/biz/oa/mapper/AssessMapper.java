package com.bubblecloud.biz.oa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.Assess;

/**
 * eb_assess Mapper。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Mapper
public interface AssessMapper extends UpMapper<Assess> {

	/**
	 * 校验考核维度是否存在。
	 */
	int countAssessSpace(@Param("entid") Long entid, @Param("assessId") Long assessId, @Param("spaceId") Long spaceId);

	/**
	 * 计划内应考但未生成考核记录的人员 ID（对齐 PHP 异常检测核心逻辑）。
	 */
	List<Long> findAbnormalTestUids(@Param("entid") Long entid, @Param("period") Integer period,
			@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

}
