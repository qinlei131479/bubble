package com.bubblecloud.biz.oa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.ProgramTask;

/**
 * eb_program_task Mapper。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Mapper
public interface ProgramTaskMapper extends UpMapper<ProgramTask> {

	long countByProgramId(@Param("programId") Long programId);

	long countIncompleteByProgramId(@Param("programId") Long programId);

	List<ProgramTask> selectFlatList(@Param("query") ProgramTask query,
			@Param("participantTaskIds") List<Long> participantTaskIds, @Param("authScope") boolean authScope);

	Integer selectMaxSort(@Param("pid") Long pid, @Param("programId") Long programId);

	List<Long> selectTaskIdsByMemberUid(@Param("uid") Long uid);

	List<ProgramTask> selectByPathContains(@Param("ancestorId") Long ancestorId);

}
