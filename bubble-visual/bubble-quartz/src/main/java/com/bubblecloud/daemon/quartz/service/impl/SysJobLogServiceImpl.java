package com.bubblecloud.daemon.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubblecloud.daemon.quartz.mapper.SysJobLogMapper;
import com.bubblecloud.daemon.quartz.entity.SysJobLog;
import com.bubblecloud.daemon.quartz.service.SysJobLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 定时任务执行日志表
 *
 * @author frwcloud
 * @date 2019-01-27 13:40:20
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogService {

}
