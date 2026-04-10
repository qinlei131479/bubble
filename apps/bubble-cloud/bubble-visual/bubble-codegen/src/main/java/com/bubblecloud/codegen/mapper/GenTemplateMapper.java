package com.bubblecloud.codegen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.codegen.entity.GenTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 代码生成模板Mapper接口
 *
 * @author qinlei
 * @date 2025/05/31
 */
@Mapper
public interface GenTemplateMapper extends BaseMapper<GenTemplateEntity> {

	/**
	 * 根据模板组ID查询模板列表
	 * @param groupId 模板组ID
	 * @return 模板实体列表
	 */
	List<GenTemplateEntity> listTemplateById(Long groupId);

}
