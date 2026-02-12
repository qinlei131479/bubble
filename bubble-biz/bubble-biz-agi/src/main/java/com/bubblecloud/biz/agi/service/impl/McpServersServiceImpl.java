package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.McpServers;
import com.bubblecloud.biz.agi.mapper.McpServersMapper;
import com.bubblecloud.biz.agi.service.McpServersService;
import org.springframework.stereotype.Service;

/**
 * MCP服务表
 *
 * @author Rampart
 * @date 2026-02-12 18:18:39
 */
@Service
public class McpServersServiceImpl extends UpServiceImpl<McpServersMapper, McpServers> implements McpServersService {

}