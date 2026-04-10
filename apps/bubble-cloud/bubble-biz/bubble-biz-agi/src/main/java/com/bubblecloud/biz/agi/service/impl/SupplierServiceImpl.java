package com.bubblecloud.biz.agi.service.impl;

import org.springframework.stereotype.Service;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.Supplier;
import com.bubblecloud.biz.agi.mapper.SupplierMapper;
import com.bubblecloud.biz.agi.service.SupplierService;

/**
 * service实现类：AI供应商表
 *
 * @author Rampart Qin
 * @date   2026/02/11 18:33
 */
@Service
public class SupplierServiceImpl extends UpServiceImpl<SupplierMapper, Supplier> implements SupplierService {
}
