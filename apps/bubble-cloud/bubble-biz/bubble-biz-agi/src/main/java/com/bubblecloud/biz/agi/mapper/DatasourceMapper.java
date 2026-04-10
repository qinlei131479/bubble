package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.Datasource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源 ORM映射层
 *
 * @author Rampart
 * @date 2026-02-13 16:45:56
 */
@Mapper
public interface DatasourceMapper extends UpMapper<Datasource> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(Datasource req);
}