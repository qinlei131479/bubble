package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AssessTargetMapper;
import com.bubblecloud.biz.oa.service.AssessTargetService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.AssessTargetSaveDTO;
import com.bubblecloud.oa.api.entity.AssessTarget;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 绩效指标服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class AssessTargetServiceImpl extends UpServiceImpl<AssessTargetMapper, AssessTarget>
		implements AssessTargetService {

	private static final String NOT_EXISTS = "common.operation.noExists";

	@Override
	public SimplePageVO pageTarget(Pg<AssessTarget> pg, AssessTarget query) {
		Page<AssessTarget> r = findPg(pg, query);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createTarget(AssessTargetSaveDTO dto) {
		AssessTarget entity = new AssessTarget();
		entity.setCateId(dto.getCateId());
		entity.setName(dto.getName());
		entity.setContent(dto.getContent());
		entity.setTypes(dto.getTypes());
		entity.setWeight(ObjectUtil.defaultIfNull(dto.getWeight(), 0));
		entity.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		entity.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		save(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTarget(Long id, AssessTargetSaveDTO dto) {
		AssessTarget existing = getById(id);
		assertExists(existing);
		if (ObjectUtil.isNotNull(dto.getCateId())) {
			existing.setCateId(dto.getCateId());
		}
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (StrUtil.isNotBlank(dto.getContent())) {
			existing.setContent(dto.getContent());
		}
		if (StrUtil.isNotBlank(dto.getTypes())) {
			existing.setTypes(dto.getTypes());
		}
		if (ObjectUtil.isNotNull(dto.getWeight())) {
			existing.setWeight(dto.getWeight());
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
	public void removeTarget(Long id) {
		assertExists(getById(id));
		removeById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(AssessTarget req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(AssessTarget req) {
		return super.update(req);
	}

	private void assertExists(AssessTarget entity) {
		if (ObjectUtil.isNull(entity) || ObjectUtil.isNotNull(entity.getDeletedAt())) {
			throw new IllegalArgumentException(NOT_EXISTS);
		}
	}

}
