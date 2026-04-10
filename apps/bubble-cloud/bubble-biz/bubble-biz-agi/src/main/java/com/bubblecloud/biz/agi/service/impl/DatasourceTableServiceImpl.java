package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.DatasourceTable;
import com.bubblecloud.biz.agi.mapper.DatasourceTableMapper;
import com.bubblecloud.biz.agi.service.DatasourceTableService;
import org.springframework.stereotype.Service;

/**
 * 数据源授权
 *
 * @author Rampart
 * @date 2026-02-13 16:47:36
 */
@Service
public class DatasourceTableServiceImpl extends UpServiceImpl<DatasourceTableMapper, DatasourceTable> implements DatasourceTableService {

}