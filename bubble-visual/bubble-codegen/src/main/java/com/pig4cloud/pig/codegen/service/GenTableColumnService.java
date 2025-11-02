package com.pig4cloud.pig.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.codegen.entity.GenTableColumnEntity;

import java.util.List;

/**
 * 列属性
 *
 * @author pigx code generator
 * @date 2023-02-06 20:16:01
 */
public interface GenTableColumnService extends IService<GenTableColumnEntity> {

	void initFieldList(List<GenTableColumnEntity> tableFieldList);

	void updateTableField(String dsName, String tableName, List<GenTableColumnEntity> tableFieldList);

}
