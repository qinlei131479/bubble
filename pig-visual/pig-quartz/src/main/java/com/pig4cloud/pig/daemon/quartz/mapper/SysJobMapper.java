package com.pig4cloud.pig.daemon.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.daemon.quartz.entity.SysJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务调度表
 *
 * @author frwcloud
 * @date 2019-01-27 10:04:42
 */
@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {

}
