package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.RankCateSaveDTO;
import com.bubblecloud.oa.api.entity.RankCategory;
import com.bubblecloud.oa.api.vo.SimplePageVO;

/**
 * 职级体系分类服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface RankCategoryService extends UpService<RankCategory> {

	SimplePageVO pageRankCate(Pg<RankCategory> pg, RankCategory query);

	void createRankCate(RankCateSaveDTO dto);

	void updateRankCate(long id, RankCateSaveDTO dto);

	void removeRankCate(long id);

}
