package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.Datasource;
import com.bubblecloud.biz.agi.mapper.DatasourceMapper;
import com.bubblecloud.biz.agi.service.DatasourceService;
import org.springframework.stereotype.Service;

/**
 * 数据源
 *
 * @author Rampart
 * @date 2026-02-13 16:45:56
 */
@Service
public class DatasourceServiceImpl extends UpServiceImpl<DatasourceMapper, Datasource> implements DatasourceService {

}