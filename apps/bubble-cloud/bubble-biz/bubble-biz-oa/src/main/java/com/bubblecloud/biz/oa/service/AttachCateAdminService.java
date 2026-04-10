package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Category;
import com.bubblecloud.oa.api.vo.CategoryAttachTreeVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;

/**
 * 附件分类（eb_category.type=systemAttach），对齐 PHP ent/system/attach_cate。
 *
 * @author qinlei
 * @date 2026/3/31
 */
public interface AttachCateAdminService extends UpService<Category> {

	List<CategoryAttachTreeVO> attachCateTree(int entid);

	void deleteAttachCategory(Long id, int entid);

	OaElFormVO buildAttachCateCreateForm(int entid);

}
