package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.ProgramOaConstants;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.ProgramMapper;
import com.bubblecloud.biz.oa.mapper.ProgramMemberMapper;
import com.bubblecloud.biz.oa.mapper.ProgramTaskMapper;
import com.bubblecloud.biz.oa.mapper.ProgramTaskMemberMapper;
import com.bubblecloud.biz.oa.mapper.ProgramTaskCommentMapper;
import com.bubblecloud.biz.oa.mapper.ProgramVersionMapper;
import com.bubblecloud.biz.oa.service.ProgramDynamicOaService;
import com.bubblecloud.biz.oa.service.ProgramTaskOaService;
import com.bubblecloud.biz.oa.util.ProgramTreeUtil;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.program.ProgramTaskBatchDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskSortDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskStoreDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskSubordinateDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskUpdateDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Program;
import com.bubblecloud.oa.api.entity.ProgramMember;
import com.bubblecloud.oa.api.entity.ProgramTask;
import com.bubblecloud.oa.api.entity.ProgramTaskComment;
import com.bubblecloud.oa.api.entity.ProgramTaskMember;
import com.bubblecloud.oa.api.entity.ProgramVersion;
import com.bubblecloud.oa.api.vo.CreatedIdVO;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.program.ProgramAdminLiteVO;
import com.bubblecloud.oa.api.vo.program.ProgramMiniVO;
import com.bubblecloud.oa.api.vo.program.ProgramTaskNodeVO;
import com.bubblecloud.oa.api.vo.program.ProgramTaskSelectNodeVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 项目任务实现。
 *
 * @author qinlei
 * @date 2026/4/8 14:30
 */
@Service
@RequiredArgsConstructor
public class ProgramTaskOaServiceImpl implements ProgramTaskOaService {

	private final ProgramTaskMapper programTaskMapper;

	private final ProgramTaskMemberMapper programTaskMemberMapper;

	private final ProgramTaskCommentMapper programTaskCommentMapper;

	private final ProgramMemberMapper programMemberMapper;

	private final ProgramMapper programMapper;

	private final ProgramVersionMapper programVersionMapper;

	private final AdminMapper adminMapper;

	private final ProgramDynamicOaService programDynamicOaService;

	private final ObjectMapper objectMapper;

