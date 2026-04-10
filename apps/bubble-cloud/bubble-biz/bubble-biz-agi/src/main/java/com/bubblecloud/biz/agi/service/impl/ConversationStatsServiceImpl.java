package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.ConversationStats;
import com.bubblecloud.biz.agi.mapper.ConversationStatsMapper;
import com.bubblecloud.biz.agi.service.ConversationStatsService;
import org.springframework.stereotype.Service;

/**
 * 对话统计
 *
 * @author rampart
 * @date 2026-02-13 09:38:18
 */
@Service
public class ConversationStatsServiceImpl extends UpServiceImpl<ConversationStatsMapper, ConversationStats> implements ConversationStatsService {

}