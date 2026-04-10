package com.bubblecloud.biz.oa.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.Enterprise;
import org.apache.ibatis.annotations.Mapper;

/**
 * eb_enterprise 表 Mapper。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Mapper
public interface EnterpriseMapper extends UpMapper<Enterprise> {

}
