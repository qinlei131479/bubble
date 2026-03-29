package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * eb_assess_score 聚合查询（SQL 见 classpath:/mapper/AssessScoreMapper.xml）。
 *
 * @author qinlei
 */
@Mapper
public interface AssessScoreMapper {

	Integer selectMaxScoreByEntid(@Param("entid") long entid);

}
