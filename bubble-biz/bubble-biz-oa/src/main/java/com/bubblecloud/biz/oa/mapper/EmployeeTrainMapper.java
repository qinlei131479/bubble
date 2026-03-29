package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.EmployeeTrain;

/**
 * eb_employee_train 表 Mapper（SQL 见 classpath:/mapper/EmployeeTrainMapper.xml）。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Mapper
public interface EmployeeTrainMapper extends UpMapper<EmployeeTrain> {

}
