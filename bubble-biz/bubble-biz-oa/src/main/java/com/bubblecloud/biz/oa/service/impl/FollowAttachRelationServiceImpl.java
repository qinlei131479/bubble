package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.SystemAttachMapper;
import com.bubblecloud.biz.oa.service.FollowAttachRelationService;
import com.bubblecloud.biz.oa.util.OaLocalUploadSupport;
import com.bubblecloud.oa.api.entity.SystemAttach;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 跟进附件关联实现。
 *
 * @author qinlei
 * @date 2026/4/6 18:45
 */
@Service
@RequiredArgsConstructor
public class FollowAttachRelationServiceImpl implements FollowAttachRelationService {

	private final SystemAttachMapper systemAttachMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveRelation(int entid, String uid, long followId, List<Integer> attachIds) {
		if (attachIds == null) {
			return;
		}
		List<Integer> ids = attachIds.stream()
			.filter(ObjectUtil::isNotNull)
			.map(Integer::intValue)
			.distinct()
			.collect(Collectors.toCollection(ArrayList::new));
		List<SystemAttach> existing = systemAttachMapper.selectList(Wrappers.lambdaQuery(SystemAttach.class)
			.eq(SystemAttach::getEntid, entid)
			.eq(SystemAttach::getUid, StrUtil.nullToEmpty(uid))
			.eq(SystemAttach::getRelationType, OaConstants.RELATION_TYPE_FOLLOW)
			.eq(SystemAttach::getRelationId, (int) followId));
		Set<Integer> desired = new HashSet<>(ids);
		for (SystemAttach row : existing) {
			if (row.getId() != null && !desired.contains(row.getId())) {
				delCoverRow(row);
			}
		}
		if (ids.isEmpty()) {
			return;
		}
		LocalDateTime now = LocalDateTime.now();
		for (Integer id : ids) {
			SystemAttach row = systemAttachMapper.selectById(id);
			if (row == null || row.getEntid() == null || row.getEntid() != entid) {
				continue;
			}
			if (StrUtil.isNotBlank(uid) && !uid.equals(row.getUid())) {
				continue;
			}
			row.setRelationType(OaConstants.RELATION_TYPE_FOLLOW);
			row.setRelationId((int) followId);
			row.setUpdatedAt(now);
			systemAttachMapper.updateById(row);
		}
	}

	private void delCoverRow(SystemAttach row) {
		if (StrUtil.isNotBlank(row.getAttDir())) {
			String rel = row.getAttDir().replaceFirst("^/+", "");
			OaLocalUploadSupport.deleteRelativeFileIfExists(rel);
		}
		systemAttachMapper.deleteById(row.getId());
	}

}
