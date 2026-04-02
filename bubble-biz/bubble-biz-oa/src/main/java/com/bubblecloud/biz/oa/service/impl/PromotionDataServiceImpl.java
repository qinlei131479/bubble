package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.bubblecloud.biz.oa.mapper.PromotionDataMapper;
import com.bubblecloud.biz.oa.service.PromotionDataService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.PromotionData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

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
	@Transactional(rollbackFor = Exception.class)
	public void updateStandard(PromotionData dto) {
		PromotionData existing = getById(dto.getId());
		existing.setStandard(dto.getStandard());
		baseMapper.updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void sortPromotionData(PromotionData dto) {
		if (ObjectUtil.isNull(dto) || CollUtil.isEmpty(dto.getIds())) {
			return;
		}
		List<Long> ids = dto.getIds();
		for (int i = 0; i < ids.size(); i++) {
			PromotionData entity = getById(ids.get(i));
			if (ObjectUtil.isNotNull(entity) && ObjectUtil.isNull(entity.getDeletedAt())) {
				entity.setSort(i + 1);
				baseMapper.updateById(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(PromotionData req) {
		req.setSort(ObjectUtil.defaultIfNull(req.getSort(), 0));
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(PromotionData req) {
		return super.update(req);
	}

}
