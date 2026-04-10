package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.ScheduleMapper;
import com.bubblecloud.biz.oa.mapper.ScheduleTypeMapper;
import com.bubblecloud.biz.oa.service.ScheduleTypeService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.ScheduleTypeSaveDTO;
import com.bubblecloud.oa.api.entity.Schedule;
import com.bubblecloud.oa.api.entity.ScheduleType;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleTypeVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 日程类型实现。
 *
 * @author qinlei
 * @date 2026/4/6 10:00
 */
@Service
@RequiredArgsConstructor
public class ScheduleTypeServiceImpl extends UpServiceImpl<ScheduleTypeMapper, ScheduleType>
		implements ScheduleTypeService {

	private final ScheduleMapper scheduleMapper;

	@Override
	public List<ScheduleTypeVO> listForUser(Long userId) {
		List<ScheduleType> rows = baseMapper.selectList(Wrappers.lambdaQuery(ScheduleType.class)
			.and(w -> w.eq(ScheduleType::getUserId, userId).or().eq(ScheduleType::getIsPublic, 1))
			.orderByAsc(ScheduleType::getSort)
			.orderByAsc(ScheduleType::getId));
		return rows.stream().map(this::toVo).collect(Collectors.toList());
	}

	private ScheduleTypeVO toVo(ScheduleType t) {
		return new ScheduleTypeVO(t.getId(), t.getName(), "", StrUtil.nullToEmpty(t.getColor()),
				StrUtil.nullToEmpty(t.getInfo()), ObjectUtil.defaultIfNull(t.getIsPublic(), 0));
	}

	@Override
	public OaElFormVO createForm(ObjectMapper om) {
		ArrayNode rule = scheduleTypeRules(om, null);
		return new OaElFormVO("添加日程类型", "post", "/ent/schedule/type/save", rule);
	}

	@Override
	public OaElFormVO editForm(Long id, Long userId, ObjectMapper om) {
		ScheduleType t = baseMapper.selectOne(Wrappers.lambdaQuery(ScheduleType.class)
			.eq(ScheduleType::getId, id)
			.eq(ScheduleType::getUserId, userId));
		if (ObjectUtil.isNull(t)) {
			throw new IllegalArgumentException("修改的日程类型不存在");
		}
		ArrayNode rule = scheduleTypeRules(om, t);
		return new OaElFormVO("修改日程类型", "put", "/ent/schedule/type/update/" + id, rule);
	}

	private ArrayNode scheduleTypeRules(ObjectMapper om, ScheduleType row) {
		ArrayNode rules = om.createArrayNode();
		rules.add(inputRule(om, "name", "类型名称", row == null ? "" : StrUtil.nullToEmpty(row.getName()), true));
		rules.add(inputRule(om, "color", "颜色标识", row == null ? "#1890FF" : StrUtil.nullToEmpty(row.getColor()), true));
		rules.add(textareaRule(om, "info", "类型描述", row == null ? "" : StrUtil.nullToEmpty(row.getInfo()), false));
		return rules;
	}

	private ObjectNode inputRule(ObjectMapper om, String field, String title, String value, boolean required) {
		ObjectNode n = om.createObjectNode();
		n.put("type", "input");
		n.put("field", field);
		n.put("title", title);
		n.put("value", value);
		if (required) {
			n.put("$required", true);
		}
		return n;
	}

	private ObjectNode textareaRule(ObjectMapper om, String field, String title, String value, boolean required) {
		ObjectNode n = om.createObjectNode();
		n.put("type", "input");
		n.put("field", field);
		n.put("title", title);
		n.put("value", value);
		ObjectNode p = om.createObjectNode();
		p.put("type", "textarea");
		p.put("rows", 4);
		n.set("props", p);
		if (required) {
			n.put("$required", true);
		}
		return n;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveType(Long userId, ScheduleTypeSaveDTO dto) {
		if (StrUtil.isBlank(dto.getName())) {
			throw new IllegalArgumentException("请填写日程类型名称");
		}
		ScheduleType t = new ScheduleType();
		t.setUserId(userId);
		t.setEntid(1);
		t.setName(dto.getName());
		t.setColor(StrUtil.nullToEmpty(dto.getColor()));
		t.setInfo(StrUtil.nullToEmpty(dto.getInfo()));
		t.setIsPublic(1);
		t.setSort(0);
		t.setCreatedAt(LocalDateTime.now());
		t.setUpdatedAt(LocalDateTime.now());
		baseMapper.insert(t);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateType(Long id, Long userId, ScheduleTypeSaveDTO dto) {
		ScheduleType existing = baseMapper.selectOne(Wrappers.lambdaQuery(ScheduleType.class)
			.eq(ScheduleType::getId, id)
			.eq(ScheduleType::getUserId, userId));
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException("修改的日程类型不存在");
		}
		if (StrUtil.isNotBlank(dto.getColor()) && !StrUtil.equals(dto.getColor(), existing.getColor())) {
			scheduleMapper.update(null,
					Wrappers.lambdaUpdate(Schedule.class)
						.set(Schedule::getColor, dto.getColor())
						.eq(Schedule::getCid, id));
		}
		existing.setName(StrUtil.nullToEmpty(dto.getName()));
		existing.setColor(StrUtil.nullToEmpty(dto.getColor()));
		existing.setInfo(StrUtil.nullToEmpty(dto.getInfo()));
		existing.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteType(Long id, Long userId) {
		long n = baseMapper.selectCount(Wrappers.lambdaQuery(ScheduleType.class)
			.eq(ScheduleType::getId, id)
			.eq(ScheduleType::getUserId, userId));
		if (n == 0) {
			throw new IllegalArgumentException("未找到该日程类型");
		}
		baseMapper.delete(Wrappers.lambdaQuery(ScheduleType.class)
			.eq(ScheduleType::getId, id)
			.eq(ScheduleType::getUserId, userId));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(ScheduleType req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(ScheduleType req) {
		return super.update(req);
	}

}
