package com.bubblecloud.biz.oa.service.impl;

import java.util.List;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CategoryMapper;
import com.bubblecloud.biz.oa.service.AttachCateAdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Category;
import org.springframework.stereotype.Service;

/**
 * 附件分类实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
public class AttachCateAdminServiceImpl extends UpServiceImpl<CategoryMapper, Category>
		implements AttachCateAdminService {

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
