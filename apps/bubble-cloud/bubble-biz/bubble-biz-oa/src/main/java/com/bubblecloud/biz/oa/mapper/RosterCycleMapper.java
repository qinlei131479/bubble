package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.RosterCycle;

/**
 * eb_roster_cycle Mapper。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Mapper
public interface RosterCycleMapper extends UpMapper<RosterCycle> {

	/**
	 * 物理删除周期（对齐 PHP forceDelete）。
	 */
	void physicalDeleteById(@Param("id") Integer id);

}
