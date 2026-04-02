package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CategoryMapper;
import com.bubblecloud.biz.oa.service.QuickCateAdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 快捷入口分类实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
public class QuickCateAdminServiceImpl extends UpServiceImpl<CategoryMapper, Category>
		implements QuickCateAdminService {

	private static final String TYPE_QUICK = "quickConfig";

	@Override
	public List<Category> list(Integer entid) {
		return baseMapper.selectList(Wrappers.lambdaQuery(Category.class)
			.eq(Category::getType, TYPE_QUICK)
			.eq(Category::getEntid, entid)
			.orderByDesc(Category::getSort)
			.orderByAsc(Category::getId));
	}

	@Override
	public Category getCate(Long id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long saveCate(Category row) {
		row.setType(TYPE_QUICK);
		LocalDateTime now = LocalDateTime.now();
		row.setCreatedAt(now);
		row.setUpdatedAt(now);
		baseMapper.insert(row);
		return row.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCate(Category row) {
		row.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCate(Long id) {
		baseMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Category req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Category req) {
		return super.update(req);
	}

}
