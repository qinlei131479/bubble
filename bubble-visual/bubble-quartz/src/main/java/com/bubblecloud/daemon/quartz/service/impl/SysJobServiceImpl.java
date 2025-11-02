package com.bubblecloud.daemon.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubblecloud.daemon.quartz.mapper.SysJobMapper;
import com.bubblecloud.daemon.quartz.service.SysJobService;
import com.bubblecloud.daemon.quartz.entity.SysJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 定时任务调度表
 *
 * @author frwcloud
 * @date 2019-01-27 10:04:42
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements SysJobService {

}
