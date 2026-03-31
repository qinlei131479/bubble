package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.SystemQuickMapper;
import com.bubblecloud.biz.oa.service.QuickAdminService;
import com.bubblecloud.oa.api.entity.SystemQuick;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 快捷入口实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
public class QuickAdminServiceImpl implements QuickAdminService {

	private final SystemQuickMapper systemQuickMapper;

	@Override
	public Page<SystemQuick> page(Integer cid, String nameLike, int current, int size) {
		var q = Wrappers.lambdaQuery(SystemQuick.class)
			.orderByDesc(SystemQuick::getSort)
			.orderByAsc(SystemQuick::getId);
		if (cid != null && cid > 0) {
			q.eq(SystemQuick::getCid, cid);
		}
		if (StringUtils.hasText(nameLike)) {
			q.like(SystemQuick::getName, nameLike);
		}
		return systemQuickMapper.selectPage(new Page<>(current, size), q);
	}

	@Override
	public List<SystemQuick> listAll(Integer cid) {
		var q = Wrappers.lambdaQuery(SystemQuick.class).orderByDesc(SystemQuick::getSort);
		if (cid != null && cid > 0) {
			q.eq(SystemQuick::getCid, cid);
		}
		return systemQuickMapper.selectList(q);
	}

	@Override
	public SystemQuick get(int id) {
		return systemQuickMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(SystemQuick row) {
		LocalDateTime now = LocalDateTime.now();
		row.setCreatedAt(now);
		row.setUpdatedAt(now);
		systemQuickMapper.insert(row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SystemQuick row) {
		row.setUpdatedAt(LocalDateTime.now());
		systemQuickMapper.updateById(row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(int id) {
		systemQuickMapper.deleteById(id);
	}

}
