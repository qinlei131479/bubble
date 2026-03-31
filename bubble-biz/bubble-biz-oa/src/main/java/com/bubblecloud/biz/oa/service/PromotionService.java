package com.bubblecloud.biz.oa.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.PromotionSaveDTO;
import com.bubblecloud.oa.api.entity.Promotion;
import com.bubblecloud.oa.api.vo.SimplePageVO;

/**
 * 晋升表服务。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
public interface PromotionService extends UpService<Promotion> {

	SimplePageVO pagePromotion(Pg<Promotion> pg, Integer status);

	void createPromotion(PromotionSaveDTO dto);

	Promotion getPromotionDetail(long id);

	void updatePromotion(long id, PromotionSaveDTO dto);

	void removePromotion(long id);

}
