package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.AttendanceGroup;

/**
 * eb_attendance_group Mapper（分页 SQL 见 classpath:/mapper/AttendanceGroupMapper.xml）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Mapper
public interface AttendanceGroupMapper extends UpMapper<AttendanceGroup> {

}
