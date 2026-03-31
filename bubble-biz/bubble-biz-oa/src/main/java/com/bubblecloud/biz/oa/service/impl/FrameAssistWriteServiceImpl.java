package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.service.FrameAssistWriteService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.FrameAssist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 维护用户部门关联（对齐 PHP FrameAssistService::setUserFrame 核心逻辑）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
public class FrameAssistWriteServiceImpl extends UpServiceImpl<FrameAssistMapper, FrameAssist>
		implements FrameAssistWriteService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setUserFrames(int entid, long userId, List<Integer> frameIds, int masterFrameId, boolean isAdmin,
			long superiorUid, List<Integer> manageFrameIds) {
		LocalDateTime now = LocalDateTime.now();
		List<Integer> manage = ObjectUtil.isNull(manageFrameIds) ? List.of() : manageFrameIds;

		baseMapper.update(null,
				Wrappers.lambdaUpdate(FrameAssist.class)
					.set(FrameAssist::getDeletedAt, now)
					.eq(FrameAssist::getEntid, entid)
					.eq(FrameAssist::getUserId, userId)
					.notIn(FrameAssist::getFrameId, frameIds)
					.isNull(FrameAssist::getDeletedAt));

		for (Integer fid : frameIds) {
			int isMastart = (ObjectUtil.isNotNull(fid) && fid == masterFrameId) ? 1 : 0;
			int isFrameAdmin = (isAdmin && ObjectUtil.isNotNull(fid) && manage.contains(fid)) ? 1 : 0;
			FrameAssist exist = baseMapper.selectOne(Wrappers.lambdaQuery(FrameAssist.class)
				.eq(FrameAssist::getEntid, entid)
				.eq(FrameAssist::getUserId, userId)
				.eq(FrameAssist::getFrameId, fid)
				.last("LIMIT 1"));
			if (ObjectUtil.isNull(exist)) {
				FrameAssist fa = new FrameAssist();
				fa.setEntid(entid);
				fa.setFrameId(fid);
				fa.setUserId(userId);
				fa.setIsMastart(isMastart);
				fa.setIsAdmin(isFrameAdmin);
				fa.setSuperiorUid(superiorUid);
				fa.setCreatedAt(now);
				fa.setUpdatedAt(now);
				baseMapper.insert(fa);
			}
			else {
				exist.setDeletedAt(null);
				exist.setIsMastart(isMastart);
				exist.setIsAdmin(isFrameAdmin);
				exist.setSuperiorUid(superiorUid);
				exist.setUpdatedAt(now);
				baseMapper.updateById(exist);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(FrameAssist req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(FrameAssist req) {
		return super.update(req);
	}

}
