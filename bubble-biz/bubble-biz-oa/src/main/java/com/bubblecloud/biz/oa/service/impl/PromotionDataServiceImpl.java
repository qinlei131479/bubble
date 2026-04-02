package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.PromotionDataMapper;
import com.bubblecloud.biz.oa.service.PromotionDataService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.PromotionDataSaveDTO;
import com.bubblecloud.oa.api.dto.hr.PromotionDataSortDTO;
import com.bubblecloud.oa.api.dto.hr.PromotionDataStandardDTO;
import com.bubblecloud.oa.api.entity.PromotionData;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 晋升数据项服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class PromotionDataServiceImpl extends UpServiceImpl<PromotionDataMapper, PromotionData>
		implements PromotionDataService {

	@Override
	public SimplePageVO pagePromotionData(Pg<PromotionData> pg, PromotionData query) {
		Page<PromotionData> r = findPg(pg, query);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createPromotionData(PromotionDataSaveDTO dto) {
		PromotionData entity = new PromotionData();
		entity.setPid(dto.getPid());
		entity.setName(dto.getName());
		entity.setContent(dto.getContent());
		entity.setStandard(dto.getStandard());
		entity.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		save(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updatePromotionData(Long id, PromotionDataSaveDTO dto) {
		PromotionData existing = getById(id);
		assertExists(existing);
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (StrUtil.isNotBlank(dto.getContent())) {
			existing.setContent(dto.getContent());
		}
		if (StrUtil.isNotBlank(dto.getStandard())) {
			existing.setStandard(dto.getStandard());
		}
		if (ObjectUtil.isNotNull(dto.getSort())) {
			existing.setSort(dto.getSort());
		}
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removePromotionData(Long id) {
		assertExists(getById(id));
		removeById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStandard(Long id, PromotionDataStandardDTO dto) {
		PromotionData existing = getById(id);
		assertExists(existing);
		existing.setStandard(dto.getStandard());
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void sortPromotionData(Long pid, PromotionDataSortDTO dto) {
		if (ObjectUtil.isNull(dto) || CollUtil.isEmpty(dto.getIds())) {
			return;
		}
		List<Long> ids = dto.getIds();
		for (int i = 0; i < ids.size(); i++) {
			PromotionData entity = getById(ids.get(i));
			if (ObjectUtil.isNotNull(entity) && ObjectUtil.isNull(entity.getDeletedAt())) {
				entity.setSort(i + 1);
				updateById(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(PromotionData req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(PromotionData req) {
		return super.update(req);
	}

	private void assertExists(PromotionData entity) {
		if (ObjectUtil.isNull(entity) || ObjectUtil.isNotNull(entity.getDeletedAt())) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
	}

}
