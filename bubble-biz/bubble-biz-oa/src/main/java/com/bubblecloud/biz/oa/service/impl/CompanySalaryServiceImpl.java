package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.EnterpriseUserSalaryMapper;
import com.bubblecloud.biz.oa.service.CompanySalaryService;
import com.bubblecloud.oa.api.dto.CompanySalarySaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserSalary;

import lombok.RequiredArgsConstructor;

/**
 * 调薪记录实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Service
@RequiredArgsConstructor
public class CompanySalaryServiceImpl implements CompanySalaryService {

	private final EnterpriseUserSalaryMapper enterpriseUserSalaryMapper;

	@Override
	public Page<EnterpriseUserSalary> pageSalary(int entid, Integer cardId, Integer linkId, Long id,
			Page<EnterpriseUserSalary> page) {
		var q = Wrappers.lambdaQuery(EnterpriseUserSalary.class).eq(EnterpriseUserSalary::getEntid, entid);
		if (cardId != null && cardId > 0) {
			q.eq(EnterpriseUserSalary::getCardId, cardId);
		}
		if (linkId != null && linkId > 0) {
			q.eq(EnterpriseUserSalary::getLinkId, linkId);
		}
		if (id != null && id > 0) {
			q.eq(EnterpriseUserSalary::getId, id);
		}
		q.orderByDesc(EnterpriseUserSalary::getTakeDate).orderByDesc(EnterpriseUserSalary::getId);
		return enterpriseUserSalaryMapper.selectPage(page, q);
	}

	@Override
	public EnterpriseUserSalary getForEdit(long id) {
		return enterpriseUserSalaryMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveSalary(CompanySalarySaveDTO dto) {
		EnterpriseUserSalary e = new EnterpriseUserSalary();
		e.setEntid(dto.getEntid() == null ? 1 : dto.getEntid());
		e.setCardId(dto.getCardId() == null ? 0 : dto.getCardId());
		e.setTotal(dto.getTotal() == null ? java.math.BigDecimal.ZERO : dto.getTotal());
		e.setContent(dto.getContent() == null ? "" : dto.getContent());
		e.setMark(dto.getMark() == null ? "" : dto.getMark());
		e.setTakeDate(dto.getTakeDate());
		e.setLinkId(0);
		return enterpriseUserSalaryMapper.insert(e) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateSalary(long id, CompanySalarySaveDTO dto) {
		EnterpriseUserSalary e = enterpriseUserSalaryMapper.selectById(id);
		if (e == null) {
			return false;
		}
		if (dto.getCardId() != null) {
			e.setCardId(dto.getCardId());
		}
		if (dto.getTotal() != null) {
			e.setTotal(dto.getTotal());
		}
		if (dto.getContent() != null) {
			e.setContent(dto.getContent());
		}
		if (dto.getMark() != null) {
			e.setMark(dto.getMark());
		}
		if (dto.getTakeDate() != null) {
			e.setTakeDate(dto.getTakeDate());
		}
		if (dto.getEntid() != null) {
			e.setEntid(dto.getEntid());
		}
		return enterpriseUserSalaryMapper.updateById(e) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeSalary(long id) {
		return enterpriseUserSalaryMapper.deleteById(id) > 0;
	}

	@Override
	public List<EnterpriseUserSalary> lastByCardId(int cardId) {
		return enterpriseUserSalaryMapper.selectList(Wrappers.lambdaQuery(EnterpriseUserSalary.class)
				.eq(EnterpriseUserSalary::getCardId, cardId)
				.orderByDesc(EnterpriseUserSalary::getTakeDate)
				.orderByDesc(EnterpriseUserSalary::getId)
				.last("LIMIT 1"));
	}

}
