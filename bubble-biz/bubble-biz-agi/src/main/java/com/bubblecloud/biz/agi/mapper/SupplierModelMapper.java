package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.SupplierModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qinlei
 */
@Mapper
public interface SupplierModelMapper extends UpMapper<SupplierModel> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(SupplierModel req);
}