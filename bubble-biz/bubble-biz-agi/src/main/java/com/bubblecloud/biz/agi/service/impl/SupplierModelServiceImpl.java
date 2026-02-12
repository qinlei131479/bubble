package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.SupplierModel;
import com.bubblecloud.biz.agi.mapper.SupplierModelMapper;
import com.bubblecloud.biz.agi.service.SupplierModelService;
import org.springframework.stereotype.Service;

/**
 * AI供应商模型表
 *
 * @author Rampart
 * @date 2026-02-12 14:04:58
 */
@Service
public class SupplierModelServiceImpl extends UpServiceImpl<SupplierModelMapper, SupplierModel> implements SupplierModelService {

}