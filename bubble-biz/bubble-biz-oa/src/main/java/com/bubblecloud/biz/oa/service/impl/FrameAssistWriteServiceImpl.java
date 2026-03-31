package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.FrameAssistEntityMapper;
import com.bubblecloud.biz.oa.service.FrameAssistWriteService;
import com.bubblecloud.oa.api.entity.FrameAssist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 维护用户部门关联（对齐 PHP FrameAssistService::setUserFrame 核心逻辑）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
@RequiredArgsConstructor
public class FrameAssistWriteServiceImpl implements FrameAssistWriteService {

	private final FrameAssistEntityMapper frameAssistEntityMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setUserFrames(int entid, long userId, List<Integer> frameIds, int masterFrameId, boolean isAdmin,
			long superiorUid, List<Integer> manageFrameIds) {
		LocalDateTime now = LocalDateTime.now();
		List<Integer> manage = manageFrameIds == null ? List.of() : manageFrameIds;

		frameAssistEntityMapper.update(null,
				Wrappers.lambdaUpdate(FrameAssist.class)
					.set(FrameAssist::getDeletedAt, now)
					.eq(FrameAssist::getEntid, entid)
					.eq(FrameAssist::getUserId, userId)
					.notIn(FrameAssist::getFrameId, frameIds)
					.isNull(FrameAssist::getDeletedAt));

		for (Integer fid : frameIds) {
			int isMastart = (fid != null && fid == masterFrameId) ? 1 : 0;
			int isFrameAdmin = (isAdmin && fid != null && manage.contains(fid)) ? 1 : 0;
			FrameAssist exist = frameAssistEntityMapper.selectOne(Wrappers.lambdaQuery(FrameAssist.class)
				.eq(FrameAssist::getEntid, entid)
				.eq(FrameAssist::getUserId, userId)
				.eq(FrameAssist::getFrameId, fid)
				.last("LIMIT 1"));
			if (exist == null) {
				FrameAssist fa = new FrameAssist();
				fa.setEntid(entid);
				fa.setFrameId(fid);
				fa.setUserId(userId);
				fa.setIsMastart(isMastart);
				fa.setIsAdmin(isFrameAdmin);
				fa.setSuperiorUid(superiorUid);
				fa.setCreatedAt(now);
				fa.setUpdatedAt(now);
				frameAssistEntityMapper.insert(fa);
			}
			else {
				exist.setDeletedAt(null);
				exist.setIsMastart(isMastart);
				exist.setIsAdmin(isFrameAdmin);
				exist.setSuperiorUid(superiorUid);
				exist.setUpdatedAt(now);
				frameAssistEntityMapper.updateById(exist);
			}
		}
	}

}
