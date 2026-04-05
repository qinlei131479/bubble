package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.EnterpriseUserEducationMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.EnterpriseUserEducationBizService;
import com.bubblecloud.biz.oa.util.OaDateParse;
import com.bubblecloud.biz.oa.util.OaFormRuleFactory;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.enterprise.EnterpriseUserEducationSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.EnterpriseUserEducation;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * EnterpriseUserEducationBizServiceImpl。
 *
 * @author qinlei
 * @date 2026/4/5
 */

@Service
@RequiredArgsConstructor
public class EnterpriseUserEducationBizServiceImpl
		extends UpServiceImpl<EnterpriseUserEducationMapper, EnterpriseUserEducation>
		implements EnterpriseUserEducationBizService {

	private final AdminService adminService;

	@Override
	public ListCountVO<EnterpriseUserEducation> list(Long userId) {
		List<EnterpriseUserEducation> list = baseMapper.selectList(Wrappers.lambdaQuery(EnterpriseUserEducation.class)
			.eq(EnterpriseUserEducation::getUserId, userId)
			.orderByAsc(EnterpriseUserEducation::getId));
		return ListCountVO.of(list, list.size());
	}

	@Override
	public OaElFormVO createForm(Long userId, ObjectMapper om) {
		requireAdmin(userId);
		return new OaElFormVO("添加教育经历", "post", "/ent/education",
				OaFormRuleFactory.enterpriseUserEducationRules(om, userId, null));
	}

	@Override
	public OaElFormVO editForm(Long id, ObjectMapper om) {
		EnterpriseUserEducation row = baseMapper.selectById(id);
		if (ObjectUtil.isNull(row)) {
			throw new IllegalArgumentException("修改的信息不存在");
		}
		return new OaElFormVO("修改教育经历", "put", "/ent/education/" + id,
				OaFormRuleFactory.enterpriseUserEducationRules(om, row.getUserId(), row));
	}

	@Override
	public Long save(EnterpriseUserEducationSaveDTO dto) {
		if (ObjectUtil.isNull(dto.getUserId())) {
			throw new IllegalArgumentException("用户ID不能为空");
		}
		requireAdmin(dto.getUserId());
		long cardId = ObjectUtil.isNotNull(dto.getCardId()) ? dto.getCardId() : dto.getUserId();
		var start = OaDateParse.requireDate(dto.getStartTime(), "入学时间");
		var end = OaDateParse.requireDate(dto.getEndTime(), "毕业时间");
		OaDateParse.assertNotAfter(start, end, "入学时间不能大于毕业时间");
		if (StrUtil.isBlank(dto.getSchoolName()) || StrUtil.isBlank(dto.getMajor())
				|| StrUtil.isBlank(dto.getEducation())) {
			throw new IllegalArgumentException("请填写完整教育经历");
		}
		LocalDateTime now = LocalDateTime.now();
		EnterpriseUserEducation e = new EnterpriseUserEducation();
		e.setUserId(dto.getUserId());
		e.setCardId(cardId);
		e.setStartTime(start);
		e.setEndTime(end);
		e.setSchoolName(dto.getSchoolName().trim());
		e.setMajor(dto.getMajor().trim());
		e.setEducation(dto.getEducation().trim());
		e.setAcademic(StrUtil.nullToEmpty(dto.getAcademic()));
		e.setRemark(StrUtil.nullToEmpty(dto.getRemark()));
		e.setCreatedAt(now);
		e.setUpdatedAt(now);
		baseMapper.insert(e);
		return e.getId();
	}

	@Override
	public void update(Long id, EnterpriseUserEducationSaveDTO dto) {
		EnterpriseUserEducation exist = baseMapper.selectById(id);
		if (ObjectUtil.isNull(exist)) {
			throw new IllegalArgumentException("修改的信息不存在");
		}
		var start = OaDateParse.requireDate(dto.getStartTime(), "入学时间");
		var end = OaDateParse.requireDate(dto.getEndTime(), "毕业时间");
		OaDateParse.assertNotAfter(start, end, "入学时间不能大于毕业时间");
		if (StrUtil.isBlank(dto.getSchoolName()) || StrUtil.isBlank(dto.getMajor())
				|| StrUtil.isBlank(dto.getEducation())) {
			throw new IllegalArgumentException("请填写完整教育经历");
		}
		exist.setStartTime(start);
		exist.setEndTime(end);
		exist.setSchoolName(dto.getSchoolName().trim());
		exist.setMajor(dto.getMajor().trim());
		exist.setEducation(dto.getEducation().trim());
		exist.setAcademic(StrUtil.nullToEmpty(dto.getAcademic()));
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
