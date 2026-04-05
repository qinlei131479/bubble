package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.EnterpriseUserPositionMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.EnterpriseUserPositionBizService;
import com.bubblecloud.biz.oa.util.OaDateParse;
import com.bubblecloud.biz.oa.util.OaFormRuleFactory;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.enterprise.EnterpriseUserPositionSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.EnterpriseUserPosition;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnterpriseUserPositionBizServiceImpl
		extends UpServiceImpl<EnterpriseUserPositionMapper, EnterpriseUserPosition>
		implements EnterpriseUserPositionBizService {

	private final AdminService adminService;

	@Override
	public ListCountVO<EnterpriseUserPosition> list(Long userId) {
		List<EnterpriseUserPosition> list = baseMapper.selectList(Wrappers.lambdaQuery(EnterpriseUserPosition.class)
			.eq(EnterpriseUserPosition::getCardId, userId)
			.orderByAsc(EnterpriseUserPosition::getId));
		return ListCountVO.of(list, list.size());
	}

	@Override
	public OaElFormVO createForm(Long userId, ObjectMapper om) {
		requireAdmin(userId);
		return new OaElFormVO("添加任职经历", "post", "/ent/position",
				OaFormRuleFactory.enterpriseUserPositionRules(om, userId, null));
	}

	@Override
	public OaElFormVO editForm(Long id, ObjectMapper om) {
		EnterpriseUserPosition row = baseMapper.selectById(id);
		if (ObjectUtil.isNull(row)) {
			throw new IllegalArgumentException("修改的信息不存在");
		}
		return new OaElFormVO("修改任职经历", "put", "/ent/position/" + id,
				OaFormRuleFactory.enterpriseUserPositionRules(om, row.getCardId(), row));
	}

	@Override
	public Long save(EnterpriseUserPositionSaveDTO dto) {
		if (ObjectUtil.isNull(dto.getUserId())) {
			throw new IllegalArgumentException("用户ID不能为空");
		}
		requireAdmin(dto.getUserId());
		long cardId = ObjectUtil.isNotNull(dto.getCardId()) ? dto.getCardId() : dto.getUserId();
		var start = OaDateParse.startOfDay(dto.getStartTime(), "开始时间");
		var end = OaDateParse.startOfDay(dto.getEndTime(), "结束时间");
		if (start.isAfter(end)) {
			throw new IllegalArgumentException("开始时间不能大于结束时间");
		}
		if (StrUtil.isBlank(dto.getPosition()) || StrUtil.isBlank(dto.getDepartment())) {
			throw new IllegalArgumentException("请填写职位与部门");
		}
		if (ObjectUtil.isNull(dto.getIsAdmin()) || ObjectUtil.isNull(dto.getStatus())) {
			throw new IllegalArgumentException("请选择身份与任职状态");
		}
		LocalDateTime now = LocalDateTime.now();
		EnterpriseUserPosition e = new EnterpriseUserPosition();
		e.setCardId(cardId);
		e.setStartTime(start);
		e.setEndTime(end);
		e.setPosition(dto.getPosition().trim());
		e.setDepartment(dto.getDepartment().trim());
		e.setIsAdmin(dto.getIsAdmin());
		e.setStatus(dto.getStatus());
		e.setRemark(StrUtil.nullToEmpty(dto.getRemark()));
		e.setCreatedAt(now);
		e.setUpdatedAt(now);
		baseMapper.insert(e);
		return e.getId();
	}

	@Override
	public void update(Long id, EnterpriseUserPositionSaveDTO dto) {
		EnterpriseUserPosition exist = baseMapper.selectById(id);
		if (ObjectUtil.isNull(exist)) {
			throw new IllegalArgumentException("修改的信息不存在");
		}
		var start = OaDateParse.startOfDay(dto.getStartTime(), "开始时间");
		var end = OaDateParse.startOfDay(dto.getEndTime(), "结束时间");
		if (start.isAfter(end)) {
			throw new IllegalArgumentException("开始时间不能大于结束时间");
		}
		if (StrUtil.isBlank(dto.getPosition()) || StrUtil.isBlank(dto.getDepartment())) {
			throw new IllegalArgumentException("请填写职位与部门");
		}
		if (ObjectUtil.isNull(dto.getIsAdmin()) || ObjectUtil.isNull(dto.getStatus())) {
			throw new IllegalArgumentException("请选择身份与任职状态");
		}
		exist.setStartTime(start);
		exist.setEndTime(end);
		exist.setPosition(dto.getPosition().trim());
		exist.setDepartment(dto.getDepartment().trim());
		exist.setIsAdmin(dto.getIsAdmin());
		exist.setStatus(dto.getStatus());
		exist.setRemark(StrUtil.nullToEmpty(dto.getRemark()));
		exist.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(exist);
	}

	@Override
	public void remove(Long id) {
		if (baseMapper.deleteById(id) <= 0) {
			throw new IllegalArgumentException("没有查询到需要删除的数据");
		}
	}

	private void requireAdmin(Long userId) {
		Admin a = adminService.getById(userId);
		if (ObjectUtil.isNull(a)) {
			throw new IllegalArgumentException("企业名片不存在");
		}
	}

}
