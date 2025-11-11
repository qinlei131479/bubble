package com.bubblecloud.biz.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.backend.api.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志表 Mapper 接口
 *
 * @author qinlei
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {

}
