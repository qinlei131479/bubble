package com.bubblecloud.codegen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.codegen.entity.TableField;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper接口
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
@Mapper
public interface TableFieldMapper extends BaseMapper<TableField> {
	/**
	 * 查表的字段列表
	 *
	 * @param req
	 * @return
	 */
	List<TableField> findTableFieldListByTableName(TableField req);

	/**
	 * 查表的注释
	 *
	 * @param req
	 * @return
	 */
	String findTableComment(TableField req);
}
