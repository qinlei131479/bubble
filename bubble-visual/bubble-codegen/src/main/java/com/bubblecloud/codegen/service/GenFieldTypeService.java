package com.bubblecloud.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubblecloud.codegen.entity.GenFieldType;

import java.util.Set;

/**
 * 列属性
 *
 * @author pigx code generator
 * @date 2023-02-06 20:16:01
 */
public interface GenFieldTypeService extends IService<GenFieldType> {

	/**
	 * 根据tableId，获取包列表
	 * @param dsName 数据源名称
	 * @param tableName 表名称
	 * @return 返回包列表
	 */
	Set<String> getPackageByTableId(String dsName, String tableName);

}
