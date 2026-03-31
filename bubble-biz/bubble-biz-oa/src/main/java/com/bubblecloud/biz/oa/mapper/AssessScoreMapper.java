package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.AssessScore;

/**
 * eb_assess_score 表访问（SQL 见 classpath:/mapper/AssessScoreMapper.xml）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Mapper
public interface AssessScoreMapper extends UpMapper<AssessScore> {

	Integer selectMaxScoreByEntid(@Param("entid") long entid);

}
