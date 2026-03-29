package com.bubblecloud.biz.oa.mapper;

import java.util.List;

import com.bubblecloud.oa.api.dto.FrameAssistView;
import com.bubblecloud.oa.api.vo.frame.FrameAdminBriefVO;
import com.bubblecloud.oa.api.vo.frame.FrameAssistUserRowVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * eb_frame_assist 查询（SQL 见 classpath:/mapper/FrameAssistMapper.xml）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Mapper
public interface FrameAssistMapper {

	List<FrameAssistView> selectUserFrames(@Param("userId") Long userId, @Param("entid") Integer entid);

	List<FrameAdminBriefVO> selectFrameUsers(@Param("frameId") int frameId, @Param("entid") int entid);

	List<FrameAssistUserRowVO> selectUsersByEnt(@Param("entid") int entid);

}
