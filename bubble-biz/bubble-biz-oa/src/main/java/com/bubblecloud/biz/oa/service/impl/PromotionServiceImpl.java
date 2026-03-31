package com.bubblecloud.biz.oa.service.impl;

import org.springframework.stereotype.Service;

import com.bubblecloud.biz.oa.mapper.PromotionMapper;
import com.bubblecloud.biz.oa.service.PromotionService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Promotion;

/**
 * 晋升表服务实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Service
public class PromotionServiceImpl extends UpServiceImpl<PromotionMapper, Promotion> implements PromotionService {

}
