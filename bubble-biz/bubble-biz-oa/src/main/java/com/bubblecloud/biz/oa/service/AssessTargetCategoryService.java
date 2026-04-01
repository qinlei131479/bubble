package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.AssessTargetCateSaveDTO;
import com.bubblecloud.oa.api.entity.AssessTargetCategory;
import com.bubblecloud.oa.api.vo.SimplePageVO;

/**
 * 绩效指标分类服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface AssessTargetCategoryService extends UpService<AssessTargetCategory> {

	SimplePageVO pageTargetCate(Pg<AssessTargetCategory> pg, AssessTargetCategory query);

	void createTargetCate(AssessTargetCateSaveDTO dto);

	void updateTargetCate(long id, AssessTargetCateSaveDTO dto);

	void removeTargetCate(long id);

}
