package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Category;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;

/**
 * 快捷入口分类（eb_category.type=quickConfig）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface CategoryService extends UpService<Category> {

	List<Category> list(Long entId);

	/**
	 * 快捷入口分类下拉用（type=quickConfig、已展示）。
	 */
	List<Category> listVisibleQuickCategories(long entId);

	OaElFormVO buildQuickCategoryCreateForm(long entId);

}
