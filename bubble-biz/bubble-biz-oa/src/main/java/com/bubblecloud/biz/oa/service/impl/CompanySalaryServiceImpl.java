package com.bubblecloud.biz.oa.service.impl;

import java.util.List;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.EnterpriseUserSalaryMapper;
import com.bubblecloud.biz.oa.service.CompanySalaryService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.CompanySalarySaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserSalary;
import cn.hutool.core.util.ObjectUtil;

/**
 * 调薪记录实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Service
public class CompanySalaryServiceImpl extends UpServiceImpl<EnterpriseUserSalaryMapper, EnterpriseUserSalary>
		implements CompanySalaryService {

	@Override
	public Page<EnterpriseUserSalary> pageSalary(Integer entid, Integer cardId, Integer linkId, Long id,
			Page<EnterpriseUserSalary> page) {
		var q = Wrappers.lambdaQuery(EnterpriseUserSalary.class).eq(EnterpriseUserSalary::getEntid, entid);
		if (ObjectUtil.isNotNull(cardId) && cardId > 0) {
			q.eq(EnterpriseUserSalary::getCardId, cardId);
		}
		if (ObjectUtil.isNotNull(linkId) && linkId > 0) {
			q.eq(EnterpriseUserSalary::getLinkId, linkId);
		}
		if (ObjectUtil.isNotNull(id) && id > 0) {
			q.eq(EnterpriseUserSalary::getId, id);
		}
		q.orderByDesc(EnterpriseUserSalary::getTakeDate).orderByDesc(EnterpriseUserSalary::getId);
		return baseMapper.selectPage(page, q);
	}

	@Override
	public EnterpriseUserSalary getForEdit(Long id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveSalary(CompanySalarySaveDTO dto) {
		EnterpriseUserSalary e = new EnterpriseUserSalary();
		e.setEntid(ObjectUtil.isNull(dto.getEntid()) ? 1 : dto.getEntid());
		e.setCardId(ObjectUtil.isNull(dto.getCardId()) ? 0 : dto.getCardId());
		e.setTotal(ObjectUtil.isNull(dto.getTotal()) ? java.math.BigDecimal.ZERO : dto.getTotal());
		e.setContent(ObjectUtil.isNull(dto.getContent()) ? "" : dto.getContent());
		e.setMark(ObjectUtil.isNull(dto.getMark()) ? "" : dto.getMark());
		e.setTakeDate(dto.getTakeDate());
		e.setLinkId(0);
		return baseMapper.insert(e) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateSalary(Long id, CompanySalarySaveDTO dto) {
		EnterpriseUserSalary e = baseMapper.selectById(id);
		if (ObjectUtil.isNull(e)) {
			return false;
		}
		if (ObjectUtil.isNotNull(dto.getCardId())) {
			e.setCardId(dto.getCardId());
		}
		if (ObjectUtil.isNotNull(dto.getTotal())) {
			e.setTotal(dto.getTotal());
		}
		if (ObjectUtil.isNotNull(dto.getContent())) {
			e.setContent(dto.getContent());
		}
		if (ObjectUtil.isNotNull(dto.getMark())) {
			e.setMark(dto.getMark());
		}
		if (ObjectUtil.isNotNull(dto.getTakeDate())) {
			e.setTakeDate(dto.getTakeDate());
		}
		if (ObjectUtil.isNotNull(dto.getEntid())) {
			e.setEntid(dto.getEntid());
		}
		return baseMapper.updateById(e) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeSalary(Long id) {
		return baseMapper.deleteById(id) > 0;
	}

	@Override
	public List<EnterpriseUserSalary> lastByCardId(Integer cardId) {
		return baseMapper.selectList(Wrappers.lambdaQuery(EnterpriseUserSalary.class)
			.eq(EnterpriseUserSalary::getCardId, cardId)
			.orderByDesc(EnterpriseUserSalary::getTakeDate)
			.orderByDesc(EnterpriseUserSalary::getId)
			.last("LIMIT 1"));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(EnterpriseUserSalary req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(EnterpriseUserSalary req) {
		return super.update(req);
	}

}
