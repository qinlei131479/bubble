package com.bubblecloud.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.backend.api.entity.SysDictItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统字典项数据访问接口
 *
 * @author qinlei
 * @date 2025/05/30
 */
@Mapper
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {

}
