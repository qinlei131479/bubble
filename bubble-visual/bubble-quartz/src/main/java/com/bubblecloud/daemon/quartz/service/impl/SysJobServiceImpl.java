package com.bubblecloud.daemon.quartz.service.impl;

import com.bubblecloud.daemon.quartz.entity.SysJob;
import com.bubblecloud.daemon.quartz.mapper.SysJobMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubblecloud.daemon.quartz.service.SysJobService;

import lombok.AllArgsConstructor;

/**
 * 定时任务调度服务实现类
 *
 * @author qinlei
 * @date 2025/05/31
 */
@Service
@AllArgsConstructor
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements SysJobService {

}
