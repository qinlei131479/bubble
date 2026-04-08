package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.ProgramTaskCommentMapper;
import com.bubblecloud.biz.oa.mapper.ProgramTaskMapper;
import com.bubblecloud.biz.oa.service.ProgramTaskCommentOaService;
import com.bubblecloud.biz.oa.util.ProgramTreeUtil;
import com.bubblecloud.oa.api.dto.program.ProgramTaskCommentSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.ProgramTask;
import com.bubblecloud.oa.api.entity.ProgramTaskComment;
import com.bubblecloud.oa.api.vo.CreatedIdVO;
import com.bubblecloud.oa.api.vo.program.ProgramAdminLiteVO;
import com.bubblecloud.oa.api.vo.program.ProgramCommentListVO;
import com.bubblecloud.oa.api.vo.program.ProgramCommentNodeVO;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 任务评论实现。
 *
 * @author qinlei
 * @date 2026/4/8 13:30
 */
@Service
@RequiredArgsConstructor
public class ProgramTaskCommentOaServiceImpl implements ProgramTaskCommentOaService {

	private final ProgramTaskCommentMapper programTaskCommentMapper;

	private final ProgramTaskMapper programTaskMapper;

	private final AdminMapper adminMapper;

	@Override
	public ProgramCommentListVO listByTask(Long taskId) {
		long total = programTaskCommentMapper
			.selectCount(Wrappers.lambdaQuery(ProgramTaskComment.class).eq(ProgramTaskComment::getTaskId, taskId));
		List<ProgramTaskComment> rows = programTaskCommentMapper
			.selectList(Wrappers.lambdaQuery(ProgramTaskComment.class)
				.eq(ProgramTaskComment::getTaskId, taskId)
				.orderByAsc(ProgramTaskComment::getId));
		Map<Long, Admin> admins = loadAdmins(rows);
		List<ProgramCommentNodeVO> flat = new ArrayList<>();
		for (ProgramTaskComment c : rows) {
			flat.add(toNode(c, admins));
		}
		List<ProgramCommentNodeVO> tree = ProgramTreeUtil.buildForest(flat, ProgramCommentNodeVO::getId,
				c -> ObjectUtil.defaultIfNull(c.getPid(), 0L), ProgramCommentNodeVO::getChildren,
				ProgramCommentNodeVO::setChildren, Comparator.comparing(ProgramCommentNodeVO::getId));
		return new ProgramCommentListVO(tree, total);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public CreatedIdVO save(ProgramTaskCommentSaveDTO dto, Long operatorUid) {
		if (ObjectUtil.isNull(dto.getTaskId()) || dto.getTaskId() < 1) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		ProgramTask task = programTaskMapper.selectById(dto.getTaskId());
		if (ObjectUtil.isNull(task)) {
			throw new IllegalArgumentException("任务数据异常");
		}
		if (ObjectUtil.defaultIfNull(dto.getPid(), 0L) > 0) {
			ProgramTaskComment parent = programTaskCommentMapper.selectById(dto.getPid());
			if (ObjectUtil.isNull(parent) || !ObjectUtil.equal(parent.getTaskId(), dto.getTaskId())) {
				throw new IllegalArgumentException("请重新选择上级评价");
			}
		}
		ProgramTaskComment c = new ProgramTaskComment();
		c.setTaskId(dto.getTaskId());
		c.setPid(ObjectUtil.defaultIfNull(dto.getPid(), 0L));
		c.setReplyUid(ObjectUtil.defaultIfNull(dto.getReplyUid(), 0L));
		c.setUid(ObjectUtil.defaultIfNull(operatorUid, 0L));
		c.setDescribe(StrUtil.nullToEmpty(dto.getDescribe()));
		c.setCreatedAt(LocalDateTime.now());
		c.setUpdatedAt(LocalDateTime.now());
		programTaskCommentMapper.insert(c);
		return new CreatedIdVO(c.getId());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(Long id, ProgramTaskCommentSaveDTO dto, Long operatorUid) {
		ProgramTaskComment c = programTaskCommentMapper.selectById(id);
		if (ObjectUtil.isNull(c)) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (!ObjectUtil.equal(c.getUid(), operatorUid)) {
			throw new IllegalArgumentException("common.operation.noPermission");
		}
		c.setDescribe(StrUtil.nullToEmpty(dto.getDescribe()));
		c.setUpdatedAt(LocalDateTime.now());
		programTaskCommentMapper.updateById(c);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Long id, Long operatorUid) {
		ProgramTaskComment c = programTaskCommentMapper.selectById(id);
		if (ObjectUtil.isNull(c)) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (!ObjectUtil.equal(c.getUid(), operatorUid)) {
			throw new IllegalArgumentException("common.operation.noPermission");
		}
		programTaskCommentMapper
			.delete(Wrappers.lambdaQuery(ProgramTaskComment.class).eq(ProgramTaskComment::getPid, id));
		programTaskCommentMapper.deleteById(id);
	}

	private ProgramCommentNodeVO toNode(ProgramTaskComment c, Map<Long, Admin> admins) {
		ProgramCommentNodeVO n = new ProgramCommentNodeVO();
		n.setId(c.getId());
		n.setTaskId(c.getTaskId());
		n.setPid(c.getPid());
		n.setReplyUid(c.getReplyUid());
		n.setUid(c.getUid());
		n.setDescribe(c.getDescribe());
		n.setCreatedAt(c.getCreatedAt());
		n.setMember(toLite(admins.get(c.getUid())));
		n.setReplyMember(toLite(admins.get(c.getReplyUid())));
		return n;
	}

	private static ProgramAdminLiteVO toLite(Admin a) {
		if (a == null) {
			return null;
		}
		ProgramAdminLiteVO v = new ProgramAdminLiteVO();
		v.setId(a.getId());
		v.setName(a.getName());
		v.setAvatar(a.getAvatar());
		v.setUid(a.getUid());
		v.setPhone(a.getPhone());
		return v;
	}

	private Map<Long, Admin> loadAdmins(List<ProgramTaskComment> rows) {
		List<Long> ids = rows.stream()
			.flatMap(c -> java.util.stream.Stream.of(c.getUid(), c.getReplyUid()))
			.filter(ObjectUtil::isNotNull)
			.filter(u -> u > 0)
			.distinct()
			.collect(Collectors.toList());
		if (ids.isEmpty()) {
			return new HashMap<>();
		}
		List<Admin> list = adminMapper.selectList(Wrappers.lambdaQuery(Admin.class).in(Admin::getId, ids));
		return list.stream().collect(Collectors.toMap(Admin::getId, a -> a, (x, y) -> x));
	}

}
