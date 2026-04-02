package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.PromotionMapper;
import com.bubblecloud.biz.oa.service.PromotionService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Promotion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 晋升表服务实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PromotionServiceImpl extends UpServiceImpl<PromotionMapper, Promotion> implements PromotionService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Promotion req) {
		req.setSort(ObjectUtil.isNull(req.getSort()) ? 0 : req.getSort());
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Promotion req) {
		return super.update(req);
	}

}
