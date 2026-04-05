package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.UserResumeMapper;
import com.bubblecloud.biz.oa.mapper.UserWorkHistoryMapper;
import com.bubblecloud.biz.oa.service.UserResumeSupport;
import com.bubblecloud.biz.oa.service.UserWorkHistoryService;
import com.bubblecloud.biz.oa.util.OaDateParse;
import com.bubblecloud.biz.oa.util.OaFormRuleFactory;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.resume.UserWorkHistorySaveDTO;
import com.bubblecloud.oa.api.entity.UserResume;
import com.bubblecloud.oa.api.entity.UserWorkHistory;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserWorkHistoryServiceImpl extends UpServiceImpl<UserWorkHistoryMapper, UserWorkHistory>
		implements UserWorkHistoryService {

	private final UserResumeSupport userResumeSupport;

	private final UserResumeMapper userResumeMapper;

	@Override
	public ListCountVO<UserWorkHistory> list(Long adminId, Long resumeIdFilter) {
		Long resumeId = resumeIdFilter != null ? resumeIdFilter : userResumeSupport.getOrCreateResumeId(adminId);
		List<UserWorkHistory> list = baseMapper.selectList(Wrappers.lambdaQuery(UserWorkHistory.class)
			.eq(UserWorkHistory::getResumeId, resumeId)
			.orderByAsc(UserWorkHistory::getId));
		return ListCountVO.of(list, list.size());
	}

	@Override
	public OaElFormVO createForm(Long adminId, ObjectMapper om) {
		long resumeId = userResumeSupport.getOrCreateResumeId(adminId);
		return new OaElFormVO("添加工作经历", "post", "/ent/user/work",
				OaFormRuleFactory.userWorkHistoryRules(om, resumeId, null));
	}

	@Override
	public OaElFormVO editForm(Long adminId, Long id, ObjectMapper om) {
		UserWorkHistory row = requireOwned(id, adminId);
		return new OaElFormVO("修改工作经历", "put", "/ent/user/work/" + id,
				OaFormRuleFactory.userWorkHistoryRules(om, row.getResumeId(), row));
	}

	@Override
	public Long save(Long adminId, UserWorkHistorySaveDTO dto) {
		String uid = userResumeSupport.requireUid(adminId);
		if (ObjectUtil.isNull(dto.getResumeId())) {
			throw new IllegalArgumentException("简历ID不能为空");
		}
		UserResume resume = userResumeMapper.selectById(dto.getResumeId());
		if (ObjectUtil.isNull(resume) || !uid.equals(resume.getUid())) {
			throw new IllegalArgumentException("个人简历不存在");
		}
		var start = OaDateParse.requireDate(dto.getStartTime(), "开始时间");
		var end = OaDateParse.requireDate(dto.getEndTime(), "结束时间");
		OaDateParse.assertNotAfter(start, end, "开始时间不能大于结束时间");
		if (StrUtil.isBlank(dto.getCompany()) || StrUtil.isBlank(dto.getPosition())
				|| StrUtil.isBlank(dto.getDescribe())) {
			throw new IllegalArgumentException("请填写完整工作经历");
		}
		UserWorkHistory e = new UserWorkHistory();
		e.setUid(uid);
		e.setResumeId(dto.getResumeId());
		e.setStartTime(start);
		e.setEndTime(end);
		e.setCompany(dto.getCompany().trim());
		e.setPosition(dto.getPosition().trim());
		e.setDescribe(StrUtil.nullToEmpty(dto.getDescribe()));
		e.setQuitReason(StrUtil.nullToEmpty(dto.getQuitReason()));
		baseMapper.insert(e);
		return e.getId();
	}

	@Override
	public void update(Long adminId, Long id, UserWorkHistorySaveDTO dto) {
		UserWorkHistory exist = requireOwned(id, adminId);
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
		baseMapper.updateById(exist);
	}

	@Override
	public void remove(Long adminId, Long id) {
		requireOwned(id, adminId);
		baseMapper.deleteById(id);
	}

	private UserWorkHistory requireOwned(Long id, Long adminId) {
		String uid = userResumeSupport.requireUid(adminId);
		UserWorkHistory row = baseMapper.selectById(id);
		if (ObjectUtil.isNull(row) || !uid.equals(row.getUid())) {
			throw new IllegalArgumentException("记录不存在");
		}
		return row;
	}

}
