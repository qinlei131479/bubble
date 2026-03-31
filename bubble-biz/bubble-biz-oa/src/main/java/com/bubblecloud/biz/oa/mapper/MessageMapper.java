package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.Message;

/**
 * eb_message Mapper。
 *
 * @author qinlei
 * @date 2026/3/29 20:35
 */
@Mapper
public interface MessageMapper extends UpMapper<Message> {

}
