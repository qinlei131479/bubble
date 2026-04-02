package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.PromotionDataSaveDTO;
import com.bubblecloud.oa.api.dto.hr.PromotionDataSortDTO;
import com.bubblecloud.oa.api.dto.hr.PromotionDataStandardDTO;
import com.bubblecloud.oa.api.entity.PromotionData;
import com.bubblecloud.oa.api.vo.SimplePageVO;

/**
 * 晋升数据项服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface PromotionDataService extends UpService<PromotionData> {

	SimplePageVO pagePromotionData(Pg<PromotionData> pg, PromotionData query);

	void createPromotionData(PromotionDataSaveDTO dto);

	void updatePromotionData(Long id, PromotionDataSaveDTO dto);

	void removePromotionData(Long id);

	void updateStandard(Long id, PromotionDataStandardDTO dto);

	void sortPromotionData(Long pid, PromotionDataSortDTO dto);

}
