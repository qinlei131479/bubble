package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.oa.api.entity.Category;

/**
 * 快捷入口分类（eb_category.type=quickConfig）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface QuickCateAdminService {

	List<Category> list(int entid);

	Category get(long id);

	long save(Category row);

	void update(Category row);

	void delete(long id);

}