	@Override
	public ListCountVO<ProgramTaskNodeVO> pageTaskTree(Pg pg, ProgramTask query, Long operatorUid) {
		long adminUid = ObjectUtil.defaultIfNull(operatorUid, 0L);
		query.setAdminUid(adminUid);
		boolean authScope = !(ObjectUtil.defaultIfNull(query.getPid(), 0L) > 0);
		List<Long> participantTaskIds = null;
		if (authScope && ObjectUtil.equal(query.getTypes(), 2)) {
			participantTaskIds = programTaskMapper.selectTaskIdsByMemberUid(adminUid);
		}
		List<ProgramTask> flat = programTaskMapper.selectFlatList(query, participantTaskIds, authScope);
		if (flat.isEmpty()) {
			return ListCountVO.of(List.of(), 0);
		}
		Map<Long, ProgramTaskNodeVO> voById = new LinkedHashMap<>();
		for (ProgramTask t : flat) {
			voById.put(t.getId(), skeleton(t));
		}
		enrichNodes(voById, flat, operatorUid);
		Comparator<ProgramTaskNodeVO> ord = Comparator
			.comparing(ProgramTaskNodeVO::getSort, Comparator.nullsLast(Integer::compareTo))
			.thenComparing(ProgramTaskNodeVO::getId);
		List<ProgramTaskNodeVO> roots = ProgramTreeUtil.buildForest(new ArrayList<>(voById.values()),
				ProgramTaskNodeVO::getId, n -> ObjectUtil.defaultIfNull(n.getPid(), 0L), ProgramTaskNodeVO::getChildren,
				ProgramTaskNodeVO::setChildren, ord);
		long total = roots.size();
		long page = ObjectUtil.defaultIfNull(pg.getCurrent(), 1L);
		long size = ObjectUtil.defaultIfNull(pg.getSize(), 20L);
		int from = (int) Math.max(0, (page - 1) * size);
		int to = (int) Math.min(from + size, roots.size());
		List<ProgramTaskNodeVO> slice = from >= roots.size() ? List.of() : roots.subList(from, to);
		setOperateRecursive(slice, operatorUid);
		return ListCountVO.of(slice, total);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public CreatedIdVO store(ProgramTaskStoreDTO dto, Long operatorUid) {
		List<Long> programMembers = programMemberUids(dto.getProgramId());
		validateProgram(dto.getProgramId());
		if (ObjectUtil.isNotNull(dto.getVersionId()) && dto.getVersionId() > 0) {
			validateVersion(dto.getProgramId(), dto.getVersionId());
		}
		if (ObjectUtil.defaultIfNull(dto.getUid(), 0L) > 0 && !programMembers.contains(dto.getUid())) {
			throw new IllegalArgumentException("请重新选择负责人");
		}
		if (CollUtil.isNotEmpty(dto.getMembers())
				&& dto.getMembers().stream().anyMatch(u -> !programMembers.contains(u))) {
			throw new IllegalArgumentException("请重新选择协作者");
		}
		ProgramTask t = new ProgramTask();
		t.setName(StrUtil.nullToEmpty(dto.getName()));
		t.setIdent(generateIdent());
		t.setProgramId(dto.getProgramId());
		t.setVersionId(ObjectUtil.defaultIfNull(dto.getVersionId(), 0L));
		t.setUid(ObjectUtil.defaultIfNull(dto.getUid(), 0L));
		t.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 0));
		t.setPriority(ObjectUtil.defaultIfNull(dto.getPriority(), 0));
		t.setDescribe(StrUtil.nullToEmpty(dto.getDescribe()));
		t.setPlanStart(dto.getPlanStart());
		t.setPlanEnd(dto.getPlanEnd());
		t.setCreatorUid(ObjectUtil.defaultIfNull(operatorUid, 0L));
		t.setCreatedAt(LocalDateTime.now());
		t.setUpdatedAt(LocalDateTime.now());
		applyParentFromPath(t, dto.getPath(), dto.getProgramId());
		if (t.getPlanStart() != null && t.getPlanEnd() != null && t.getPlanStart().isAfter(t.getPlanEnd())) {
			throw new IllegalArgumentException("开始时间不能大于结束时间");
		}
		int mx = ObjectUtil.defaultIfNull(programTaskMapper.selectMaxSort(t.getPid(), t.getProgramId()), 0);
		t.setSort(mx + 1);
		programTaskMapper.insert(t);
		replaceMembers(t.getId(), dto.getMembers());
		programDynamicOaService.addLog(ProgramOaConstants.DYNAMIC_TYPE_TASK, ProgramOaConstants.DYNAMIC_ACTION_CREATE,
				operatorUid, t.getId(), "新建了 <b>任务</b>", "[]");
		return new CreatedIdVO(t.getId());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public CreatedIdVO saveSubordinate(ProgramTaskSubordinateDTO dto, Long operatorUid) {
		ProgramTask parent = programTaskMapper.selectById(dto.getPid());
		if (ObjectUtil.isNull(parent)) {
			throw new IllegalArgumentException("请选择父级任务");
		}
		if (ObjectUtil.defaultIfNull(parent.getLevel(), 1) >= 4) {
			throw new IllegalArgumentException("最多支持4级任务");
		}
		LocalDate today = LocalDate.now();
		ProgramTask t = new ProgramTask();
		t.setName(StrUtil.nullToEmpty(dto.getName()));
		t.setIdent(generateIdent());
		t.setPid(parent.getId());
		t.setPath(childPath(parent));
		t.setTopId(ObjectUtil.equal(parent.getLevel(), 1) ? parent.getId() : parent.getTopId());
		t.setLevel(ObjectUtil.defaultIfNull(parent.getLevel(), 1) + 1);
		t.setProgramId(parent.getProgramId());
		t.setVersionId(ObjectUtil.defaultIfNull(parent.getVersionId(), 0L));
		t.setUid(parent.getUid());
		t.setStatus(0);
		t.setPriority(0);
		t.setDescribe("");
		t.setPlanStart(today);
		t.setPlanEnd(today);
		t.setCreatorUid(ObjectUtil.defaultIfNull(operatorUid, 0L));
		t.setCreatedAt(LocalDateTime.now());
		t.setUpdatedAt(LocalDateTime.now());
		int mx = ObjectUtil.defaultIfNull(programTaskMapper.selectMaxSort(t.getPid(), t.getProgramId()), 0);
		t.setSort(mx + 1);
		programTaskMapper.insert(t);
		programDynamicOaService.addLog(ProgramOaConstants.DYNAMIC_TYPE_TASK, ProgramOaConstants.DYNAMIC_ACTION_CREATE,
				operatorUid, t.getId(), "新建了 <b>任务</b>", "[]");
		return new CreatedIdVO(t.getId());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(Long id, ProgramTaskUpdateDTO dto, Long operatorUid) {
		ProgramTask task = programTaskMapper.selectById(id);
		if (ObjectUtil.isNull(task)) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (!canEditTask(task, operatorUid)) {
			throw new IllegalArgumentException("您没有权限修改该任务");
		}
		String field = StrUtil.nullToEmpty(dto.getField());
		if (StrUtil.equals(field, "plan_date")) {
			if (dto.getPlanStart() != null && dto.getPlanEnd() != null
					&& dto.getPlanStart().isAfter(dto.getPlanEnd())) {
				throw new IllegalArgumentException("请重新选择计划时间, 开始时间需要小于结束时间");
			}
			task.setPlanStart(dto.getPlanStart());
			task.setPlanEnd(dto.getPlanEnd());
			task.setUpdatedAt(LocalDateTime.now());
			programTaskMapper.updateById(task);
			return;
		}
		if (StrUtil.isBlank(field)) {
			throw new IllegalArgumentException("修改的数据不存在");
		}
		switch (field) {
			case "name" -> {
				if (StrUtil.isBlank(dto.getName())) {
					throw new IllegalArgumentException("修改的数据不存在");
				}
				logTaskUpdate(task, operatorUid, "修改了 <b>名称</b>", describePair(task.getName(), dto.getName()));
				task.setName(dto.getName());
			}
			case "describe" -> {
				logTaskUpdate(task, operatorUid, "修改了 <b>描述</b>", describePair(task.getDescribe(), dto.getDescribe()));
				task.setDescribe(StrUtil.nullToEmpty(dto.getDescribe()));
			}
			case "status" -> {
				if (ObjectUtil.isNull(dto.getStatus())) {
					throw new IllegalArgumentException("修改的数据不存在");
				}
				logTaskUpdate(task, operatorUid, "将 <b>状态</b> 更新", "[]");
				task.setStatus(dto.getStatus());
			}
			case "priority" -> {
				if (ObjectUtil.isNull(dto.getPriority())) {
					throw new IllegalArgumentException("修改的数据不存在");
				}
				logTaskUpdate(task, operatorUid, "将 <b>优先级</b> 更新", "[]");
				task.setPriority(dto.getPriority());
			}
			case "uid" -> {
				List<Long> pm = programMemberUids(task.getProgramId());
				if (ObjectUtil.defaultIfNull(dto.getUid(), 0L) > 0 && !pm.contains(dto.getUid())) {
					throw new IllegalArgumentException("请重新选择负责人");
				}
				logTaskUpdate(task, operatorUid, "将 <b>负责人</b> 更新", "[]");
				task.setUid(ObjectUtil.defaultIfNull(dto.getUid(), 0L));
			}
			case "version_id" -> {
				if (ObjectUtil.defaultIfNull(dto.getVersionId(), 0L) > 0) {
					validateVersion(task.getProgramId(), dto.getVersionId());
				}
				logTaskUpdate(task, operatorUid, "将 <b>关联版本</b> 更新", "[]");
				task.setVersionId(ObjectUtil.defaultIfNull(dto.getVersionId(), 0L));
			}
			case "program_id" -> {
				if (ObjectUtil.defaultIfNull(dto.getProgramId(), 0L) < 1) {
					throw new IllegalArgumentException("修改的数据不存在");
				}
				if (ObjectUtil.defaultIfNull(task.getPid(), 0L) > 0) {
					throw new IllegalArgumentException("当前任务存在父级任务，无法修改关联项目");
				}
				validateProgram(dto.getProgramId());
				logTaskUpdate(task, operatorUid, "将 <b>关联项目</b> 更新", "[]");
				task.setProgramId(dto.getProgramId());
				task.setVersionId(0L);
				List<Long> pm = programMemberUids(task.getProgramId());
				if (task.getUid() > 0 && !pm.contains(task.getUid())) {
					task.setUid(0L);
				}
				replaceMembers(task.getId(), filterMembers(task.getId(), pm));
			}
			case "pid" -> {
				long npid = ObjectUtil.defaultIfNull(dto.getPid(), 0L);
				applyPidChange(task, npid, operatorUid);
			}
			case "members" -> {
				List<Long> pm = programMemberUids(task.getProgramId());
				List<Long> next = dto.getMembers() == null ? List.of() : dto.getMembers();
				if (next.stream().anyMatch(u -> !pm.contains(u))) {
					throw new IllegalArgumentException("请重新选择协作者");
				}
				logTaskUpdate(task, operatorUid, "将 <b>协作者</b> 更新", "[]");
				replaceMembers(task.getId(), next);
			}
			case "plan_start" -> {
				task.setPlanStart(dto.getPlanStart());
				if (dto.getPlanStart() != null && task.getPlanEnd() != null
						&& dto.getPlanStart().isAfter(task.getPlanEnd())) {
					task.setPlanEnd(null);
				}
			}
			case "plan_end" -> {
				task.setPlanEnd(dto.getPlanEnd());
				if (dto.getPlanEnd() != null && task.getPlanStart() != null
						&& task.getPlanStart().isAfter(dto.getPlanEnd())) {
					task.setPlanStart(null);
				}
			}
			default -> throw new IllegalArgumentException("修改的数据不存在");
		}
		task.setUpdatedAt(LocalDateTime.now());
		programTaskMapper.updateById(task);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Long id, Long operatorUid) {
		batchDelete(List.of(id), operatorUid);
	}

	@Override
	public ProgramTaskNodeVO info(Long id, Long operatorUid) {
		ProgramTask t = programTaskMapper.selectById(id);
		if (ObjectUtil.isNull(t)) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		Map<Long, ProgramTaskNodeVO> m = Map.of(id, skeleton(t));
		enrichNodes(new LinkedHashMap<>(m), List.of(t), operatorUid);
		ProgramTaskNodeVO vo = m.get(id);
		vo.setOperate(canOperateTask(t, operatorUid));
		return vo;
	}

	@Override
	public List<ProgramTaskSelectNodeVO> select(Long programId, Long pid, Long operatorUid) {
		List<Long> visible = visibleProgramIds(operatorUid);
		if (visible.isEmpty()) {
			return List.of();
		}
		long prog = ObjectUtil.defaultIfNull(programId, 0L);
		if (prog > 0 && !visible.contains(prog)) {
			prog = 0;
		}
		ProgramTask q = new ProgramTask();
		if (prog > 0) {
			q.setProgramId(prog);
		}
		else {
			q.setScopeProgramIds(visible);
		}
		q.setPid(pid);
		q.setAdminUid(ObjectUtil.defaultIfNull(operatorUid, 0L));
		q.setTypes(0);
		List<Long> participantTaskIds = programTaskMapper.selectTaskIdsByMemberUid(q.getAdminUid());
		List<ProgramTask> flat = programTaskMapper.selectFlatList(q, participantTaskIds, true);
		List<ProgramTaskSelectNodeVO> nodes = flat.stream().map(this::toSelectNode).collect(Collectors.toList());
		Comparator<ProgramTaskSelectNodeVO> ord = Comparator.comparing(ProgramTaskSelectNodeVO::getValue);
		return ProgramTreeUtil.buildForest(nodes, ProgramTaskSelectNodeVO::getValue,
				n -> ObjectUtil.defaultIfNull(n.getPid(), 0L), ProgramTaskSelectNodeVO::getChildren,
				ProgramTaskSelectNodeVO::setChildren, ord);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchUpdate(ProgramTaskBatchDTO dto, Long operatorUid) {
		List<Long> ids = dto.getData() == null ? List.of()
				: dto.getData().stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
		if (ids.isEmpty()) {
			return;
		}
		if (dto.getStartDate() != null && dto.getEndDate() != null && dto.getStartDate().isAfter(dto.getEndDate())) {
			throw new IllegalArgumentException("开始时间不能大于结束时间");
		}
		if ((ObjectUtil.defaultIfNull(dto.getPid(), 0L) > 0 || ObjectUtil.defaultIfNull(dto.getVersionId(), 0L) > 0
				|| ObjectUtil.defaultIfNull(dto.getUid(), 0L) > 0)
				&& ObjectUtil.defaultIfNull(dto.getProgramId(), 0L) < 1) {
			throw new IllegalArgumentException("请选择关联项目");
		}
		for (Long id : ids) {
			ProgramTask t = programTaskMapper.selectById(id);
			if (ObjectUtil.isNull(t) || !canEditTask(t, operatorUid)) {
				continue;
			}
			if (ObjectUtil.isNotNull(dto.getProgramId()) && dto.getProgramId() > 0) {
				validateProgram(dto.getProgramId());
				t.setProgramId(dto.getProgramId());
			}
			if (ObjectUtil.isNotNull(dto.getVersionId())) {
				if (dto.getVersionId() > 0) {
					validateVersion(t.getProgramId(), dto.getVersionId());
				}
				t.setVersionId(dto.getVersionId());
			}
			if (ObjectUtil.isNotNull(dto.getPid())) {
				applyPidChange(t, dto.getPid(), operatorUid);
			}
			if (ObjectUtil.isNotNull(dto.getUid())) {
				List<Long> pm = programMemberUids(t.getProgramId());
				if (dto.getUid() > 0 && !pm.contains(dto.getUid())) {
					throw new IllegalArgumentException("请重新选择负责人");
				}
				t.setUid(dto.getUid());
			}
			if (ObjectUtil.isNotNull(dto.getStatus())) {
				t.setStatus(dto.getStatus());
			}
			if (dto.getStartDate() != null || dto.getEndDate() != null) {
				if (dto.getStartDate() != null) {
					t.setPlanStart(dto.getStartDate());
				}
				if (dto.getEndDate() != null) {
					t.setPlanEnd(dto.getEndDate());
				}
			}
			t.setUpdatedAt(LocalDateTime.now());
			programTaskMapper.updateById(t);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchDelete(List<Long> ids, Long operatorUid) {
		if (CollUtil.isEmpty(ids)) {
			return;
		}
		long op = ObjectUtil.defaultIfNull(operatorUid, 0L);
		for (Long id : ids.stream().filter(Objects::nonNull).distinct().toList()) {
			ProgramTask task = programTaskMapper.selectById(id);
			if (ObjectUtil.isNull(task)) {
				continue;
			}
			Program program = programMapper.selectById(task.getProgramId());
			if (ObjectUtil.isNull(program)) {
				continue;
			}
			if (!canForceDeleteTask(program, task, op)) {
				throw new IllegalArgumentException("删除失败，您暂无权限删除！");
			}
			List<ProgramTask> children = programTaskMapper.selectByPathContains(id);
			List<Long> allIds = new ArrayList<>();
			allIds.add(id);
			allIds.addAll(children.stream().map(ProgramTask::getId).toList());
			programDynamicOaService.addLog(ProgramOaConstants.DYNAMIC_TYPE_TASK,
					ProgramOaConstants.DYNAMIC_ACTION_DELETE, op, id,
					"删除了任务 </b>" + StrUtil.nullToEmpty(task.getName()) + "</b>", "[]");
			for (ProgramTask ch : children) {
				programDynamicOaService.addLog(ProgramOaConstants.DYNAMIC_TYPE_TASK,
						ProgramOaConstants.DYNAMIC_ACTION_DELETE, op, id,
						"删除了任务 </b>" + StrUtil.nullToEmpty(ch.getName()) + "</b>", "[]");
			}
			programTaskCommentMapper
				.delete(Wrappers.lambdaQuery(ProgramTaskComment.class).in(ProgramTaskComment::getTaskId, allIds));
			programTaskMemberMapper
				.delete(Wrappers.lambdaQuery(ProgramTaskMember.class).in(ProgramTaskMember::getTaskId, allIds));
			programTaskMapper.delete(Wrappers.lambdaQuery(ProgramTask.class).in(ProgramTask::getId, allIds));
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void sort(ProgramTaskSortDTO dto, Long operatorUid) {
		long c = ObjectUtil.defaultIfNull(dto.getCurrent(), 0L);
		long g = ObjectUtil.defaultIfNull(dto.getTarget(), 0L);
		if (c == g || c < 1 || g < 1) {
			return;
		}
		ProgramTask a = programTaskMapper.selectById(c);
		ProgramTask b = programTaskMapper.selectById(g);
		if (ObjectUtil.isNull(a) || ObjectUtil.isNull(b)) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (!ObjectUtil.equal(a.getPid(), b.getPid())) {
			throw new IllegalArgumentException("仅支持兄弟之间进行排序");
		}
		Integer sa = a.getSort();
		Integer sb = b.getSort();
		a.setSort(sb);
		b.setSort(sa);
		a.setUpdatedAt(LocalDateTime.now());
		b.setUpdatedAt(LocalDateTime.now());
		programTaskMapper.updateById(a);
		programTaskMapper.updateById(b);
	}

	@Override
	public ProgramTaskNodeVO share(String ident, Long operatorUid) {
		if (StrUtil.isBlank(ident)) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		ProgramTask t = programTaskMapper
			.selectOne(Wrappers.lambdaQuery(ProgramTask.class).eq(ProgramTask::getIdent, ident).last("LIMIT 1"));
		if (ObjectUtil.isNull(t)) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		ProgramTaskNodeVO vo = info(t.getId(), operatorUid);
		vo.setOperate(true);
		return vo;
	}

	private ProgramTaskSelectNodeVO toSelectNode(ProgramTask t) {
		ProgramTaskSelectNodeVO n = new ProgramTaskSelectNodeVO();
		n.setValue(t.getId());
		n.setLabel(t.getName());
		n.setLevel(t.getLevel());
		n.setPid(t.getPid());
		n.setDisabled(ObjectUtil.equal(t.getLevel(), 4));
		return n;
	}

	private void setOperateRecursive(List<ProgramTaskNodeVO> nodes, Long operatorUid) {
		if (nodes == null) {
			return;
		}
		for (ProgramTaskNodeVO n : nodes) {
			n.setOperate(canOperateTaskLite(n, operatorUid));
			setOperateRecursive(n.getChildren(), operatorUid);
		}
	}

	private boolean canOperateTaskLite(ProgramTaskNodeVO n, Long operatorUid) {
		long op = ObjectUtil.defaultIfNull(operatorUid, 0L);
		return ObjectUtil.equal(n.getUid(), op) || ObjectUtil.equal(n.getCreatorUid(), op);
	}

	private boolean canOperateTask(ProgramTask t, Long operatorUid) {
		return canEditTask(t, operatorUid);
	}

	private boolean canEditTask(ProgramTask task, Long operatorUid) {
		long op = ObjectUtil.defaultIfNull(operatorUid, 0L);
		if (ObjectUtil.equal(task.getUid(), op) || ObjectUtil.equal(task.getCreatorUid(), op)) {
			return true;
		}
		return programMemberMapper.selectCount(Wrappers.lambdaQuery(ProgramMember.class)
			.eq(ProgramMember::getProgramId, task.getProgramId())
			.eq(ProgramMember::getUid, op)) > 0;
	}

	private boolean canForceDeleteTask(Program program, ProgramTask task, Long operatorUid) {
		long op = ObjectUtil.defaultIfNull(operatorUid, 0L);
		return ObjectUtil.equal(program.getUid(), op) || ObjectUtil.equal(program.getCreatorUid(), op)
				|| ObjectUtil.equal(task.getCreatorUid(), op);
	}

	private void applyPidChange(ProgramTask task, long newPid, Long operatorUid) {
		if (newPid < 1) {
			task.setPid(0L);
			task.setPath("");
			task.setLevel(1);
			task.setTopId(0L);
			return;
		}
		ProgramTask parent = programTaskMapper.selectById(newPid);
		if (ObjectUtil.isNull(parent) || !ObjectUtil.equal(parent.getProgramId(), task.getProgramId())) {
			throw new IllegalArgumentException("请重新选择父级任务");
		}
		if (ObjectUtil.equal(parent.getId(), task.getId())) {
			throw new IllegalArgumentException("请重新选择父级任务");
		}
		if (pathContainsId(parent.getPath(), task.getId())) {
			throw new IllegalArgumentException("请重新选择父级任务");
		}
		if (ObjectUtil.defaultIfNull(parent.getLevel(), 1) >= 4) {
			throw new IllegalArgumentException("最多支持4级任务");
		}
		task.setPid(parent.getId());
		task.setPath(childPath(parent));
		task.setLevel(ObjectUtil.defaultIfNull(parent.getLevel(), 1) + 1);
		task.setTopId(ObjectUtil.equal(parent.getLevel(), 1) ? parent.getId() : parent.getTopId());
		int mx = ObjectUtil.defaultIfNull(programTaskMapper.selectMaxSort(task.getPid(), task.getProgramId()), 0);
		task.setSort(mx + 1);
	}

	private static boolean pathContainsId(String path, Long id) {
		if (id == null || StrUtil.isBlank(path)) {
			return false;
		}
		return path.contains("/" + id + "/");
	}

	private List<Long> filterMembers(Long taskId, List<Long> allowed) {
		List<ProgramTaskMember> ms = programTaskMemberMapper
			.selectList(Wrappers.lambdaQuery(ProgramTaskMember.class).eq(ProgramTaskMember::getTaskId, taskId));
		return ms.stream().map(ProgramTaskMember::getUid).filter(allowed::contains).collect(Collectors.toList());
	}

	private void logTaskUpdate(ProgramTask task, Long operatorUid, String title, String describeJson) {
		programDynamicOaService.addLog(ProgramOaConstants.DYNAMIC_TYPE_TASK, ProgramOaConstants.DYNAMIC_ACTION_UPDATE,
				operatorUid, task.getId(), title, describeJson);
	}

	private String describePair(String before, String after) {
		try {
			return objectMapper
				.writeValueAsString(List.of(Map.of("title", "修改前：", "value", StrUtil.nullToEmpty(before)),
						Map.of("title", "修改后：", "value", StrUtil.nullToEmpty(after))));
		}
		catch (Exception e) {
			return "[]";
		}
	}

	private void validateProgram(Long programId) {
		if (ObjectUtil.isNull(programId) || programId < 1 || ObjectUtil.isNull(programMapper.selectById(programId))) {
			throw new IllegalArgumentException("请重新选择关联项目");
		}
	}

	private void validateVersion(Long programId, Long versionId) {
		long c = programVersionMapper.selectCount(Wrappers.lambdaQuery(ProgramVersion.class)
			.eq(ProgramVersion::getProgramId, programId)
			.eq(ProgramVersion::getId, versionId));
		if (c < 1) {
			throw new IllegalArgumentException("请重新选择关联版本");
		}
	}

	private List<Long> programMemberUids(Long programId) {
		List<ProgramMember> ms = programMemberMapper
			.selectList(Wrappers.lambdaQuery(ProgramMember.class).eq(ProgramMember::getProgramId, programId));
		return ms.stream().map(ProgramMember::getUid).collect(Collectors.toList());
	}

	private List<Long> visibleProgramIds(Long operatorUid) {
		long u = ObjectUtil.defaultIfNull(operatorUid, 0L);
		Set<Long> out = new LinkedHashSet<>();
		if (u < 1) {
			return List.of();
		}
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

	private void replaceMembers(Long taskId, List<Long> uids) {
		programTaskMemberMapper
			.delete(Wrappers.lambdaQuery(ProgramTaskMember.class).eq(ProgramTaskMember::getTaskId, taskId));
		if (CollUtil.isEmpty(uids)) {
			return;
		}
		LocalDateTime now = LocalDateTime.now();
		for (Long uid : uids) {
			if (ObjectUtil.isNull(uid) || uid < 1) {
				continue;
			}
			ProgramTaskMember m = new ProgramTaskMember();
			m.setTaskId(taskId);
			m.setUid(uid);
			m.setCreatedAt(now);
			m.setUpdatedAt(now);
			programTaskMemberMapper.insert(m);
		}
	}

	private void applyParentFromPath(ProgramTask t, List<Long> path, Long programId) {
		t.setPid(0L);
		t.setPath("");
		t.setLevel(1);
		t.setTopId(0L);
		if (CollUtil.isEmpty(path)) {
			return;
		}
		long parentId = path.get(path.size() - 1);
		ProgramTask parent = programTaskMapper.selectOne(Wrappers.lambdaQuery(ProgramTask.class)
			.eq(ProgramTask::getId, parentId)
			.eq(ProgramTask::getProgramId, programId)
			.last("LIMIT 1"));
		if (ObjectUtil.isNull(parent)) {
			throw new IllegalArgumentException("请重新选择父级任务");
		}
		if (ObjectUtil.defaultIfNull(parent.getLevel(), 1) >= 4) {
			throw new IllegalArgumentException("最多支持4级任务");
		}
		if (!ObjectUtil.equal(parent.getProgramId(), programId)) {
			throw new IllegalArgumentException("当前任务存在父级任务，无法修改关联项目");
		}
		t.setPid(parent.getId());
		t.setPath(childPath(parent));
		t.setLevel(ObjectUtil.defaultIfNull(parent.getLevel(), 1) + 1);
		t.setTopId(ObjectUtil.equal(parent.getLevel(), 1) ? parent.getId() : parent.getTopId());
	}

	private static String childPath(ProgramTask parent) {
		String p = StrUtil.nullToEmpty(parent.getPath());
		if (!p.endsWith("/")) {
			p = p + "/";
		}
		if (!p.startsWith("/") && StrUtil.isNotBlank(p)) {
			p = "/" + p;
		}
		return p + parent.getId() + "/";
	}

	private String generateIdent() {
		for (int i = 0; i < 20; i++) {
			String s = RandomUtil.randomString("abcdefghijklmnopqrstuvwxyz0123456789", 7);
			long c = programTaskMapper
				.selectCount(Wrappers.lambdaQuery(ProgramTask.class).eq(ProgramTask::getIdent, s));
			if (c == 0) {
				return s;
			}
		}
		throw new IllegalStateException("任务编号生成失败");
	}

	private ProgramTaskNodeVO skeleton(ProgramTask t) {
		ProgramTaskNodeVO n = new ProgramTaskNodeVO();
		n.setId(t.getId());
		n.setName(t.getName());
		n.setProgramId(t.getProgramId());
		n.setVersionId(t.getVersionId());
		n.setIdent(t.getIdent());
		n.setLevel(t.getLevel());
		n.setPid(t.getPid());
		n.setCreatorUid(t.getCreatorUid());
		n.setUid(t.getUid());
		n.setStatus(t.getStatus());
		n.setPriority(t.getPriority());
		n.setPlanStart(t.getPlanStart());
		n.setPlanEnd(t.getPlanEnd());
		n.setSort(t.getSort());
		n.setDescribe(t.getDescribe());
		n.setCreatedAt(t.getCreatedAt());
		n.setUpdatedAt(t.getUpdatedAt());
		n.setPath(parsePathIds(t.getPath()));
		return n;
	}

	private void enrichNodes(Map<Long, ProgramTaskNodeVO> voById, List<ProgramTask> flat, Long operatorUid) {
		Set<Long> pids = flat.stream().map(ProgramTask::getProgramId).collect(Collectors.toSet());
		Set<Long> vids = flat.stream()
			.map(ProgramTask::getVersionId)
			.filter(v -> v != null && v > 0)
			.collect(Collectors.toSet());
		Set<Long> uids = new HashSet<>();
		for (ProgramTask t : flat) {
			if (t.getUid() != null && t.getUid() > 0) {
				uids.add(t.getUid());
			}
			if (t.getCreatorUid() != null && t.getCreatorUid() > 0) {
				uids.add(t.getCreatorUid());
			}
			if (t.getPid() != null && t.getPid() > 0) {
				uids.add(t.getPid());
			}
		}
		Map<Long, Program> programs = pids.isEmpty() ? Map.of()
				: programMapper.selectList(Wrappers.lambdaQuery(Program.class).in(Program::getId, pids))
					.stream()
					.collect(Collectors.toMap(Program::getId, p -> p));
		Map<Long, ProgramVersion> versions = vids.isEmpty() ? Map.of()
				: programVersionMapper
					.selectList(Wrappers.lambdaQuery(ProgramVersion.class).in(ProgramVersion::getId, vids))
					.stream()
					.collect(Collectors.toMap(ProgramVersion::getId, v -> v));
		Map<Long, Admin> admins = uids.isEmpty() ? Map.of()
				: adminMapper.selectList(Wrappers.lambdaQuery(Admin.class).in(Admin::getId, uids))
					.stream()
					.collect(Collectors.toMap(Admin::getId, a -> a));
		Map<Long, List<ProgramTaskMember>> memByTask = new HashMap<>();
		if (!flat.isEmpty()) {
			List<Long> taskIds = flat.stream().map(ProgramTask::getId).toList();
			List<ProgramTaskMember> all = programTaskMemberMapper
				.selectList(Wrappers.lambdaQuery(ProgramTaskMember.class).in(ProgramTaskMember::getTaskId, taskIds));
			for (ProgramTaskMember m : all) {
				memByTask.computeIfAbsent(m.getTaskId(), k -> new ArrayList<>()).add(m);
			}
		}
		Map<Long, ProgramTask> taskById = flat.stream().collect(Collectors.toMap(ProgramTask::getId, x -> x));
		for (ProgramTask t : flat) {
			ProgramTaskNodeVO n = voById.get(t.getId());
			Program p = programs.get(t.getProgramId());
			if (p != null) {
				ProgramMiniVO pm = new ProgramMiniVO();
				pm.setId(p.getId());
				pm.setName(p.getName());
				n.setProgram(pm);
			}
			if (t.getVersionId() != null && t.getVersionId() > 0) {
				ProgramVersion v = versions.get(t.getVersionId());
				if (v != null) {
					ProgramMiniVO vm = new ProgramMiniVO();
					vm.setId(v.getId());
					vm.setName(v.getName());
					n.setVersion(vm);
				}
			}
			Admin assignee = admins.get(t.getUid());
			if (assignee != null) {
				n.getAdmins().add(toLite(assignee));
			}
			Admin cr = admins.get(t.getCreatorUid());
			if (cr != null) {
				n.getCreator().add(toLite(cr));
			}
			for (ProgramTaskMember m : memByTask.getOrDefault(t.getId(), List.of())) {
				Admin ma = admins.get(m.getUid());
				if (ma != null) {
					n.getMembers().add(toLite(ma));
				}
			}
			if (t.getPid() != null && t.getPid() > 0) {
				ProgramTask pt = taskById.get(t.getPid());
				if (pt != null) {
					ProgramMiniVO par = new ProgramMiniVO();
					par.setId(pt.getId());
					par.setName(pt.getName());
					n.setParent(par);
				}
			}
		}
	}

	private static ProgramAdminLiteVO toLite(Admin a) {
		ProgramAdminLiteVO v = new ProgramAdminLiteVO();
		v.setId(a.getId());
		v.setName(a.getName());
		v.setAvatar(a.getAvatar());
		v.setUid(a.getUid());
		v.setPhone(a.getPhone());
		return v;
	}

	private static List<Long> parsePathIds(String path) {
		if (StrUtil.isBlank(path)) {
			return new ArrayList<>();
		}
		String p = path.trim();
		if (p.startsWith("/")) {
			p = p.substring(1);
		}
		if (p.endsWith("/")) {
			p = p.substring(0, p.length() - 1);
		}
		if (StrUtil.isBlank(p)) {
			return new ArrayList<>();
		}
		List<Long> out = new ArrayList<>();
		for (String s : p.split("/")) {
			if (StrUtil.isBlank(s)) {
				continue;
			}
			try {
				out.add(Long.parseLong(s.trim()));
			}
			catch (NumberFormatException ignored) {
			}
		}
		return out;
	}

}
