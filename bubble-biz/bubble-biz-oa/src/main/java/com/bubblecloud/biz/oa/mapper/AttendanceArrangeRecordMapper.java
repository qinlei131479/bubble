package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.AttendanceArrangeRecord;

/**
 * eb_attendance_arrange_record Mapper。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Mapper
public interface AttendanceArrangeRecordMapper extends UpMapper<AttendanceArrangeRecord> {

	/**
	 * 将指定班次在未来的排班记录逻辑删除（对齐 PHP clearFutureArrangeByShiftId）。
	 */
	int logicDeleteFutureByShiftId(@Param("shiftId") Integer shiftId, @Param("afterDate") String afterDate);

}
