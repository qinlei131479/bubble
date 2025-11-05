package com.pig4cloud.pig.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.codegen.entity.GenTableColumnEntity;

import java.util.List;

/**
 * 代码生成表列服务接口
 *
 * @author qinlei
 * @date 2025/05/31
 */
public interface GenTableColumnService extends IService<GenTableColumnEntity> {

	/**
	 * 初始化字段列表
	 * @param tableFieldList 表字段实体列表
	 */
	void initFieldList(List<GenTableColumnEntity> tableFieldList);

	/**
	 * 更新表字段信息
	 * @param dsName 数据源名称
	 * @param tableName 表名
	 * @param tableFieldList 表字段列表
	 */
	void updateTableField(String dsName, String tableName, List<GenTableColumnEntity> tableFieldList);

}
