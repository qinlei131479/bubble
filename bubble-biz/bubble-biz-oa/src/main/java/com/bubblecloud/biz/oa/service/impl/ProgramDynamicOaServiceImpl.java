package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bubblecloud.biz.oa.constant.ProgramOaConstants;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.ProgramDynamicMapper;
import com.bubblecloud.biz.oa.mapper.ProgramTaskMapper;
import com.bubblecloud.biz.oa.service.ProgramDynamicOaService;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.ProgramDynamic;
import com.bubblecloud.oa.api.entity.ProgramTask;
import com.bubblecloud.oa.api.vo.program.ProgramDynamicListVO;
import com.bubblecloud.oa.api.vo.program.ProgramDynamicRowVO;
import com.bubblecloud.oa.api.vo.program.ProgramTaskTinyVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 项目/任务动态实现。
 *
 * @author qinlei
 * @date 2026/4/8 12:30
 */
@Service
@RequiredArgsConstructor
public class ProgramDynamicOaServiceImpl implements ProgramDynamicOaService {

	private final ProgramDynamicMapper programDynamicMapper;

	private final AdminMapper adminMapper;

	private final ProgramTaskMapper programTaskMapper;

	private final ObjectMapper objectMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addLog(int types, int actionType, Long operatorUid, Long relationId, String title,
			String describeJson) {
		Admin op = adminMapper.selectById(operatorUid);
		String opName = op == null ? "" : StrUtil.nullToEmpty(op.getName());
		ProgramDynamic d = new ProgramDynamic();
		d.setTypes(types);
		d.setActionType(actionType);
		d.setUid(ObjectUtil.defaultIfNull(operatorUid, 0L));
		d.setOperator(opName);
		d.setRelationId(ObjectUtil.defaultIfNull(relationId, 0L));
		d.setTitle(StrUtil.nullToEmpty(title));
		d.setDescribe(StrUtil.nullToEmpty(describeJson));
		d.setCreatedAt(LocalDateTime.now());
		d.setUpdatedAt(LocalDateTime.now());
		programDynamicMapper.insert(d);
	}

	@Override
	public ProgramDynamicListVO pageProgramDynamics(Pg pg, Long uid, Long relationId) {
		return pageInternal(ProgramOaConstants.DYNAMIC_TYPE_PROGRAM, relationId, null, uid, pg, false);
	}

	@Override
	public ProgramDynamicListVO pageTaskDynamics(Pg pg, Long uid, Long programId, Long relationId) {
		boolean withTotal = ObjectUtil.isNotNull(relationId) && relationId > 0;
		return pageInternal(ProgramOaConstants.DYNAMIC_TYPE_TASK, relationId, programId, uid, pg, withTotal);
	}

	private ProgramDynamicListVO pageInternal(int types, Long relationId, Long programId, Long uid, Pg pg,
			boolean withRelationTotal) {
		long page = ObjectUtil.defaultIfNull(pg.getCurrent(), 1L);
		long size = ObjectUtil.defaultIfNull(pg.getSize(), 20L);
		long offset = (page - 1) * size;
		long total = programDynamicMapper.countDynamicPage(types, relationId, programId, uid);
		List<ProgramDynamic> rows = total == 0 ? List.of()
				: programDynamicMapper.selectDynamicPage(types, relationId, programId, uid, offset, size);
		List<ProgramDynamicRowVO> list = new ArrayList<>();
		for (ProgramDynamic d : rows) {
			list.add(toRow(d, types));
		}
		Long totalCount = null;
		if (withRelationTotal && ObjectUtil.isNotNull(relationId) && relationId > 0) {
			totalCount = programDynamicMapper.countDynamicPage(types, relationId, null, null);
		}
		return new ProgramDynamicListVO(list, total, totalCount);
	}

	private ProgramDynamicRowVO toRow(ProgramDynamic d, int types) {
		ProgramDynamicRowVO vo = new ProgramDynamicRowVO();
		vo.setId(d.getId());
		vo.setUid(d.getUid());
		vo.setOperator(d.getOperator());
		vo.setRelationId(d.getRelationId());
		vo.setActionType(d.getActionType());
		vo.setTitle(d.getTitle());
		vo.setCreatedAt(d.getCreatedAt());
		vo.setDescribe(parseDescribeJson(d.getDescribe()));
		if (types == ProgramOaConstants.DYNAMIC_TYPE_TASK) {
			ProgramTask t = programTaskMapper.selectById(d.getRelationId());
			if (ObjectUtil.isNotNull(t)) {
				ProgramTaskTinyVO tv = new ProgramTaskTinyVO();
				tv.setId(t.getId());
				tv.setName(t.getName());
				tv.setIdent(t.getIdent());
				tv.setProgramId(t.getProgramId());
				vo.setTask(tv);
			}
		}
		return vo;
	}

	private JsonNode parseDescribeJson(String raw) {
		if (StrUtil.isBlank(raw)) {
			return objectMapper.createArrayNode();
		}
		try {
			return objectMapper.readTree(raw);
		}
		catch (Exception e) {
			return objectMapper.createArrayNode();
		}
	}

}
