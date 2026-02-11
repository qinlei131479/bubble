package com.bubblecloud.biz.agi.service.impl;

import org.springframework.stereotype.Service;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.Terminology;
import com.bubblecloud.biz.agi.mapper.TerminologyMapper;
import com.bubblecloud.biz.agi.service.TerminologyService;

/**
 * service实现类：术语表
 *
 * @author Rampart Qin
 * @date   2026/02/11 22:35
 */
@Service
public class TerminologyServiceImpl extends UpServiceImpl<TerminologyMapper, Terminology> implements TerminologyService {
}
