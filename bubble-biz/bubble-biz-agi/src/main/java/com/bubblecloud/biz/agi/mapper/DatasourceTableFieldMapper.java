package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.DatasourceTableField;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源表字段 ORM映射层
 *
 * @author Rampart
 * @date 2026-02-13 16:48:54
 */
@Mapper
public interface DatasourceTableFieldMapper extends UpMapper<DatasourceTableField> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(DatasourceTableField req);
}