package com.bubblecloud.biz.oa.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.biz.oa.attendance.AttendanceStatisticsSearchQuery;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.AttendanceStatistics;

/**
 * eb_attendance_statistics。
 *
 * @author qinlei
 * @date 2026/4/7
 */
@Mapper
public interface AttendanceStatisticsMapper extends UpMapper<AttendanceStatistics> {

	long countDailySearch(@Param("q") AttendanceStatisticsSearchQuery q);

	List<AttendanceStatistics> selectDailySearch(@Param("q") AttendanceStatisticsSearchQuery q,
			@Param("offset") long offset, @Param("limit") long limit);

	long countDistinctUidMonthly(@Param("q") AttendanceStatisticsSearchQuery q);

	List<AttendanceStatistics> selectMonthlyRows(@Param("q") AttendanceStatisticsSearchQuery q,
			@Param("offset") long offset, @Param("limit") long limit);

	BigDecimal avgActualWorkHours(@Param("uid") int uid, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	long countNormalDays(@Param("uid") int uid, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	long countAnyShiftStatusInRange(@Param("uid") int uid, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end, @Param("statuses") List<Integer> statuses);

	List<AttendanceStatistics> selectByUidAndRange(@Param("uid") int uid, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	List<AttendanceStatistics> selectByUidAndMonth(@Param("uid") int uid, @Param("monthYm") String monthYm);

	List<AttendanceStatistics> selectAbnormalCandidates(@Param("uid") int uid,
			@Param("q") AttendanceStatisticsSearchQuery q);

}
