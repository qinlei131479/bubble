package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.MessageMapper;
import com.bubblecloud.biz.oa.service.SystemMessageAdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Message;
import org.springframework.stereotype.Service;

/**
 * 系统消息配置实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
public class SystemMessageAdminServiceImpl extends UpServiceImpl<MessageMapper, Message> implements SystemMessageAdminService {

}
