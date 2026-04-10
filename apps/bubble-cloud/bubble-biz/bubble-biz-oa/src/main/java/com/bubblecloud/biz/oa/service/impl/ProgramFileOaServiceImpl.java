package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.ProgramOaConstants;
import com.bubblecloud.biz.oa.mapper.SystemAttachMapper;
import com.bubblecloud.biz.oa.service.ProgramFileOaService;
import com.bubblecloud.biz.oa.util.OaLocalUploadSupport;
import com.bubblecloud.oa.api.entity.SystemAttach;
import com.bubblecloud.oa.api.vo.ListCountVO;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 项目附件实现。
 *
 * @author qinlei
 * @date 2026/4/8 14:00
 */
@Service
@RequiredArgsConstructor
public class ProgramFileOaServiceImpl implements ProgramFileOaService {

	private final SystemAttachMapper systemAttachMapper;

	@Override
	public ListCountVO<SystemAttach> list(int entid, long programId, String nameLike, int page, int limit) {
		int p = Math.max(page, 1);
		int sz = limit > 0 ? limit : 20;
		Page<SystemAttach> pg = new Page<>(p, sz);
		var q = Wrappers.lambdaQuery(SystemAttach.class)
			.eq(SystemAttach::getEntid, entid)
			.eq(SystemAttach::getRelationType, ProgramOaConstants.ATTACH_RELATION_PROGRAM)
			.eq(SystemAttach::getRelationId, (int) programId);
		if (StrUtil.isNotBlank(nameLike)) {
			q.like(SystemAttach::getName, nameLike);
		}
		q.orderByDesc(SystemAttach::getId);
		Page<SystemAttach> res = systemAttachMapper.selectPage(pg, q);
		return ListCountVO.of(res.getRecords(), res.getTotal());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(int id, int entid) {
		SystemAttach row = systemAttachMapper.selectById(id);
		if (row == null || ObjectUtil.defaultIfNull(row.getEntid(), 0) != entid) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (ObjectUtil.defaultIfNull(row.getUpType(), 1) == 1) {
			String dir = row.getAttDir();
			if (StrUtil.isNotBlank(dir)) {
				String path = dir.startsWith("/") ? dir.substring(1) : dir;
				OaLocalUploadSupport.deleteRelativeFileIfExists(path);
			}
		}
		systemAttachMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void rename(int id, int entid, String realName) {
		if (StrUtil.isBlank(realName)) {
			throw new IllegalArgumentException("文件名称不能为空");
		}
		SystemAttach row = systemAttachMapper.selectById(id);
		if (row == null || ObjectUtil.defaultIfNull(row.getEntid(), 0) != entid) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		row.setRealName(realName);
		row.setUpdatedAt(LocalDateTime.now());
		systemAttachMapper.updateById(row);
	}

}
