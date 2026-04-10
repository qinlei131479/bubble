package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.SystemCity;

/**
 * eb_system_city 省市区。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Mapper
public interface SystemCityMapper extends UpMapper<SystemCity> {

}
