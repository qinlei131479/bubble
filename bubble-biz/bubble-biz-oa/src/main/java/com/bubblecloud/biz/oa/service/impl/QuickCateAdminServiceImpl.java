package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CategoryMapper;
import com.bubblecloud.biz.oa.service.QuickCateAdminService;
import com.bubblecloud.oa.api.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 快捷入口分类实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
public class QuickCateAdminServiceImpl implements QuickCateAdminService {

	private static final String TYPE_QUICK = "quickConfig";

	private final CategoryMapper categoryMapper;

	@Override
	public List<Category> list(int entid) {
		return categoryMapper.selectList(Wrappers.lambdaQuery(Category.class)
			.eq(Category::getType, TYPE_QUICK)
			.eq(Category::getEntid, entid)
			.orderByDesc(Category::getSort)
			.orderByAsc(Category::getId));
	}

	@Override
	public Category get(long id) {
		return categoryMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public long save(Category row) {
		row.setType(TYPE_QUICK);
		LocalDateTime now = LocalDateTime.now();
		row.setCreatedAt(now);
		row.setUpdatedAt(now);
		categoryMapper.insert(row);
		return row.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(Category row) {
		row.setUpdatedAt(LocalDateTime.now());
		categoryMapper.updateById(row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(long id) {
		categoryMapper.deleteById(id);
	}

}
