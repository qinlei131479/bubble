package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.MessageCategory;

/**
 * eb_message_category。
 * @author qinlei
 */
@Mapper
public interface MessageCategoryMapper extends UpMapper<MessageCategory> {

}
