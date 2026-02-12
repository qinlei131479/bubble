package com.bubblecloud.biz.agi.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.Supplier;

/**
 * Mapper接口：AI供应商表
 *
 * @author Rampart Qin
 * @date 2026/02/11 18:33
 */
@Mapper
public interface SupplierMapper extends UpMapper<Supplier> {
	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(Supplier req);
}
