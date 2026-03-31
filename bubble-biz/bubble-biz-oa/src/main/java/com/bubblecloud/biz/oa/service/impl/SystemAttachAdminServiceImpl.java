package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.SystemAttachMapper;
import com.bubblecloud.biz.oa.service.SystemAttachAdminService;
import com.bubblecloud.oa.api.entity.SystemAttach;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统附件实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
@RequiredArgsConstructor
public class SystemAttachAdminServiceImpl implements SystemAttachAdminService {

	private final SystemAttachMapper systemAttachMapper;

	@Override
	public Page<SystemAttach> pageList(int entid, int page, int limit) {
		var q = Wrappers.lambdaQuery(SystemAttach.class)
			.eq(SystemAttach::getEntid, entid)
			.orderByDesc(SystemAttach::getId);
		return systemAttachMapper.selectPage(new Page<>(page, limit), q);
	}

}
