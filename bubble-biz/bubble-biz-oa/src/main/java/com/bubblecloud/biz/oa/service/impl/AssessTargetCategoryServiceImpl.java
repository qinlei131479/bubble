package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AssessTargetCategoryMapper;
import com.bubblecloud.biz.oa.service.AssessTargetCategoryService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.AssessTargetCateSaveDTO;
import com.bubblecloud.oa.api.entity.AssessTargetCategory;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 绩效指标分类服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class AssessTargetCategoryServiceImpl extends UpServiceImpl<AssessTargetCategoryMapper, AssessTargetCategory>
		implements AssessTargetCategoryService {

	private static final String NOT_EXISTS = "common.operation.noExists";

	@Override
	public SimplePageVO pageTargetCate(Pg<AssessTargetCategory> pg, AssessTargetCategory query) {
		Page<AssessTargetCategory> r = findPg(pg, query);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createTargetCate(AssessTargetCateSaveDTO dto) {
		AssessTargetCategory entity = new AssessTargetCategory();
		entity.setName(dto.getName());
		entity.setTypes(dto.getTypes());
		entity.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		entity.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		save(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTargetCate(Long id, AssessTargetCateSaveDTO dto) {
		AssessTargetCategory existing = getById(id);
		assertExists(existing);
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (StrUtil.isNotBlank(dto.getTypes())) {
			existing.setTypes(dto.getTypes());
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
	public void removeTargetCate(Long id) {
		assertExists(getById(id));
		removeById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(AssessTargetCategory req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(AssessTargetCategory req) {
		return super.update(req);
	}

	private void assertExists(AssessTargetCategory entity) {
		if (ObjectUtil.isNull(entity) || ObjectUtil.isNotNull(entity.getDeletedAt())) {
			throw new IllegalArgumentException(NOT_EXISTS);
		}
	}

}
