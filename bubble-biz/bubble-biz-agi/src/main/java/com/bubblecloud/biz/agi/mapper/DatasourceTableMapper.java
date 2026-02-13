package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.DatasourceTable;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源授权 ORM映射层
 *
 * @author Rampart
 * @date 2026-02-13 16:47:36
 */
@Mapper
public interface DatasourceTableMapper extends UpMapper<DatasourceTable> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(DatasourceTable req);
}