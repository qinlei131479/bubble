package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.Conversations;
import com.bubblecloud.biz.agi.mapper.ConversationsMapper;
import com.bubblecloud.biz.agi.service.ConversationsService;
import org.springframework.stereotype.Service;

/**
 * 对话
 *
 * @author Rampart
 * @date 2026-02-13 09:36:42
 */
@Service
public class ConversationsServiceImpl extends UpServiceImpl<ConversationsMapper, Conversations> implements ConversationsService {

}