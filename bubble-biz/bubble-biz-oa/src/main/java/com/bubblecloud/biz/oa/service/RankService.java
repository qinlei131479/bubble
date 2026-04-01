package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.RankSaveDTO;
import com.bubblecloud.oa.api.entity.Rank;
import com.bubblecloud.oa.api.vo.SimplePageVO;

/**
 * 职级服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface RankService extends UpService<Rank> {

	SimplePageVO pageRank(Pg<Rank> pg, Rank query);

	void createRank(RankSaveDTO dto);

	void updateRank(long id, RankSaveDTO dto);

	void removeRank(long id);

}
