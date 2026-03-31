package com.bubblecloud.biz.oa.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.PromotionMapper;
import com.bubblecloud.biz.oa.service.PromotionService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Promotion;
import cn.hutool.core.util.ObjectUtil;

/**
 * 晋升表服务实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Service
public class PromotionServiceImpl extends UpServiceImpl<PromotionMapper, Promotion> implements PromotionService {

	@Override
	public Page<Promotion> pagePromotions(long current, long size, Integer status) {
		var q = Wrappers.lambdaQuery(Promotion.class).isNull(Promotion::getDeletedAt);
		if (ObjectUtil.isNotNull(status)) {
			q.eq(Promotion::getStatus, status);
		}
		q.orderByAsc(Promotion::getSort).orderByDesc(Promotion::getId);
		return this.page(new Page<>(current, size), q);
	}

}
