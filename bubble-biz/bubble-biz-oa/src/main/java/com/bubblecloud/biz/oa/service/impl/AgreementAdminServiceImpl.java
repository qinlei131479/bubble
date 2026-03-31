package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AgreementMapper;
import com.bubblecloud.biz.oa.service.AgreementAdminService;
import com.bubblecloud.oa.api.entity.Agreement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户协议实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
@RequiredArgsConstructor
public class AgreementAdminServiceImpl implements AgreementAdminService {

	private final AgreementMapper agreementMapper;

	@Override
	public List<Agreement> list(String title, String ident) {
		var q = Wrappers.lambdaQuery(Agreement.class).orderByAsc(Agreement::getId);
		if (StringUtils.hasText(title)) {
			q.like(Agreement::getTitle, title);
		}
		if (StringUtils.hasText(ident)) {
			q.eq(Agreement::getIdent, ident);
		}
		return agreementMapper.selectList(q);
	}

	@Override
	public Agreement getById(int id) {
		return agreementMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateById(Agreement row) {
		agreementMapper.updateById(row);
	}

}
