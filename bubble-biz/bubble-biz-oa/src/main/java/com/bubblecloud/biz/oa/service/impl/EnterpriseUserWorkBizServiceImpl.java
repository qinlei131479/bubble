package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.EnterpriseUserWorkMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.EnterpriseUserWorkBizService;
import com.bubblecloud.biz.oa.util.OaDateParse;
import com.bubblecloud.biz.oa.util.OaFormRuleFactory;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.enterprise.EnterpriseUserWorkSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.EnterpriseUserWork;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnterpriseUserWorkBizServiceImpl extends UpServiceImpl<EnterpriseUserWorkMapper, EnterpriseUserWork>
		implements EnterpriseUserWorkBizService {

	private final AdminService adminService;

	@Override
	public ListCountVO<EnterpriseUserWork> list(Long userId) {
		List<EnterpriseUserWork> list = baseMapper.selectList(Wrappers.lambdaQuery(EnterpriseUserWork.class)
			.eq(EnterpriseUserWork::getUserId, userId)
			.orderByAsc(EnterpriseUserWork::getId));
		return ListCountVO.of(list, list.size());
	}

	@Override
	public OaElFormVO createForm(Long userId, ObjectMapper om) {
		requireAdmin(userId);
		return new OaElFormVO("添加工作经历", "post", "/ent/work",
				OaFormRuleFactory.enterpriseUserWorkRules(om, userId, null));
	}

	@Override
	public OaElFormVO editForm(Long id, ObjectMapper om) {
		EnterpriseUserWork row = baseMapper.selectById(id);
		if (ObjectUtil.isNull(row)) {
			throw new IllegalArgumentException("修改的工作经历不存在");
		}
		return new OaElFormVO("修改工作经历", "put", "/ent/work/" + id,
				OaFormRuleFactory.enterpriseUserWorkRules(om, row.getUserId(), row));
	}

	@Override
	public Long save(EnterpriseUserWorkSaveDTO dto) {
		if (ObjectUtil.isNull(dto.getUserId())) {
			throw new IllegalArgumentException("用户ID不能为空");
		}
		requireAdmin(dto.getUserId());
		long cardId = ObjectUtil.isNotNull(dto.getCardId()) ? dto.getCardId() : dto.getUserId();
		var start = OaDateParse.requireDate(dto.getStartTime(), "开始时间");
		var end = OaDateParse.requireDate(dto.getEndTime(), "结束时间");
		OaDateParse.assertNotAfter(start, end, "开始时间不能大于结束时间");
		if (StrUtil.isBlank(dto.getCompany()) || StrUtil.isBlank(dto.getPosition())
				|| StrUtil.isBlank(dto.getDescribe())) {
			throw new IllegalArgumentException("请填写完整工作经历");
		}
		LocalDateTime now = LocalDateTime.now();
		EnterpriseUserWork e = new EnterpriseUserWork();
		e.setUserId(dto.getUserId());
		e.setCardId(cardId);
		e.setStartTime(start);
		e.setEndTime(end);
		e.setCompany(dto.getCompany().trim());
		e.setPosition(dto.getPosition().trim());
		e.setDescribe(StrUtil.nullToEmpty(dto.getDescribe()));
		e.setQuitReason(StrUtil.nullToEmpty(dto.getQuitReason()));
		e.setCreatedAt(now);
		e.setUpdatedAt(now);
		baseMapper.insert(e);
		return e.getId();
	}

	@Override
	public void update(Long id, EnterpriseUserWorkSaveDTO dto) {
		EnterpriseUserWork exist = baseMapper.selectById(id);
		if (ObjectUtil.isNull(exist)) {
			throw new IllegalArgumentException("修改的信息不存在");
		}
		var start = OaDateParse.requireDate(dto.getStartTime(), "开始时间");
		var end = OaDateParse.requireDate(dto.getEndTime(), "结束时间");
		OaDateParse.assertNotAfter(start, end, "开始时间不能大于结束时间");
		if (StrUtil.isBlank(dto.getCompany()) || StrUtil.isBlank(dto.getPosition())
				|| StrUtil.isBlank(dto.getDescribe())) {
			throw new IllegalArgumentException("请填写完整工作经历");
		}
		exist.setStartTime(start);
		exist.setEndTime(end);
		exist.setCompany(dto.getCompany().trim());
		exist.setPosition(dto.getPosition().trim());
		exist.setDescribe(StrUtil.nullToEmpty(dto.getDescribe()));
		exist.setQuitReason(StrUtil.nullToEmpty(dto.getQuitReason()));
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
