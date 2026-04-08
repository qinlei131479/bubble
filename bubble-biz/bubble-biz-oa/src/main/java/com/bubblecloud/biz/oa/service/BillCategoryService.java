package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.BillCategory;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * 财务分类服务。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
public interface BillCategoryService extends UpService<BillCategory> {

	/**
	 * 对齐 PHP {@code BillCategoryService::getSearchCate}：无 anchor 时一级分类；否则先取子级，无则取自身。
	 */
	List<BillCategory> searchCate(Integer anchorId, int types, long entid);

	/** 子树（含根）全部分类 id，rootId≤0 时返回该企业该类型下全部分类 id */
	List<Integer> subtreeInclusiveIds(int rootId, long entid, int types);

	/** form-create 级联选项树（支出/收入各一棵），排除指定分类及其子级 */
	ArrayNode buildBillCascaderOptions(int types, long excludeCategoryId, long entid);

	OaElFormVO buildCategoryCreateForm(long entid);

	OaElFormVO buildCategoryEditForm(long id, long entid);

}
