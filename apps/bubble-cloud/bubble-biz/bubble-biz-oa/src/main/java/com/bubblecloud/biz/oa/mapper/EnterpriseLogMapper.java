package com.bubblecloud.biz.oa.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.EnterpriseLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * eb_enterprise_log_* 分表 Mapper（默认 eb_enterprise_log_1）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Mapper
public interface EnterpriseLogMapper extends UpMapper<EnterpriseLog> {

}
