package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.HayGroupDataMapper;
import com.bubblecloud.biz.oa.mapper.HayGroupMapper;
import com.bubblecloud.biz.oa.service.HayGroupService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.HayGroupSaveDTO;
import com.bubblecloud.oa.api.entity.HayGroup;
import com.bubblecloud.oa.api.entity.HayGroupData;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 海氏评估组服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
@RequiredArgsConstructor
public class HayGroupServiceImpl extends UpServiceImpl<HayGroupMapper, HayGroup> implements HayGroupService {

	private static final String NOT_EXISTS = "common.operation.noExists";

	private final HayGroupDataMapper hayGroupDataMapper;

	@Override
	public SimplePageVO pageHayGroup(Pg<HayGroup> pg, HayGroup query) {
		Page<HayGroup> r = findPg(pg, query);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createHayGroup(HayGroupSaveDTO dto) {
		HayGroup entity = new HayGroup();
		entity.setName(dto.getName());
		entity.setMark(dto.getMark());
		entity.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		save(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateHayGroup(Long id, HayGroupSaveDTO dto) {
		HayGroup existing = getById(id);
		assertExists(existing);
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (StrUtil.isNotBlank(dto.getMark())) {
			existing.setMark(dto.getMark());
		}
		if (ObjectUtil.isNotNull(dto.getStatus())) {
			existing.setStatus(dto.getStatus());
		}
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeHayGroup(Long id) {
		assertExists(getById(id));
		removeById(id);
	}

	@Override
	public List<HayGroupData> dataList(Long groupId) {
		return hayGroupDataMapper.selectList(
				Wrappers.lambdaQuery(HayGroupData.class).eq(HayGroupData::getGroupId, groupId)
						.orderByDesc(HayGroupData::getId));
	}

	@Override
	public List<HayGroupData> historyList(Long groupId) {
		return hayGroupDataMapper.selectList(
				Wrappers.lambdaQuery(HayGroupData.class).eq(HayGroupData::getGroupId, groupId)
						.orderByDesc(HayGroupData::getAssessTime));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(HayGroup req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(HayGroup req) {
		return super.update(req);
	}

	private void assertExists(HayGroup entity) {
		if (ObjectUtil.isNull(entity) || ObjectUtil.isNotNull(entity.getDeletedAt())) {
			throw new IllegalArgumentException(NOT_EXISTS);
		}
	}

}
