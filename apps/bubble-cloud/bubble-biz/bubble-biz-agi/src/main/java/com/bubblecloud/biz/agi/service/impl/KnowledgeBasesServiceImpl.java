package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.KnowledgeBases;
import com.bubblecloud.biz.agi.mapper.KnowledgeBasesMapper;
import com.bubblecloud.biz.agi.service.KnowledgeBasesService;
import org.springframework.stereotype.Service;

/**
 * 知识库
 *
 * @author Rampart
 * @date 2026-02-13 09:30:12
 */
@Service
public class KnowledgeBasesServiceImpl extends UpServiceImpl<KnowledgeBasesMapper, KnowledgeBases> implements KnowledgeBasesService {

}