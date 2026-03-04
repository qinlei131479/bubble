package com.bubblecloud.biz.agi.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.agi.api.entity.Datasource;
import com.bubblecloud.agi.api.dto.DatasourceTestDTO;
import com.bubblecloud.agi.api.vo.DatasourceTestResultVO;
import com.bubblecloud.agi.api.vo.TableInfoVO;

import java.util.List;

/**
 * 数据源服务层
 *
 * @author Rampart
 * @date 2026-02-13 16:45:56
 */
public interface DatasourceService extends UpService<Datasource> {

	/**
	 * 测试数据源连接
	 *
	 * @param dto 测试连接参数
	 * @return 测试结果
	 */
	DatasourceTestResultVO testConnection(DatasourceTestDTO dto);

	/**
	 * 获取数据库表详细信息
	 *
	 * @param dto 数据源配置
	 * @return 表信息列表
	 */
	List<TableInfoVO> getTableInfo(DatasourceTestDTO dto);

	/**
	 * 同步数据源表结构到本地数据库
	 *
	 * @param dsId       数据源ID
	 * @param tableNames 选中的表名列表（为空则同步所有表）
	 */
	void syncTables(Long dsId, List<String> tableNames);

}