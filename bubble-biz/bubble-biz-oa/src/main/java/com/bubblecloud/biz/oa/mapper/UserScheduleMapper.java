package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.UserSchedule;

/**
 * eb_user_schedule Mapper。
 *
 * @author qinlei
 * @date 2026/3/29 20:35
 */
@Mapper
public interface UserScheduleMapper extends UpMapper<UserSchedule> {

}
