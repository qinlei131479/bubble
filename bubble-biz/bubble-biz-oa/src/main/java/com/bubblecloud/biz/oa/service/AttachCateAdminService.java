package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Category;

/**
 * 附件分类（eb_category.type=systemAttach），对齐 PHP ent/system/attach_cate。
 *
 * @author qinlei
 * @date 2026/3/31
 */
public interface AttachCateAdminService extends UpService<Category> {

	/**
	 * 按企业查询附件分类列表。
	 * @param entid 企业 ID
	 * @return 分类列表
	 */
	List<Category> listByEntid(int entid);

}
