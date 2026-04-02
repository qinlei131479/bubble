package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.PromotionData;

/**
 * 晋升数据项服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface PromotionDataService extends UpService<PromotionData> {

	void updateStandard(PromotionData dto);

	void sortPromotionData(PromotionData dto);

}
