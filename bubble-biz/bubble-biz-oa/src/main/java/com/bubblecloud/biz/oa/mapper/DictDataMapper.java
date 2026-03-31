package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.DictData;

/**
 * eb_dict_data 表 Mapper。
 *
 * @author qinlei
 * @date 2026/3/30 下午10:10
 */
@Mapper
public interface DictDataMapper extends UpMapper<DictData> {

}
