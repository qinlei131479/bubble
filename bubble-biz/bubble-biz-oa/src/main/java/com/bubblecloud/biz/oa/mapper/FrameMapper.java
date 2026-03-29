package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.Frame;

/**
 * eb_frame 表 Mapper（SQL 见 classpath:/mapper/FrameMapper.xml）。
 *
 * @author qinlei
 * @date 2026/3/29 下午12:00
 */
@Mapper
public interface FrameMapper extends UpMapper<Frame> {

	long countByPid(@Param("pid") long pid, @Param("entid") long entid);

}
