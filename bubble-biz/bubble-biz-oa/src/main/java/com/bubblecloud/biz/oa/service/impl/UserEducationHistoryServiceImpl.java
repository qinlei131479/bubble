package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.UserEducationHistoryMapper;
import com.bubblecloud.biz.oa.mapper.UserResumeMapper;
import com.bubblecloud.biz.oa.service.UserEducationHistoryService;
import com.bubblecloud.biz.oa.service.UserResumeSupport;
import com.bubblecloud.biz.oa.util.OaDateParse;
import com.bubblecloud.biz.oa.util.OaFormRuleFactory;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.resume.UserEducationHistorySaveDTO;
import com.bubblecloud.oa.api.entity.UserEducationHistory;
import com.bubblecloud.oa.api.entity.UserResume;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEducationHistoryServiceImpl extends UpServiceImpl<UserEducationHistoryMapper, UserEducationHistory>
		implements UserEducationHistoryService {

	private final UserResumeSupport userResumeSupport;

	private final UserResumeMapper userResumeMapper;

	@Override
	public ListCountVO<UserEducationHistory> list(Long adminId, Long resumeIdFilter) {
		Long resumeId = resumeIdFilter != null ? resumeIdFilter : userResumeSupport.getOrCreateResumeId(adminId);
		List<UserEducationHistory> list = baseMapper.selectList(Wrappers.lambdaQuery(UserEducationHistory.class)
			.eq(UserEducationHistory::getResumeId, resumeId)
			.orderByAsc(UserEducationHistory::getId));
		return ListCountVO.of(list, list.size());
	}

	@Override
	public OaElFormVO createForm(Long adminId, ObjectMapper om) {
		long resumeId = userResumeSupport.getOrCreateResumeId(adminId);
		return new OaElFormVO("添加教育经历", "post", "/ent/user/education",
				OaFormRuleFactory.userEducationHistoryRules(om, resumeId, null));
	}

	@Override
	public OaElFormVO editForm(Long adminId, Long id, ObjectMapper om) {
		UserEducationHistory row = requireOwned(id, adminId);
		return new OaElFormVO("修改教育经历", "put", "/ent/user/education/" + id,
				OaFormRuleFactory.userEducationHistoryRules(om, row.getResumeId(), row));
	}

	@Override
	public Long save(Long adminId, UserEducationHistorySaveDTO dto) {
		String uid = userResumeSupport.requireUid(adminId);
		if (ObjectUtil.isNull(dto.getResumeId())) {
			throw new IllegalArgumentException("简历ID不能为空");
		}
		UserResume resume = userResumeMapper.selectById(dto.getResumeId());
		if (ObjectUtil.isNull(resume) || !uid.equals(resume.getUid())) {
			throw new IllegalArgumentException("个人简历不存在");
		}
		var start = OaDateParse.requireDate(dto.getStartTime(), "入学时间");
		var end = OaDateParse.requireDate(dto.getEndTime(), "毕业时间");
		OaDateParse.assertNotAfter(start, end, "入学时间不能大于毕业时间");
		if (StrUtil.isBlank(dto.getSchoolName()) || StrUtil.isBlank(dto.getMajor())
				|| StrUtil.isBlank(dto.getEducation())) {
			throw new IllegalArgumentException("请填写完整教育经历");
		}
		UserEducationHistory e = new UserEducationHistory();
		e.setUid(uid);
		e.setResumeId(dto.getResumeId());
		e.setStartTime(start);
		e.setEndTime(end);
		e.setSchoolName(dto.getSchoolName().trim());
		e.setMajor(dto.getMajor().trim());
		e.setEducation(dto.getEducation().trim());
		e.setAcademic(StrUtil.nullToEmpty(dto.getAcademic()));
		e.setRemark(StrUtil.nullToEmpty(dto.getRemark()));
		baseMapper.insert(e);
		return e.getId();
	}

	@Override
	public void update(Long adminId, Long id, UserEducationHistorySaveDTO dto) {
		UserEducationHistory exist = requireOwned(id, adminId);
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
		baseMapper.updateById(exist);
	}

	@Override
	public void remove(Long adminId, Long id) {
		requireOwned(id, adminId);
		baseMapper.deleteById(id);
	}

	private UserEducationHistory requireOwned(Long id, Long adminId) {
		String uid = userResumeSupport.requireUid(adminId);
		UserEducationHistory row = baseMapper.selectById(id);
		if (ObjectUtil.isNull(row) || !uid.equals(row.getUid())) {
			throw new IllegalArgumentException("记录不存在");
		}
		return row;
	}

}
