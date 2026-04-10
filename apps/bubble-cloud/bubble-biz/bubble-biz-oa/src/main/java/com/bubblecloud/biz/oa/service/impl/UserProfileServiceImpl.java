package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminInfoMapper;
import com.bubblecloud.biz.oa.mapper.UserResumeMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.SmsVerifyService;
import com.bubblecloud.biz.oa.service.UserProfileService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.CheckPwdDTO;
import com.bubblecloud.oa.api.dto.UserResumeSaveDTO;
import com.bubblecloud.oa.api.dto.UserSelfUpdateDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.AdminInfo;
import com.bubblecloud.oa.api.entity.UserResume;
import com.bubblecloud.oa.api.vo.user.UserResumeDetailVO;
import com.bubblecloud.oa.api.vo.user.UserSelfInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.common.core.util.R;

/**
 * 当前用户资料与简历。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl extends UpServiceImpl<AdminInfoMapper, AdminInfo> implements UserProfileService {

	private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

	private final AdminService adminService;

	private final UserResumeMapper userResumeMapper;

	private final SmsVerifyService smsVerifyService;

	@Override
	public UserSelfInfoVO getSelfInfo(Long adminId) {
		Admin admin = adminService.getById(adminId);
		if (ObjectUtil.isNull(admin)) {
			return null;
		}
		UserSelfInfoVO vo = new UserSelfInfoVO();
		vo.setId(admin.getId());
		vo.setUid(admin.getUid());
		vo.setPassword(admin.getPassword());
		vo.setPhone(admin.getPhone());
		vo.setName(admin.getName());
		vo.setAvatar(admin.getAvatar());
		AdminInfo info = baseMapper.selectById(adminId);
		if (ObjectUtil.isNotNull(info) && StrUtil.isNotBlank(info.getEmail())) {
			vo.setEmail(info.getEmail());
		}
		else {
			vo.setEmail("");
		}
		return vo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateSelf(Long adminId, UserSelfUpdateDTO dto) {
		Admin admin = adminService.getById(adminId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("用户不存在");
		}
		if (StrUtil.isNotBlank(dto.getEmail())) {
			AdminInfo info = baseMapper.selectById(adminId);
			if (ObjectUtil.isNull(info)) {
				info = new AdminInfo();
				info.setId(adminId);
				info.setUid(admin.getUid());
				info.setEmail(dto.getEmail());
				baseMapper.insert(info);
			}
			else {
				info.setEmail(dto.getEmail());
				baseMapper.updateById(info);
			}
		}
		if (StrUtil.isNotBlank(dto.getPhone()) && StrUtil.isNotBlank(dto.getVerificationCode())) {
			if (!smsVerifyService.verifyCode(dto.getPhone(), dto.getVerificationCode())) {
				throw new IllegalArgumentException("验证码错误或已过期");
			}
			if (adminService.countByPhone(dto.getPhone()) > 0 && !dto.getPhone().equals(admin.getPhone())) {
				throw new IllegalArgumentException("手机号已注册,请更换没有注册的手机号");
			}
			admin.setPhone(dto.getPhone());
		}
		if (StrUtil.isNotBlank(dto.getPassword()) && StrUtil.isNotBlank(dto.getPasswordConfirm())) {
			if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
				throw new IllegalArgumentException("两次输入的密码不正确");
			}
			if (dto.getPassword().length() < 5) {
				throw new IllegalArgumentException("密码长度不正确");
			}
			admin.setPassword(ENCODER.encode(dto.getPassword()));
			admin.setIsInit(0);
		}
		if (StrUtil.isNotBlank(dto.getName())) {
			admin.setName(dto.getName());
		}
		if (StrUtil.isNotBlank(dto.getAvatar())) {
			admin.setAvatar(dto.getAvatar());
		}
		adminService.updateById(admin);
	}

	@Override
	public void checkPwd(CheckPwdDTO dto) {
		if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
			throw new IllegalArgumentException("两次输入的密码不正确");
		}
	}

	@Override
	public UserResumeDetailVO getResume(Long adminId) {
		Admin admin = adminService.getById(adminId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("用户不存在");
		}
		String uid = admin.getUid();
		UserResume row = userResumeMapper
			.selectOne(Wrappers.lambdaQuery(UserResume.class).eq(UserResume::getUid, uid).last("LIMIT 1"));
		if (ObjectUtil.isNull(row)) {
			row = new UserResume();
			row.setUid(uid);
			userResumeMapper.insert(row);
			row = userResumeMapper
				.selectOne(Wrappers.lambdaQuery(UserResume.class).eq(UserResume::getUid, uid).last("LIMIT 1"));
		}
		UserResumeDetailVO vo = new UserResumeDetailVO();
		vo.setResume(row);
		return vo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveResume(Long adminId, UserResumeSaveDTO dto) {
		Admin admin = adminService.getById(adminId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("用户不存在");
		}
		String uid = admin.getUid();
		UserResume row = userResumeMapper
			.selectOne(Wrappers.lambdaQuery(UserResume.class).eq(UserResume::getUid, uid).last("LIMIT 1"));
		if (ObjectUtil.isNull(row)) {
			throw new IllegalArgumentException("保存失败,未找到简历信息");
		}
		applyResume(row, dto);
		userResumeMapper.updateById(row);
	}

	private static void applyResume(UserResume row, UserResumeSaveDTO dto) {
		if (ObjectUtil.isNotNull(dto.getName())) {
			row.setName(dto.getName());
		}
		if (ObjectUtil.isNotNull(dto.getPhoto())) {
			row.setPhoto(dto.getPhoto());
		}
		if (ObjectUtil.isNotNull(dto.getPosition())) {
			row.setPosition(dto.getPosition());
		}
		if (ObjectUtil.isNotNull(dto.getCardId())) {
			row.setCardId(dto.getCardId());
		}
		if (ObjectUtil.isNotNull(dto.getBirthday())) {
			row.setBirthday(dto.getBirthday());
		}
		if (ObjectUtil.isNotNull(dto.getAge())) {
			row.setAge(parseIntOrNull(dto.getAge()));
		}
		if (ObjectUtil.isNotNull(dto.getEducation())) {
			row.setEducation(dto.getEducation());
		}
		if (ObjectUtil.isNotNull(dto.getEducationImage())) {
			row.setEducationImage(dto.getEducationImage());
		}
		if (ObjectUtil.isNotNull(dto.getPhone())) {
			row.setPhone(dto.getPhone());
		}
		if (ObjectUtil.isNotNull(dto.getSex())) {
			row.setSex(dto.getSex());
		}
		if (ObjectUtil.isNotNull(dto.getNation())) {
			row.setNation(dto.getNation());
		}
		if (ObjectUtil.isNotNull(dto.getAcad())) {
			row.setAcad(dto.getAcad());
		}
		if (ObjectUtil.isNotNull(dto.getAcadImage())) {
			row.setAcadImage(dto.getAcadImage());
		}
		if (ObjectUtil.isNotNull(dto.getPolitic())) {
			row.setPolitic(dto.getPolitic());
		}
		if (ObjectUtil.isNotNull(dto.getNativePlace())) {
			row.setNativePlace(dto.getNativePlace());
		}
		if (ObjectUtil.isNotNull(dto.getAddress())) {
			row.setAddress(dto.getAddress());
		}
		if (ObjectUtil.isNotNull(dto.getMarriage())) {
			row.setMarriage(dto.getMarriage());
		}
		if (ObjectUtil.isNotNull(dto.getWorkYears())) {
			row.setWorkYears(parseIntOrNull(dto.getWorkYears()));
		}
		if (ObjectUtil.isNotNull(dto.getSpareName())) {
			row.setSpareName(dto.getSpareName());
		}
		if (ObjectUtil.isNotNull(dto.getSpareTel())) {
			row.setSpareTel(dto.getSpareTel());
		}
		if (ObjectUtil.isNotNull(dto.getEmail())) {
			row.setEmail(dto.getEmail());
		}
		if (ObjectUtil.isNotNull(dto.getWorkTime())) {
			row.setWorkTime(dto.getWorkTime());
		}
		if (ObjectUtil.isNotNull(dto.getTrialTime())) {
			row.setTrialTime(dto.getTrialTime());
		}
		if (ObjectUtil.isNotNull(dto.getFormalTime())) {
			row.setFormalTime(dto.getFormalTime());
		}
		if (ObjectUtil.isNotNull(dto.getTreatyTime())) {
			row.setTreatyTime(dto.getTreatyTime());
		}
		if (ObjectUtil.isNotNull(dto.getIsPart())) {
			row.setIsPart(dto.getIsPart());
		}
		if (ObjectUtil.isNotNull(dto.getSocialNum())) {
			row.setSocialNum(dto.getSocialNum());
		}
		if (ObjectUtil.isNotNull(dto.getFundNum())) {
			row.setFundNum(dto.getFundNum());
		}
		if (ObjectUtil.isNotNull(dto.getBankNum())) {
			row.setBankNum(dto.getBankNum());
		}
		if (ObjectUtil.isNotNull(dto.getBankName())) {
			row.setBankName(dto.getBankName());
		}
		if (ObjectUtil.isNotNull(dto.getGraduateName())) {
			row.setGraduateName(dto.getGraduateName());
		}
		if (ObjectUtil.isNotNull(dto.getGraduateDate())) {
			row.setGraduateDate(dto.getGraduateDate());
		}
		if (ObjectUtil.isNotNull(dto.getCardFront())) {
			row.setCardFront(dto.getCardFront());
		}
		if (ObjectUtil.isNotNull(dto.getCardBoth())) {
			row.setCardBoth(dto.getCardBoth());
		}
	}

	private static Integer parseIntOrNull(String s) {
		if (StrUtil.isBlank(s)) {
			return null;
		}
		try {
			return Integer.parseInt(s.trim());
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(AdminInfo req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(AdminInfo req) {
		return super.update(req);
	}

}
