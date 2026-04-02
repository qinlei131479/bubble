package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CategoryMapper;
import com.bubblecloud.biz.oa.service.CategoryService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Category;
import org.springframework.stereotype.Service;

/**
 * 快捷入口分类实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
public class CategoryServiceImpl extends UpServiceImpl<CategoryMapper, Category> implements CategoryService {

	private static final String TYPE_QUICK = "quickConfig";

	@Override
	public List<Category> list(Long entId) {
		return baseMapper.selectList(Wrappers.lambdaQuery(Category.class)
			.eq(Category::getType, TYPE_QUICK)
			.eq(Category::getEntid, entId)
			.orderByDesc(Category::getSort)
			.orderByAsc(Category::getId));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Category req) {
		req.setType(TYPE_QUICK);
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Category req) {
		return super.update(req);
	}

}
