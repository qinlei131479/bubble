package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Category;

/**
 * 快捷入口分类（eb_category.type=quickConfig）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface QuickCateAdminService extends UpService<Category> {

	List<Category> list(Integer entid);

	Category getCate(Long id);

	Long saveCate(Category row);

	void updateCate(Category row);

	void deleteCate(Long id);

}
