package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.AssessTemplateMapper;
import com.bubblecloud.biz.oa.service.AssessTemplateService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.AssessTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 绩效考核模板服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class AssessTemplateServiceImpl extends UpServiceImpl<AssessTemplateMapper, AssessTemplate>
		implements AssessTemplateService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(AssessTemplate req) {
		req.setStatus(ObjectUtil.defaultIfNull(req.getStatus(), 1));
		req.setIsFavorite(0);
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(AssessTemplate req) {
		AssessTemplate existing = baseMapper.selectById(req.getId());
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		return super.update(req);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void toggleFavorite(Long id) {
		AssessTemplate existing = baseMapper.selectById(id);
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		int cur = ObjectUtil.defaultIfNull(existing.getIsFavorite(), 0);
		existing.setIsFavorite(cur == 1 ? 0 : 1);
		super.update(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setCover(Long id, String cover) {
		AssessTemplate existing = baseMapper.selectById(id);
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		existing.setCover(cover);
		super.update(existing);
	}


}
