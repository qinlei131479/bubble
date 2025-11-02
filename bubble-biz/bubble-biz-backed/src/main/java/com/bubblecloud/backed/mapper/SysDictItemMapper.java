package com.bubblecloud.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.backed.api.entity.SysDictItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典项
 *
 * @author lengleng
 * @date 2019/03/19
 */
@Mapper
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {

}
