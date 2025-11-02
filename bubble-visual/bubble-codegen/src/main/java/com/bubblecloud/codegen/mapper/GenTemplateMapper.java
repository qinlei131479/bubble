package com.bubblecloud.codegen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.codegen.entity.GenTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 模板
 *
 * @author PIG
 * @date 2023-02-21 17:15:44
 */
@Mapper
public interface GenTemplateMapper extends BaseMapper<GenTemplateEntity> {

	/**
	 * 根据groupId查询 模板
	 * @param groupId
	 * @return
	 */
	List<GenTemplateEntity> listTemplateById(Long groupId);

}
