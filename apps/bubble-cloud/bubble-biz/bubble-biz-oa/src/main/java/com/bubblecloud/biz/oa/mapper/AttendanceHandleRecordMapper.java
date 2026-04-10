package com.bubblecloud.biz.oa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.AttendanceHandleRecord;

/**
 * eb_attendance_handle_record。
 *
 * @author qinlei
 * @date 2026/4/7
 */
@Mapper
public interface AttendanceHandleRecordMapper extends UpMapper<AttendanceHandleRecord> {

	long countByStatisticsId(@Param("statisticsId") long statisticsId);

	List<AttendanceHandleRecord> selectPageByStatisticsId(@Param("statisticsId") long statisticsId,
			@Param("offset") long offset, @Param("limit") long limit);

}
