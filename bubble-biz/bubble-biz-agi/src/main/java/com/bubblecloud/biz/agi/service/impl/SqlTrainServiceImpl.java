package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.SqlTrain;
import com.bubblecloud.biz.agi.mapper.SqlTrainMapper;
import com.bubblecloud.biz.agi.service.SqlTrainService;
import org.springframework.stereotype.Service;

/**
 * SQL训练示例
 *
 * @author Rampart
 * @date 2026-02-12 18:37:03
 */
@Service
public class SqlTrainServiceImpl extends UpServiceImpl<SqlTrainMapper, SqlTrain> implements SqlTrainService {

}