package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * eb_assess_score 聚合查询。
 *
 * @author qinlei
 */
@Mapper
public interface AssessScoreMapper {

	@Select("SELECT COALESCE(MAX(`max`), 0) FROM eb_assess_score WHERE entid = #{entid}")
	Integer selectMaxScoreByEntid(@Param("entid") long entid);

}
