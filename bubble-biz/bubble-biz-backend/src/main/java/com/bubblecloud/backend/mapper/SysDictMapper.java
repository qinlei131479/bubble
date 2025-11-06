package com.bubblecloud.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.backend.api.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典表 Mapper 接口
 *
 * @author qinlei
 * @date 2025/06/27
 */
@Mapper
public interface SysDictMapper extends BaseMapper<SysDict> {

}
