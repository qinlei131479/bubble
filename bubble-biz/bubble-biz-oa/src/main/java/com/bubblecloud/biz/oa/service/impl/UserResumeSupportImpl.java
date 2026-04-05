package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.UserResumeMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.UserResumeSupport;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.UserResume;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserResumeSupportImpl implements UserResumeSupport {

	private final UserResumeMapper userResumeMapper;

	private final AdminService adminService;

	@Override
	public String requireUid(Long adminId) {
		if (ObjectUtil.isNull(adminId)) {
			throw new IllegalArgumentException("用户未登录");
		}
		Admin admin = adminService.getById(adminId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		return admin.getUid();
	}

	@Override
	public Long getOrCreateResumeId(Long adminId) {
		String uid = requireUid(adminId);
		UserResume exist = userResumeMapper
			.selectOne(Wrappers.lambdaQuery(UserResume.class).eq(UserResume::getUid, uid).last("LIMIT 1"));
		if (ObjectUtil.isNotNull(exist)) {
			return exist.getId();
		}
		UserResume r = new UserResume();
		r.setUid(uid);
		r.setPhoto("");
		r.setName("");
		r.setPhone("");
		r.setPosition("");
		r.setBirthday("");
		r.setNation("");
		r.setPolitic("");
		r.setNativePlace("");
		r.setAddress("");
		r.setSex(0);
		r.setAge(18);
		r.setMarriage(0);
		r.setIsPart(0);
		r.setWorkYears(0);
		r.setSpareName("");
		r.setSpareTel("");
		r.setEmail("");
		r.setWorkTime("");
		r.setTrialTime("");
		r.setFormalTime("");
		r.setTreatyTime("");
		r.setSocialNum("");
		r.setFundNum("");
		r.setBankNum("");
		r.setBankName("");
		r.setGraduateName("");
		r.setGraduateDate("");
		r.setCardId("");
		r.setCardFront("");
		r.setCardBoth("");
		r.setEducation("");
		r.setEducationImage("");
		r.setAcad("");
		r.setAcadImage("");
		LocalDateTime now = LocalDateTime.now();
		r.setCreatedAt(now);
		r.setUpdatedAt(now);
		userResumeMapper.insert(r);
		return r.getId();
	}

}
