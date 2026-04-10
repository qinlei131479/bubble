package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.ProgramMapper;
import com.bubblecloud.biz.oa.mapper.ProgramMemberMapper;
import com.bubblecloud.biz.oa.mapper.ProgramTaskMapper;
import com.bubblecloud.biz.oa.service.ProgramOaService;
import com.bubblecloud.oa.api.dto.program.ProgramSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Program;
import com.bubblecloud.oa.api.entity.ProgramMember;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.program.ProgramListItemVO;
import com.bubblecloud.oa.api.vo.program.ProgramTaskStatisticsVO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;

/**
 * 项目管理实现。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Service
@RequiredArgsConstructor
public class ProgramOaServiceImpl implements ProgramOaService {

	private final ProgramMapper programMapper;

	private final ProgramMemberMapper programMemberMapper;

	private final ProgramTaskMapper programTaskMapper;

	private final AdminMapper adminMapper;

	@Override
	public ListCountVO<ProgramListItemVO> index(Page<Program> page, Program query, Long operatorUid) {
		Page<Program> res = programMapper.findPg(page, query);
		List<ProgramListItemVO> rows = new ArrayList<>();
		for (Program p : res.getRecords()) {
			rows.add(toRow(p, operatorUid));
		}
		return ListCountVO.of(rows, res.getTotal());
	}

	@Override
	public List<Program> selectOptions(Program query, Long operatorUid) {
		return programMapper.selectList(Wrappers.lambdaQuery(Program.class)
			.eq(ObjectUtil.isNotNull(query.getUid()), Program::getUid, query.getUid())
			.orderByDesc(Program::getId)
			.last("LIMIT 500"));
	}

	@Override
	public List<Admin> memberCards(Long programId) {
		List<Long> uids = programMemberMapper
			.selectList(Wrappers.lambdaQuery(ProgramMember.class).eq(ProgramMember::getProgramId, programId))
			.stream()
			.map(ProgramMember::getUid)
			.toList();
		if (uids.isEmpty()) {
			return List.of();
		}
		return adminMapper.selectList(Wrappers.lambdaQuery(Admin.class).in(Admin::getId, uids));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Program save(ProgramSaveDTO dto, Long creatorUid) {
		Program p = new Program();
		BeanUtil.copyProperties(dto, p);
		long n = programMapper.selectCount(Wrappers.emptyWrapper());
		p.setIdent(String.format("P%04d", n + 1));
		p.setCreatorUid(ObjectUtil.defaultIfNull(creatorUid, 0L));
		p.setCreatedAt(LocalDateTime.now());
		p.setUpdatedAt(LocalDateTime.now());
		programMapper.insert(p);
		replaceMembers(p.getId(), dto.getUid(), dto.getMembers());
		return p;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(Long id, ProgramSaveDTO dto, Long operatorUid) {
		Program existing = programMapper.selectById(id);
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException("数据不存在");
		}
		assertOperate(existing, operatorUid);
		BeanUtil.copyProperties(dto, existing, "id", "ident", "creatorUid", "createdAt");
		if (ObjectUtil.defaultIfNull(dto.getEid(), 0L) < 1) {
			existing.setCid(0L);
		}
		existing.setUpdatedAt(LocalDateTime.now());
		programMapper.updateById(existing);
		replaceMembers(id, dto.getUid(), dto.getMembers());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Long id, Long operatorUid) {
		Program existing = programMapper.selectById(id);
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException("数据不存在");
		}
		assertOperate(existing, operatorUid);
		programMapper.deleteById(id);
		programMemberMapper.delete(Wrappers.lambdaQuery(ProgramMember.class).eq(ProgramMember::getProgramId, id));
	}

	@Override
	public Program getInfo(Long id) {
		Program p = programMapper.selectById(id);
		if (ObjectUtil.isNull(p)) {
			throw new IllegalArgumentException("数据不存在");
		}
		return p;
	}

	private void assertOperate(Program p, Long operatorUid) {
		Long u = ObjectUtil.defaultIfNull(operatorUid, 0L);
		if (!ObjectUtil.equal(u, p.getUid()) && !ObjectUtil.equal(u, p.getCreatorUid())) {
			throw new IllegalArgumentException("您暂无权限操作！");
		}
	}

	private ProgramListItemVO toRow(Program p, Long operatorUid) {
		ProgramListItemVO vo = new ProgramListItemVO();
		BeanUtil.copyProperties(p, vo);
		Long u = ObjectUtil.defaultIfNull(operatorUid, 0L);
		vo.setOperate(ObjectUtil.equal(u, p.getUid()) || ObjectUtil.equal(u, p.getCreatorUid()));
		long total = programTaskMapper.countByProgramId(p.getId());
		long inc = programTaskMapper.countIncompleteByProgramId(p.getId());
		vo.setTaskStatistics(new ProgramTaskStatisticsVO(total, inc));
		return vo;
	}

	private void replaceMembers(Long programId, Long leaderUid, List<Long> members) {
		LinkedHashSet<Long> set = new LinkedHashSet<>();
		if (ObjectUtil.isNotNull(leaderUid)) {
			set.add(leaderUid);
		}
		if (CollUtil.isNotEmpty(members)) {
			set.addAll(members);
		}
		programMemberMapper
			.delete(Wrappers.lambdaQuery(ProgramMember.class).eq(ProgramMember::getProgramId, programId));
		LocalDateTime now = LocalDateTime.now();
		for (Long uid : set) {
			if (ObjectUtil.isNull(uid) || uid <= 0) {
				continue;
			}
			ProgramMember m = new ProgramMember();
			m.setProgramId(programId);
			m.setUid(uid);
			m.setCreatedAt(now);
			m.setUpdatedAt(now);
			programMemberMapper.insert(m);
		}
	}

}
