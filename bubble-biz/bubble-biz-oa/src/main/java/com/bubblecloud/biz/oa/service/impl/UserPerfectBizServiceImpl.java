package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AdminInfoMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseMapper;
import com.bubblecloud.biz.oa.mapper.UserCardPerfectMapper;
import com.bubblecloud.biz.oa.mapper.UserResumeMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.UserPerfectBizService;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.AdminInfo;
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.entity.UserCardPerfect;
import com.bubblecloud.oa.api.entity.UserResume;
import com.bubblecloud.oa.api.vo.perfect.UserPerfectEnterpriseVO;
import com.bubblecloud.oa.api.vo.perfect.UserPerfectIndexVO;
import com.bubblecloud.oa.api.vo.perfect.UserPerfectListRowVO;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPerfectBizServiceImpl implements UserPerfectBizService {

	private final UserCardPerfectMapper userCardPerfectMapper;

	private final AdminService adminService;

	private final UserResumeMapper userResumeMapper;

	private final AdminInfoMapper adminInfoMapper;

	private final EnterpriseMapper enterpriseMapper;

	@Override
	public UserPerfectIndexVO listForCurrentUser(Long adminId, Integer status, Integer page, Integer limit) {
		if (ObjectUtil.isNull(adminId)) {
			throw new IllegalArgumentException("用户未登录");
		}
		int uid = adminId.intValue();
		var w = Wrappers.lambdaQuery(UserCardPerfect.class).eq(UserCardPerfect::getUserId, uid);
		if (status != null) {
			w.eq(UserCardPerfect::getStatus, status);
		}
		else {
			w.in(UserCardPerfect::getStatus, 1, 2).in(UserCardPerfect::getTotal, -1, 0, 1);
		}
		w.orderByDesc(UserCardPerfect::getId);
		List<UserCardPerfect> rows;
		if (page != null && limit != null && page > 0 && limit > 0) {
			Page<UserCardPerfect> pg = new Page<>(page, limit);
			rows = userCardPerfectMapper.selectPage(pg, w).getRecords();
		}
		else {
			rows = userCardPerfectMapper.selectList(w);
		}
		if (rows.isEmpty()) {
			return UserPerfectIndexVO.of(List.of());
		}
		Set<Long> entIds = rows.stream()
			.map(UserCardPerfect::getEntid)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
		Map<Long, Enterprise> entMap = entIds.isEmpty() ? Map.of()
				: enterpriseMapper.selectBatchIds(entIds).stream().collect(Collectors.toMap(Enterprise::getId, e -> e));
		List<UserPerfectListRowVO> out = new ArrayList<>();
		for (UserCardPerfect p : rows) {
			UserPerfectListRowVO vo = new UserPerfectListRowVO();
			BeanUtils.copyProperties(p, vo);
			if (ObjectUtil.isNotNull(p.getEntid())) {
				Enterprise e = entMap.get(p.getEntid());
				if (ObjectUtil.isNotNull(e)) {
					UserPerfectEnterpriseVO ev = new UserPerfectEnterpriseVO();
					ev.setId(e.getId());
					ev.setEnterpriseName(e.getName());
					ev.setLogo(e.getLogo());
					vo.setEnterprise(ev);
				}
			}
			out.add(vo);
		}
		return UserPerfectIndexVO.of(out);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void agree(Long adminId, Long id) {
		if (ObjectUtil.isNull(adminId)) {
			throw new IllegalArgumentException("用户未登录");
		}
		UserCardPerfect row = userCardPerfectMapper.selectById(id);
		if (ObjectUtil.isNull(row)) {
			throw new IllegalArgumentException("未找到相关邀请记录");
		}
		if (!Objects.equals(row.getUserId(), adminId.intValue())) {
			throw new IllegalArgumentException("人员信息不符，禁止操作");
		}
		Admin admin = adminService.getById(adminId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("用户不存在");
		}
		UserResume resume = userResumeMapper
			.selectOne(Wrappers.lambdaQuery(UserResume.class).eq(UserResume::getUid, admin.getUid()).last("LIMIT 1"));
		if (ObjectUtil.isNull(resume)) {
			throw new IllegalArgumentException("简历完善度过低，请先完善个人简历");
		}
		if (countEmptyResumeFields(resume) > 5) {
			throw new IllegalArgumentException("简历完善度过低，请先完善个人简历");
		}
		row.setStatus(1);
		userCardPerfectMapper.updateById(row);
		AdminInfo info = adminInfoMapper.selectById(adminId);
		if (ObjectUtil.isNotNull(info)) {
			applyResumeToAdminInfo(info, resume);
			adminInfoMapper.updateById(info);
		}
	}

	@Override
	public void refuse(Long id) {
		UserCardPerfect row = userCardPerfectMapper.selectById(id);
		if (ObjectUtil.isNull(row)) {
			throw new IllegalArgumentException("未找到相关邀请记录");
		}
		row.setStatus(2);
		userCardPerfectMapper.updateById(row);
	}

	private int countEmptyResumeFields(UserResume r) {
		int c = 0;
		if (isEmpty(r.getPhoto())) {
			c++;
		}
		if (isEmpty(r.getName())) {
			c++;
		}
		if (isEmpty(r.getBirthday())) {
			c++;
		}
		if (isEmpty(r.getNation())) {
			c++;
		}
		if (isEmpty(r.getPolitic())) {
			c++;
		}
		if (isEmpty(r.getNativePlace())) {
			c++;
		}
		if (isEmpty(r.getAddress())) {
			c++;
		}
		if (isEmpty(r.getEducation())) {
			c++;
		}
		if (isEmpty(r.getAcad())) {
			c++;
		}
		if (isEmpty(r.getGraduateName())) {
			c++;
		}
		if (isEmpty(r.getGraduateDate())) {
			c++;
		}
		if (isEmpty(r.getEmail())) {
			c++;
		}
		if (isEmpty(r.getCardId())) {
			c++;
		}
		if (isEmpty(r.getSpareName())) {
			c++;
		}
		if (isEmpty(r.getSpareTel())) {
			c++;
		}
		if (ObjectUtil.isNull(r.getSex())) {
			c++;
		}
		if (ObjectUtil.isNull(r.getAge())) {
			c++;
		}
		if (ObjectUtil.isNull(r.getMarriage())) {
			c++;
		}
		if (ObjectUtil.isNull(r.getWorkYears())) {
			c++;
		}
		if (isEmpty(r.getBankNum())) {
			c++;
		}
		if (isEmpty(r.getBankName())) {
			c++;
		}
		if (isEmpty(r.getSocialNum())) {
			c++;
		}
		if (isEmpty(r.getFundNum())) {
			c++;
		}
		if (isEmpty(r.getCardFront())) {
			c++;
		}
		if (isEmpty(r.getCardBoth())) {
			c++;
		}
		if (isEmpty(r.getEducationImage())) {
			c++;
		}
		if (isEmpty(r.getAcadImage())) {
			c++;
		}
		return c;
	}

	private boolean isEmpty(String s) {
		return StrUtil.isBlank(s);
	}

	private void applyResumeToAdminInfo(AdminInfo info, UserResume r) {
		String name = StrUtil.nullToEmpty(r.getName());
		if (StrUtil.isNotBlank(name)) {
			String letter = name.trim().substring(0, 1).toUpperCase(Locale.ROOT);
			info.setLetter(letter);
		}
		info.setInterviewPosition(StrUtil.nullToEmpty(r.getPosition()));
		info.setCardId(StrUtil.nullToEmpty(r.getCardId()));
		info.setSex(r.getSex());
		info.setBirthday(StrUtil.nullToEmpty(r.getBirthday()));
		info.setAge(r.getAge());
		info.setNation(StrUtil.nullToEmpty(r.getNation()));
		info.setPolitic(StrUtil.nullToEmpty(r.getPolitic()));
		info.setWorkYears(r.getWorkYears());
		info.setNativePlace(StrUtil.nullToEmpty(r.getNativePlace()));
		info.setAddress(StrUtil.nullToEmpty(r.getAddress()));
		info.setMarriage(r.getMarriage());
		info.setEmail(StrUtil.nullToEmpty(r.getEmail()));
		info.setEducation(StrUtil.nullToEmpty(r.getEducation()));
		info.setAcad(StrUtil.nullToEmpty(r.getAcad()));
		info.setGraduateDate(StrUtil.nullToEmpty(r.getGraduateDate()));
		info.setGraduateName(StrUtil.nullToEmpty(r.getGraduateName()));
		info.setBankNum(StrUtil.nullToEmpty(r.getBankNum()));
		info.setBankName(StrUtil.nullToEmpty(r.getBankName()));
		info.setSocialNum(StrUtil.nullToEmpty(r.getSocialNum()));
		info.setFundNum(StrUtil.nullToEmpty(r.getFundNum()));
		info.setSpareName(StrUtil.nullToEmpty(r.getSpareName()));
		info.setSpareTel(StrUtil.nullToEmpty(r.getSpareTel()));
		info.setCardFront(StrUtil.nullToEmpty(r.getCardFront()));
		info.setCardBoth(StrUtil.nullToEmpty(r.getCardBoth()));
		info.setEducationImage(StrUtil.nullToEmpty(r.getEducationImage()));
		info.setAcadImage(StrUtil.nullToEmpty(r.getAcadImage()));
		info.setPhoto(StrUtil.nullToEmpty(r.getPhoto()));
	}

}
