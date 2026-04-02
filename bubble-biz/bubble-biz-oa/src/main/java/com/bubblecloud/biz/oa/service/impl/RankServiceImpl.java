package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.RankMapper;
import com.bubblecloud.biz.oa.service.RankService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.RankSaveDTO;
import com.bubblecloud.oa.api.entity.Rank;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 职级服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class RankServiceImpl extends UpServiceImpl<RankMapper, Rank> implements RankService {

	@Override
	public SimplePageVO pageRank(Pg<Rank> pg, Rank query) {
		Page<Rank> r = findPg(pg, query);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createRank(RankSaveDTO dto) {
		Rank entity = new Rank();
		entity.setCateId(dto.getCateId());
		entity.setName(dto.getName());
		entity.setCode(dto.getCode());
		entity.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		entity.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		entity.setMark(dto.getMark());
		save(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRank(Long id, RankSaveDTO dto) {
		Rank existing = getById(id);
		assertExists(existing);
		if (ObjectUtil.isNotNull(dto.getCateId())) {
			existing.setCateId(dto.getCateId());
		}
		if (StrUtil.isNotBlank(dto.getName())) {
			existing.setName(dto.getName());
		}
		if (StrUtil.isNotBlank(dto.getCode())) {
			existing.setCode(dto.getCode());
		}
		if (ObjectUtil.isNotNull(dto.getSort())) {
			existing.setSort(dto.getSort());
		}
		if (ObjectUtil.isNotNull(dto.getStatus())) {
			existing.setStatus(dto.getStatus());
		}
		if (StrUtil.isNotBlank(dto.getMark())) {
			existing.setMark(dto.getMark());
		}
		updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeRank(Long id) {
		assertExists(getById(id));
		removeById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Rank req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Rank req) {
		return super.update(req);
	}

	private void assertExists(Rank entity) {
		if (ObjectUtil.isNull(entity) || ObjectUtil.isNotNull(entity.getDeletedAt())) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
	}

}
