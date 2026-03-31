package com.bubblecloud.biz.oa.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Promotion;

/**
 * 晋升表服务。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
public interface PromotionService extends UpService<Promotion> {

	Page<Promotion> pagePromotions(long current, long size, Integer status);

}
