package com.bubblecloud.daemon.quartz.service.impl;

import com.bubblecloud.daemon.quartz.entity.SysJobLog;
import com.bubblecloud.daemon.quartz.mapper.SysJobLogMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubblecloud.daemon.quartz.service.SysJobLogService;

import lombok.AllArgsConstructor;

/**
 * 定时任务执行日志服务实现类
 *
 * @author qinlei
 * @date 2025/05/31
 */
@Service
@AllArgsConstructor
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogService {

}
