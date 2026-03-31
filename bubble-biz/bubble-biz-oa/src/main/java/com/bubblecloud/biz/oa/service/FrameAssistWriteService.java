package com.bubblecloud.biz.oa.service;

import java.util.List;

/**
 * 维护 eb_frame_assist（对齐 PHP FrameAssistService::setUserFrame）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
public interface FrameAssistWriteService {

	void setUserFrames(int entid, long userId, List<Integer> frameIds, int masterFrameId, boolean isAdmin,
			long superiorUid, List<Integer> manageFrameIds);

}
