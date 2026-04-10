package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemBackupMapper;
import com.bubblecloud.biz.oa.service.SystemBackupService;
import com.bubblecloud.oa.api.entity.SystemBackup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * {@link SystemBackupService} 实现。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Service
@RequiredArgsConstructor
public class SystemBackupServiceImpl implements SystemBackupService {

	private final SystemBackupMapper systemBackupMapper;

	@Override
	public String findLatestPathByVersion(String version) {
		var q = Wrappers.lambdaQuery(SystemBackup.class).orderByDesc(SystemBackup::getId).last("LIMIT 1");
		if (StrUtil.isNotBlank(version)) {
			q.eq(SystemBackup::getVersion, version);
		}
		SystemBackup row = systemBackupMapper.selectOne(q);
		if (ObjectUtil.isNull(row) || StrUtil.isBlank(row.getPath())) {
			return "";
		}
		return row.getPath();
	}

}
