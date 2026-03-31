package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.SystemQuickMapper;
import com.bubblecloud.biz.oa.service.QuickAdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.SystemQuick;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 快捷入口实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
public class QuickAdminServiceImpl extends UpServiceImpl<SystemQuickMapper, SystemQuick> implements QuickAdminService {

	@Override
	public Page<SystemQuick> page(Integer cid, String nameLike, int current, int size) {
		var q = Wrappers.lambdaQuery(SystemQuick.class)
			.orderByDesc(SystemQuick::getSort)
			.orderByAsc(SystemQuick::getId);
		if (ObjectUtil.isNotNull(cid) && cid > 0) {
			q.eq(SystemQuick::getCid, cid);
		}
		if (StrUtil.isNotBlank(nameLike)) {
			q.like(SystemQuick::getName, nameLike);
		}
		return baseMapper.selectPage(new Page<>(current, size), q);
	}

	@Override
	public List<SystemQuick> listAll(Integer cid) {
		var q = Wrappers.lambdaQuery(SystemQuick.class).orderByDesc(SystemQuick::getSort);
		if (ObjectUtil.isNotNull(cid) && cid > 0) {
			q.eq(SystemQuick::getCid, cid);
		}
		return baseMapper.selectList(q);
	}

	@Override
	public SystemQuick getQuick(int id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveQuick(SystemQuick row) {
		LocalDateTime now = LocalDateTime.now();
		row.setCreatedAt(now);
		row.setUpdatedAt(now);
		baseMapper.insert(row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateQuick(SystemQuick row) {
		row.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteQuick(int id) {
		baseMapper.deleteById(id);
	}

}
