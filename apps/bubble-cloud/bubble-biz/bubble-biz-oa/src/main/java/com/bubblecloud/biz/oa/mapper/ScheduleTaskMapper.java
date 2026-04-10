package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.ScheduleTask;

/**
 * eb_schedule_task Mapper。
 *
 * @author qinlei
 * @date 2026/4/6 10:00
 */
@Mapper
public interface ScheduleTaskMapper extends UpMapper<ScheduleTask> {

}
