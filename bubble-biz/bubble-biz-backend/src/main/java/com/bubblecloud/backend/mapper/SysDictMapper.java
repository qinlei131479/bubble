package com.bubblecloud.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.api.backend.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @author lengleng
 * @since 2017-11-19
 */
@Mapper
public interface SysDictMapper extends BaseMapper<SysDict> {

}
