package com.bubblecloud.biz.agi.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.agi.api.entity.SupplierModel;

/**
 * @author qinlei
 */
public interface SupplierModelService extends UpService<SupplierModel> {

	/**
	 * 获取默认向量模型（包含供应商 API 域名与 Key）。
	 *
	 * @return 默认向量模型
	 */
	SupplierModel getDefaultVectorEmbeddingModel();

}