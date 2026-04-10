package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.ProgramVersion;

/**
 * eb_program_version Mapper。
 *
 * @author qinlei
 * @date 2026/4/8 12:00
 */
@Mapper
public interface ProgramVersionMapper extends UpMapper<ProgramVersion> {

}
