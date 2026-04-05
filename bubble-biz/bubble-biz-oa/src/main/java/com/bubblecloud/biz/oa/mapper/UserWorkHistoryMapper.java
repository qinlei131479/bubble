package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.UserWorkHistory;

@Mapper
public interface UserWorkHistoryMapper extends UpMapper<UserWorkHistory> {

}
