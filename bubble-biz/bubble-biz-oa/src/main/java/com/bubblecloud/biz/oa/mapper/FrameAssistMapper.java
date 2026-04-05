package com.bubblecloud.biz.oa.mapper;

import java.util.Collection;
import java.util.List;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.dto.FrameAssistView;
import com.bubblecloud.oa.api.dto.frame.FrameAssistCardBatchRow;
import com.bubblecloud.oa.api.entity.FrameAssist;
import com.bubblecloud.oa.api.vo.frame.FrameAdminBriefVO;
import com.bubblecloud.oa.api.vo.frame.FrameAssistUserRowVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * eb_frame_assist 表访问与关联查询（SQL 见 classpath:/mapper/FrameAssistMapper.xml）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Mapper
public interface FrameAssistMapper extends UpMapper<FrameAssist> {

	List<FrameAssistView> selectUserFrames(@Param("userId") Long userId, @Param("entid") Long entId);

	List<FrameAdminBriefVO> selectFrameUsers(@Param("frameId") int frameId, @Param("entid") Long entId);

	List<FrameAssistUserRowVO> selectUsersByEnt(@Param("entid") Long entId);

	List<FrameAssistCardBatchRow> selectFramesForCardBatch(@Param("entid") long entid,
			@Param("userIds") Collection<Long> userIds);

}
