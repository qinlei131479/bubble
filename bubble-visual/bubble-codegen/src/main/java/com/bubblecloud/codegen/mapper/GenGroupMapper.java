package com.bubblecloud.codegen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.codegen.entity.GenGroupEntity;
import com.bubblecloud.codegen.util.vo.GroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 模板分组 Mapper 接口
 *
 * @author qinlei
 * @date 2025/05/31
 */
@Mapper
public interface GenGroupMapper extends BaseMapper<GenGroupEntity> {

	/**
	 * 根据ID获取分组VO对象
	 * @param id 分组ID
	 * @return 分组VO对象
	 */
	GroupVO getGroupVoById(@Param("id") Long id);

}
