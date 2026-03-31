package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.UserResume;

/**
 * eb_user_resume。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Mapper
public interface UserResumeMapper extends UpMapper<UserResume> {

}
