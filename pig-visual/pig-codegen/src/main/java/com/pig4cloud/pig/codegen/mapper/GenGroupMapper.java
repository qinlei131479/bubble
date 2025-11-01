package com.pig4cloud.pig.codegen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.codegen.entity.GenGroupEntity;
import com.pig4cloud.pig.codegen.util.vo.GroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 模板分组
 *
 * @author PIG
 * @date 2023-02-21 20:01:53
 */
@Mapper
public interface GenGroupMapper extends BaseMapper<GenGroupEntity> {

	GroupVO getGroupVoById(@Param("id") Long id);

}
