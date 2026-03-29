package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.EnterpriseUserSalary;

/**
 * eb_enterprise_user_salary 表 Mapper（SQL 见 classpath:/mapper/EnterpriseUserSalaryMapper.xml）。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Mapper
public interface EnterpriseUserSalaryMapper extends UpMapper<EnterpriseUserSalary> {

}
