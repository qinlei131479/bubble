package com.pig4cloud.pig.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.admin.api.entity.SysPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 岗位信息表 Mapper 接口
 *
 * @author qinlei
 * @date 2025/06/27
 */
@Mapper
public interface SysPostMapper extends BaseMapper<SysPost> {

	/**
	 * 通过用户ID，查询岗位信息
	 * @param userId 用户id
	 * @return 岗位信息
	 */
	List<SysPost> listPostsByUserId(Long userId);

}
