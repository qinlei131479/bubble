package com.bubblecloud.codegen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.codegen.dto.TableFieldDTO;
import org.apache.ibatis.annotations.Mapper;

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
	 * 查表的字段列表
	 *
	 * @param req
	 * @return
	 */
	List<TableFieldDTO> findTableFieldListByTableName(TableFieldDTO req);

	/**
	 * 查表的注释
	 *
	 * @param req
	 * @return
	 */
	String findTableComment(TableFieldDTO req);
}
