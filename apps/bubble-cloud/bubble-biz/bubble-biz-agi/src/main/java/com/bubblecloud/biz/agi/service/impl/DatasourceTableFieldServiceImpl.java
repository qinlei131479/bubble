package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.DatasourceTableField;
import com.bubblecloud.biz.agi.mapper.DatasourceTableFieldMapper;
import com.bubblecloud.biz.agi.service.DatasourceTableFieldService;
import org.springframework.stereotype.Service;

/**
 * 数据源表字段
 *
 * @author Rampart
 * @date 2026-02-13 16:48:54
 */
@Service
public class DatasourceTableFieldServiceImpl extends UpServiceImpl<DatasourceTableFieldMapper, DatasourceTableField> implements DatasourceTableFieldService {

}