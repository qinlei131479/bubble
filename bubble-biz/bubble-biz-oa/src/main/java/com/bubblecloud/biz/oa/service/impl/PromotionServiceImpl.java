package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.PromotionMapper;
import com.bubblecloud.biz.oa.service.PromotionService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.PromotionSaveDTO;
import com.bubblecloud.oa.api.entity.Promotion;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 晋升表服务实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PromotionServiceImpl extends UpServiceImpl<PromotionMapper, Promotion> implements PromotionService {

	@Override
	public SimplePageVO pagePromotion(Pg<Promotion> pg, Integer status) {
		Promotion query = new Promotion();
		query.setStatus(status);
		Page<Promotion> r = findPg(pg, query);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	public void createPromotion(PromotionSaveDTO dto) {
		Promotion p = new Promotion();
		p.setName(dto.getName());
		p.setSort(ObjectUtil.isNull(dto.getSort()) ? 0 : dto.getSort());
		p.setStatus(1);
		save(p);
	}

	@Override
	public Promotion getPromotionDetail(Long id) {
		Promotion p = getById(id);
		assertPromotionExists(p);
		return p;
	}

	@Override
	public void updatePromotion(Long id, PromotionSaveDTO dto) {
		Promotion existing = getById(id);
		assertPromotionExists(existing);
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (ObjectUtil.isNotNull(dto.getSort())) {
			existing.setSort(dto.getSort());
		}
		updateById(existing);
	}

	@Override
	public void removePromotion(Long id) {
		Promotion existing = getById(id);
		assertPromotionExists(existing);
		removeById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Promotion req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Promotion req) {
		return super.update(req);
	}

	private static void assertPromotionExists(Promotion p) {
		if (ObjectUtil.isNull(p) || ObjectUtil.isNotNull(p.getDeletedAt())) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
	}

}
