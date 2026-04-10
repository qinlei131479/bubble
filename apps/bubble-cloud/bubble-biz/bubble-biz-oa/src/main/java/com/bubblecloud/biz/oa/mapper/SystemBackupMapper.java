package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.SystemBackup;

/**
 * eb_system_backup 备份记录。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Mapper
public interface SystemBackupMapper extends UpMapper<SystemBackup> {

}
