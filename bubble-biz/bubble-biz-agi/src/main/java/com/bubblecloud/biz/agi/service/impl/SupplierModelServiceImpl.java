package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.agi.api.entity.Supplier;
import com.bubblecloud.biz.agi.mapper.SupplierMapper;
import com.bubblecloud.common.core.constant.enums.FlagEnum;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.SupplierModel;
import com.bubblecloud.biz.agi.mapper.SupplierModelMapper;
import com.bubblecloud.biz.agi.service.SupplierModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * AI供应商模型表
 *
 * @author Rampart
 * @date 2026-02-12 14:04:58
 */
@Service
@RequiredArgsConstructor
public class SupplierModelServiceImpl extends UpServiceImpl<SupplierModelMapper, SupplierModel> implements SupplierModelService {

	private final SupplierMapper supplierMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(SupplierModel req) {
		Supplier supplier = supplierMapper.selectById(req.getSupplierId());
		if (Objects.isNull(supplier)) {
			return R.failed("供应商不存在");
		}
		Long count = this.lambdaQuery().eq(SupplierModel::getSupplierId, req.getSupplierId()).eq(SupplierModel::getName, req.getName()).count();
		if (count > 0) {
			return R.failed("供应商模型名称已存在");
		}
		supplier.setApiKey(req.getApiKey());
		supplier.setApiDomain(req.getApiDomain());
		supplierMapper.updateById(supplier);
		req.setDefaultFlag(FlagEnum.NO.getCode());
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(SupplierModel req) {
		SupplierModel model = baseMapper.selectById(req.getId());
		if (Objects.isNull(model)) {
			return R.failed("供应商模型不存在");
		}
		Long count = this.lambdaQuery().eq(SupplierModel::getSupplierId, req.getSupplierId())
				.eq(SupplierModel::getName, req.getName()).ne(SupplierModel::getId, req.getId()).count();
		if (count > 0) {
			return R.failed("供应商模型名称已存在");
		}
		Supplier supplier = supplierMapper.selectById(req.getSupplierId());
		if (Objects.isNull(supplier)) {
			return R.failed("供应商不存在");
		}
		supplier.setApiKey(req.getApiKey());
		supplier.setApiDomain(req.getApiDomain());
		supplierMapper.updateById(supplier);

		return super.update(req);
	}
}