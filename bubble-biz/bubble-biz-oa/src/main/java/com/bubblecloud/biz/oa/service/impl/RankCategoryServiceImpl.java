package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.RankCategoryMapper;
import com.bubblecloud.biz.oa.service.RankCategoryService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.RankCateSaveDTO;
import com.bubblecloud.oa.api.entity.RankCategory;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 职级体系分类服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class RankCategoryServiceImpl extends UpServiceImpl<RankCategoryMapper, RankCategory>
		implements RankCategoryService {

	private static final String NOT_EXISTS = "common.operation.noExists";

	@Override
	public SimplePageVO pageRankCate(Pg<RankCategory> pg, RankCategory query) {
		Page<RankCategory> r = findPg(pg, query);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createRankCate(RankCateSaveDTO dto) {
		RankCategory entity = new RankCategory();
		entity.setName(dto.getName());
		entity.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		entity.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		save(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRankCate(long id, RankCateSaveDTO dto) {
		RankCategory existing = getById(id);
		assertExists(existing);
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (ObjectUtil.isNotNull(dto.getSort())) {
			existing.setSort(dto.getSort());
		}
		if (ObjectUtil.isNotNull(dto.getStatus())) {
			existing.setStatus(dto.getStatus());
		}
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeRankCate(long id) {
		assertExists(getById(id));
		removeById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(RankCategory req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(RankCategory req) {
		return super.update(req);
	}

	private void assertExists(RankCategory entity) {
		if (ObjectUtil.isNull(entity) || ObjectUtil.isNotNull(entity.getDeletedAt())) {
			throw new IllegalArgumentException(NOT_EXISTS);
		}
	}

}
