package com.bubblecloud.biz.oa.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.AttendanceApplyRecord;

/**
 * eb_attendance_apply_record Mapper（假勤与审批结果写入考勤统计）。
 *
 * @author qinlei
 * @date 2026/4/7 15:30
 */
@Mapper
public interface AttendanceApplyRecordMapper extends UpMapper<AttendanceApplyRecord> {

	BigDecimal sumWorkHours(@Param("uid") int uid, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
			@Param("applyType") int applyType);

	long countByUidRangeType(@Param("uid") int uid, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end, @Param("applyType") int applyType);

	List<AttendanceApplyRecord> selectLeaveRecordsInMonth(@Param("uid") int uid, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	/**
	 * 与 PHP {@code AttendanceApplyRecordDao::search} 中 compare_time 条件一致：时段覆盖打卡点。
	 */
	List<AttendanceApplyRecord> selectCoveringInstant(@Param("uid") int uid,
			@Param("compareTime") LocalDateTime compareTime);

	long countByApplyId(@Param("applyId") long applyId);

	List<AttendanceApplyRecord> selectByCompareTimeAndTypes(@Param("compareTime") LocalDateTime compareTime,
			@Param("types") List<Integer> types);

}
