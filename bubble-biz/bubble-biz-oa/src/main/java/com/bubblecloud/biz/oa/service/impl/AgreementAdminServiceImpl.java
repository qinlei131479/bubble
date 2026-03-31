package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AgreementMapper;
import com.bubblecloud.biz.oa.service.AgreementAdminService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Agreement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.StrUtil;

/**
 * 用户协议实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
public class AgreementAdminServiceImpl extends UpServiceImpl<AgreementMapper, Agreement> implements AgreementAdminService {

	@Override
	public List<Agreement> list(String title, String ident) {
		var q = Wrappers.lambdaQuery(Agreement.class).orderByAsc(Agreement::getId);
		if (StrUtil.isNotBlank(title)) {
			q.like(Agreement::getTitle, title);
		}
		if (StrUtil.isNotBlank(ident)) {
			q.eq(Agreement::getIdent, ident);
		}
		return baseMapper.selectList(q);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Agreement req) {
		return super.update(req);
	}

}
