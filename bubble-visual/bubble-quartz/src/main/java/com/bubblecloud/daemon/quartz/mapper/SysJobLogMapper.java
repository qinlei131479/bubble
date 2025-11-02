package com.bubblecloud.daemon.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.daemon.quartz.entity.SysJobLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务执行日志表
 *
 * @author frwcloud
 * @date 2019-01-27 13:40:20
 */
@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

}
