package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.RosterCycleShift;

/**
 * eb_roster_cycle_shift Mapper。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Mapper
public interface RosterCycleShiftMapper extends UpMapper<RosterCycleShift> {

	/**
	 * 物理删除周期下关联班次（对齐 PHP forceDelete）。
	 */
	void physicalDeleteByCycleId(@Param("cycleId") Integer cycleId);

}
