package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.ProgramTask;

/**
 * eb_program_task Mapper（仅统计用字段，实体最小占位）。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Mapper
public interface ProgramTaskMapper extends UpMapper<ProgramTask> {

	long countByProgramId(@Param("programId") Long programId);

	long countIncompleteByProgramId(@Param("programId") Long programId);

}
