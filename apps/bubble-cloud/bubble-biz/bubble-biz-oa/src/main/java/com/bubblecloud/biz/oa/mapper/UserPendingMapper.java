package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.UserPending;

/**
 * eb_user_pending。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Mapper
public interface UserPendingMapper extends UpMapper<UserPending> {

}
