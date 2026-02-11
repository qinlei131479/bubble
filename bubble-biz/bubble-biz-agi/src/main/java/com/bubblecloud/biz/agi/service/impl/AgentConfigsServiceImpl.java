package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.agi.api.entity.AgentConfigs;
import com.bubblecloud.biz.agi.mapper.AgentConfigsMapper;
import com.bubblecloud.biz.agi.service.AgentConfigsService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 智能体配置表
 *
 * @author rampart
 * @date 2026-02-11 10:56:22
 */
@Service
public class AgentConfigsServiceImpl extends UpServiceImpl<AgentConfigsMapper, AgentConfigs> implements AgentConfigsService {

}
