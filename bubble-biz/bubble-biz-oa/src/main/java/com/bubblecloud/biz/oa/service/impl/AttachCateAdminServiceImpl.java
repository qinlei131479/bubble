package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CategoryMapper;
import com.bubblecloud.biz.oa.service.AttachCateAdminService;
import com.bubblecloud.oa.api.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 附件分类实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
@RequiredArgsConstructor
public class AttachCateAdminServiceImpl implements AttachCateAdminService {

	private static final String TYPE_ATTACH = "systemAttach";

	private final CategoryMapper categoryMapper;

	@Override
	public List<Category> listByEntid(int entid) {
		return categoryMapper.selectList(Wrappers.lambdaQuery(Category.class)
			.eq(Category::getType, TYPE_ATTACH)
			.eq(Category::getEntid, entid)
			.orderByDesc(Category::getSort));
	}

}
