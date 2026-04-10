package com.bubblecloud.biz.oa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.biz.oa.attendance.AttendanceClockRecordSearchQuery;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.AttendanceClockRecord;

/**
 * eb_attendance_clock_record。
 *
 * @author qinlei
 * @date 2026/4/7
 */
@Mapper
public interface AttendanceClockRecordMapper extends UpMapper<AttendanceClockRecord> {

	long countSearch(@Param("q") AttendanceClockRecordSearchQuery q);

	List<AttendanceClockRecord> selectSearch(@Param("q") AttendanceClockRecordSearchQuery q,
			@Param("offset") long offset, @Param("limit") long limit);

}
