package com.bubblecloud.biz.oa.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AssessTemplateMapper;
import com.bubblecloud.biz.oa.service.AssessTemplateService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.AssessTemplateSaveDTO;
import com.bubblecloud.oa.api.entity.AssessTemplate;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 绩效考核模板服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class AssessTemplateServiceImpl extends UpServiceImpl<AssessTemplateMapper, AssessTemplate>
		implements AssessTemplateService {

	private static final String NOT_EXISTS = "common.operation.noExists";

	@Override
	public SimplePageVO pageTemplate(Pg<AssessTemplate> pg, AssessTemplate query) {
		Page<AssessTemplate> r = findPg(pg, query);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createTemplate(AssessTemplateSaveDTO dto) {
		AssessTemplate entity = new AssessTemplate();
		entity.setName(dto.getName());
		entity.setTypes(dto.getTypes());
		entity.setCover(dto.getCover());
		entity.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		entity.setIsFavorite(0);
		if (CollUtil.isNotEmpty(dto.getTargetIds())) {
			entity.setTargetIds(dto.getTargetIds().stream()
					.map(String::valueOf).collect(Collectors.joining(",", "[", "]")));
		}
		save(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTemplate(Long id, AssessTemplateSaveDTO dto) {
		AssessTemplate existing = getById(id);
		assertExists(existing);
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (StrUtil.isNotBlank(dto.getTypes())) {
			existing.setTypes(dto.getTypes());
		}
		if (StrUtil.isNotBlank(dto.getCover())) {
			existing.setCover(dto.getCover());
		}
		if (ObjectUtil.isNotNull(dto.getStatus())) {
			existing.setStatus(dto.getStatus());
		}
		if (CollUtil.isNotEmpty(dto.getTargetIds())) {
			existing.setTargetIds(dto.getTargetIds().stream()
					.map(String::valueOf).collect(Collectors.joining(",", "[", "]")));
		}
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeTemplate(Long id) {
		assertExists(getById(id));
		removeById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void toggleFavorite(Long id) {
		AssessTemplate existing = getById(id);
		assertExists(existing);
		int cur = ObjectUtil.defaultIfNull(existing.getIsFavorite(), 0);
		existing.setIsFavorite(cur == 1 ? 0 : 1);
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setCover(Long id, String cover) {
		AssessTemplate existing = getById(id);
		assertExists(existing);
		existing.setCover(cover);
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(AssessTemplate req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(AssessTemplate req) {
		return super.update(req);
	}

	private void assertExists(AssessTemplate entity) {
		if (ObjectUtil.isNull(entity) || ObjectUtil.isNotNull(entity.getDeletedAt())) {
			throw new IllegalArgumentException(NOT_EXISTS);
		}
	}

}
