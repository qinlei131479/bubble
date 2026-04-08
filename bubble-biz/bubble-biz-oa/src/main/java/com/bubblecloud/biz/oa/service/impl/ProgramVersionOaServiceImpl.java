package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.ProgramMapper;
import com.bubblecloud.biz.oa.mapper.ProgramMemberMapper;
import com.bubblecloud.biz.oa.mapper.ProgramVersionMapper;
import com.bubblecloud.biz.oa.service.ProgramVersionOaService;
import com.bubblecloud.oa.api.dto.program.ProgramVersionRowDTO;
import com.bubblecloud.oa.api.entity.Program;
import com.bubblecloud.oa.api.entity.ProgramMember;
import com.bubblecloud.oa.api.entity.ProgramVersion;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 项目版本实现。
 *
 * @author qinlei
 * @date 2026/4/8 13:00
 */
@Service
@RequiredArgsConstructor
public class ProgramVersionOaServiceImpl implements ProgramVersionOaService {

	private final ProgramVersionMapper programVersionMapper;

	private final ProgramMapper programMapper;

	private final ProgramMemberMapper programMemberMapper;

	@Override
	public List<ProgramVersion> listByProgram(Long programId, Long operatorUid) {
		assertProgramVisible(programId, operatorUid);
		return programVersionMapper.selectList(Wrappers.lambdaQuery(ProgramVersion.class)
			.eq(ProgramVersion::getProgramId, programId)
			.orderByDesc(ProgramVersion::getSort)
			.orderByDesc(ProgramVersion::getCreatedAt));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveVersions(Long programId, List<ProgramVersionRowDTO> data, Long operatorUid) {
		Program program = programMapper.selectById(programId);
		if (ObjectUtil.isNull(program)) {
			throw new IllegalArgumentException("未找到指定项目记录");
		}
		assertProgramVisible(programId, operatorUid);
		if (CollUtil.isEmpty(data)) {
			data = List.of();
		}
		List<ProgramVersion> existing = programVersionMapper
			.selectList(Wrappers.lambdaQuery(ProgramVersion.class).eq(ProgramVersion::getProgramId, programId));
		Map<Long, ProgramVersion> byId = existing.stream()
			.filter(v -> ObjectUtil.isNotNull(v.getId()))
			.collect(Collectors.toMap(ProgramVersion::getId, v -> v, (a, b) -> a));
		Set<Long> keep = new HashSet<>();
		int num = data.size();
		long creatorUid = ObjectUtil.defaultIfNull(operatorUid, 0L);
		for (ProgramVersionRowDTO row : data) {
			if (ObjectUtil.isNull(row.getId()) || row.getId() < 1) {
				ProgramVersion v = new ProgramVersion();
				v.setProgramId(programId);
				v.setName(StrUtil.nullToEmpty(row.getName()));
				v.setSort(num);
				v.setCreatorUid(creatorUid);
				v.setCreatedAt(LocalDateTime.now());
				v.setUpdatedAt(LocalDateTime.now());
				programVersionMapper.insert(v);
			}
			else {
				ProgramVersion v = byId.get(row.getId());
				if (ObjectUtil.isNull(v) || !ObjectUtil.equal(v.getProgramId(), programId)) {
					throw new IllegalArgumentException("版本数据异常");
				}
				v.setName(StrUtil.nullToEmpty(row.getName()));
				v.setSort(num);
				v.setUpdatedAt(LocalDateTime.now());
				programVersionMapper.updateById(v);
				keep.add(v.getId());
			}
			num--;
		}
		List<Long> toDelete = existing.stream().map(ProgramVersion::getId).filter(id -> !keep.contains(id)).toList();
		if (CollUtil.isNotEmpty(toDelete)) {
			programVersionMapper.delete(Wrappers.lambdaQuery(ProgramVersion.class)
				.eq(ProgramVersion::getProgramId, programId)
				.in(ProgramVersion::getId, toDelete));
		}
	}

	@Override
	public List<ProgramVersion> selectList(Long programId, Long operatorUid) {
		List<Long> ids = visibleProgramIds(operatorUid);
		if (CollUtil.isEmpty(ids)) {
			return List.of();
		}
		if (ObjectUtil.isNotNull(programId) && programId > 0) {
			if (!ids.contains(programId)) {
				return List.of();
			}
			return programVersionMapper.selectList(Wrappers.lambdaQuery(ProgramVersion.class)
				.eq(ProgramVersion::getProgramId, programId)
				.orderByDesc(ProgramVersion::getSort)
				.orderByAsc(ProgramVersion::getId));
		}
		return programVersionMapper.selectList(Wrappers.lambdaQuery(ProgramVersion.class)
			.in(ProgramVersion::getProgramId, ids)
			.orderByDesc(ProgramVersion::getSort)
			.orderByAsc(ProgramVersion::getId));
	}

	private void assertProgramVisible(Long programId, Long operatorUid) {
		List<Long> ids = visibleProgramIds(operatorUid);
		if (!ids.contains(programId)) {
			throw new IllegalArgumentException("未找到指定项目记录");
		}
	}

	private List<Long> visibleProgramIds(Long operatorUid) {
		long u = ObjectUtil.defaultIfNull(operatorUid, 0L);
		if (u <= 0) {
			return List.of();
		}
		Set<Long> out = new LinkedHashSet<>();
		List<Program> lead = programMapper.selectList(Wrappers.lambdaQuery(Program.class)
			.and(w -> w.eq(Program::getUid, u).or().eq(Program::getCreatorUid, u)));
		for (Program p : lead) {
			out.add(p.getId());
		}
		List<ProgramMember> mem = programMemberMapper
			.selectList(Wrappers.lambdaQuery(ProgramMember.class).eq(ProgramMember::getUid, u));
		for (ProgramMember m : mem) {
			out.add(m.getProgramId());
		}
		return new ArrayList<>(out);
	}

}
