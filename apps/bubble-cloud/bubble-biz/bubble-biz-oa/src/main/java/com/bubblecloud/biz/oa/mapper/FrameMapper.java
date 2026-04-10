package com.bubblecloud.biz.oa.mapper;

import java.util.List;

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

	Long selectMinIdByEntid(@Param("entid") long entid);

	/**
	 * 某部门路径下的子部门 id（不含自身），对齐 PHP Frame path + not_id。
	 */
	List<Integer> selectSubtreeFrameIds(@Param("entid") long entid, @Param("frameId") int frameId);

}
