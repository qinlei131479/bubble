package com.bubblecloud.biz.oa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.ProgramDynamic;

/**
 * eb_program_dynamic Mapper。
 *
 * @author qinlei
 * @date 2026/4/8 12:00
 */
@Mapper
public interface ProgramDynamicMapper extends UpMapper<ProgramDynamic> {

	List<ProgramDynamic> selectDynamicPage(@Param("types") Integer types, @Param("relationId") Long relationId,
			@Param("programId") Long programId, @Param("uid") Long uid, @Param("offset") long offset,
			@Param("limit") long limit);

	long countDynamicPage(@Param("types") Integer types, @Param("relationId") Long relationId,
			@Param("programId") Long programId, @Param("uid") Long uid);

}
