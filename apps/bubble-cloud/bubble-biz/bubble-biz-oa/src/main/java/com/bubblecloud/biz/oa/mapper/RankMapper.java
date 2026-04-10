package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.Rank;

/**
 * eb_rank Mapper。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Mapper
public interface RankMapper extends UpMapper<Rank> {

}
