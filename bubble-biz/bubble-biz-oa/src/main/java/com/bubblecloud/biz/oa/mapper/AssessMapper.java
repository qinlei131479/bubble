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
	 * 查询指定计划下未创建考核的用户ID列表。
	 */
	List<Long> findAbnormalUsers(@Param("entid") Long entid, @Param("planId") Long planId);

}
