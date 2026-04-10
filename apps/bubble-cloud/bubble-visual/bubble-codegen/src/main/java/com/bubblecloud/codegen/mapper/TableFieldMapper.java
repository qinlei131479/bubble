package com.bubblecloud.codegen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.codegen.dto.TableFieldDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
@Mapper
public interface TableFieldMapper extends BaseMapper<TableFieldDTO> {
	/**
	 * 根据表名查询字段列表
	 *
	 * @param dbName
	 * @param tableName
	 * @return
	 */
	List<TableFieldDTO> findTableFieldListByTableName(@Param("dbName") String dbName, @Param("tableName") String tableName);

	/**
	 * 查表的注释
	 *
	 * @param dbName
	 * @param tableName
	 * @return
	 */
	String findTableComment(@Param("dbName") String dbName, @Param("tableName") String tableName);
}
