package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.RankLevelBatchUpdateDTO;
import com.bubblecloud.oa.api.entity.Rank;
import com.bubblecloud.oa.api.entity.RankLevel;

/**
 * 职位等级服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface RankLevelService extends UpService<RankLevel> {

	void batchUpdateRankLevel(String batch, RankLevelBatchUpdateDTO dto);

	void relateRank(Long id, Long rankId);

	List<Rank> unrelatedRanks(Long jobId, Long entid);

}
