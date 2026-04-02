package com.bubblecloud.biz.oa.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.bubblecloud.biz.oa.mapper.FormCategoryMapper;
import com.bubblecloud.biz.oa.service.FormCategoryService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.FormCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自定义表单管理实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
public class FormCategoryServiceImpl extends UpServiceImpl<FormCategoryMapper, FormCategory>
		implements FormCategoryService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(FormCategory req) {
		req.setSort(ObjectUtil.defaultIfNull(req.getSort(), 0));
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(FormCategory req) {
		return super.update(req);
	}

}
