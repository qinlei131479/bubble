package com.bubblecloud.biz.oa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.Rank;
import com.bubblecloud.oa.api.entity.RankLevel;

/**
 * eb_rank_level Mapper。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Mapper
public interface RankLevelMapper extends UpMapper<RankLevel> {

	/**
	 * 查询未关联指定职位的职级列表。
	 */
	List<Rank> findUnrelatedRanks(@Param("jobId") long jobId, @Param("entid") long entid);

}
