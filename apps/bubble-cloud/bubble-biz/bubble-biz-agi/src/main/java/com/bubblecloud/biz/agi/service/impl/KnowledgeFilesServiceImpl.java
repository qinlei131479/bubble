package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.KnowledgeFiles;
import com.bubblecloud.biz.agi.mapper.KnowledgeFilesMapper;
import com.bubblecloud.biz.agi.service.KnowledgeFilesService;
import org.springframework.stereotype.Service;

/**
 * 知识库文件
 *
 * @author Rampart
 * @date 2026-02-13 09:33:20
 */
@Service
public class KnowledgeFilesServiceImpl extends UpServiceImpl<KnowledgeFilesMapper, KnowledgeFiles> implements KnowledgeFilesService {

}