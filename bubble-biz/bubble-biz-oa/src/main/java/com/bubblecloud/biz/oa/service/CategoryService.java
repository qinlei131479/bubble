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
public interface CategoryService extends UpService<Category> {

	List<Category> list(Long entId);

}
