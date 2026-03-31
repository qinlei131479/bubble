package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminInfoMapper;
import com.bubblecloud.biz.oa.mapper.UserResumeMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.SmsVerifyService;
import com.bubblecloud.biz.oa.service.UserProfileService;
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
import org.springframework.util.StringUtils;

/**
 * 当前用户资料与简历。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

	private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

	private final AdminService adminService;

	private final AdminInfoMapper adminInfoMapper;

	private final UserResumeMapper userResumeMapper;

	private final SmsVerifyService smsVerifyService;

	@Override
	public UserSelfInfoVO getSelfInfo(long adminId) {
		Admin admin = adminService.getById(adminId);
		if (admin == null) {
			return null;
		}
		UserSelfInfoVO vo = new UserSelfInfoVO();
		vo.setId(admin.getId());
		vo.setUid(admin.getUid());
		vo.setPassword(admin.getPassword());
		vo.setPhone(admin.getPhone());
		vo.setName(admin.getName());
		vo.setAvatar(admin.getAvatar());
		AdminInfo info = adminInfoMapper.selectById(adminId);
		if (info != null && StringUtils.hasText(info.getEmail())) {
			vo.setEmail(info.getEmail());
		}
		else {
			vo.setEmail("");
		}
		return vo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateSelf(long adminId, UserSelfUpdateDTO dto) {
		Admin admin = adminService.getById(adminId);
		if (admin == null) {
			throw new IllegalArgumentException("用户不存在");
		}
		if (StringUtils.hasText(dto.getEmail())) {
			AdminInfo info = adminInfoMapper.selectById(adminId);
			if (info == null) {
				info = new AdminInfo();
				info.setId(adminId);
				info.setUid(admin.getUid());
				info.setEmail(dto.getEmail());
				adminInfoMapper.insert(info);
			}
			else {
				info.setEmail(dto.getEmail());
				adminInfoMapper.updateById(info);
			}
		}
		if (StringUtils.hasText(dto.getPhone()) && StringUtils.hasText(dto.getVerificationCode())) {
			if (!smsVerifyService.verifyCode(dto.getPhone(), dto.getVerificationCode())) {
				throw new IllegalArgumentException("验证码错误或已过期");
			}
			if (adminService.countByPhone(dto.getPhone()) > 0 && !dto.getPhone().equals(admin.getPhone())) {
				throw new IllegalArgumentException("手机号已注册,请更换没有注册的手机号");
			}
			admin.setPhone(dto.getPhone());
		}
		if (StringUtils.hasText(dto.getPassword()) && StringUtils.hasText(dto.getPasswordConfirm())) {
			if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
				throw new IllegalArgumentException("两次输入的密码不正确");
			}
			if (dto.getPassword().length() < 5) {
				throw new IllegalArgumentException("密码长度不正确");
			}
			admin.setPassword(ENCODER.encode(dto.getPassword()));
			admin.setIsInit(0);
		}
		if (StringUtils.hasText(dto.getName())) {
			admin.setName(dto.getName());
		}
		if (StringUtils.hasText(dto.getAvatar())) {
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
	public UserResumeDetailVO getResume(long adminId) {
		Admin admin = adminService.getById(adminId);
		if (admin == null) {
			throw new IllegalArgumentException("用户不存在");
		}
		String uid = admin.getUid();
		UserResume row = userResumeMapper
			.selectOne(Wrappers.lambdaQuery(UserResume.class).eq(UserResume::getUid, uid).last("LIMIT 1"));
		if (row == null) {
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
	public void saveResume(long adminId, UserResumeSaveDTO dto) {
		Admin admin = adminService.getById(adminId);
		if (admin == null) {
			throw new IllegalArgumentException("用户不存在");
		}
		String uid = admin.getUid();
		UserResume row = userResumeMapper
			.selectOne(Wrappers.lambdaQuery(UserResume.class).eq(UserResume::getUid, uid).last("LIMIT 1"));
		if (row == null) {
			throw new IllegalArgumentException("保存失败,未找到简历信息");
		}
		applyResume(row, dto);
		userResumeMapper.updateById(row);
	}

	private static void applyResume(UserResume row, UserResumeSaveDTO dto) {
		if (dto.getName() != null) {
			row.setName(dto.getName());
		}
		if (dto.getPhoto() != null) {
			row.setPhoto(dto.getPhoto());
		}
		if (dto.getPosition() != null) {
			row.setPosition(dto.getPosition());
		}
		if (dto.getCardId() != null) {
			row.setCardId(dto.getCardId());
		}
		if (dto.getBirthday() != null) {
			row.setBirthday(dto.getBirthday());
		}
		if (dto.getAge() != null) {
			row.setAge(parseIntOrNull(dto.getAge()));
		}
		if (dto.getEducation() != null) {
			row.setEducation(dto.getEducation());
		}
		if (dto.getEducationImage() != null) {
			row.setEducationImage(dto.getEducationImage());
		}
		if (dto.getPhone() != null) {
			row.setPhone(dto.getPhone());
		}
		if (dto.getSex() != null) {
			row.setSex(dto.getSex());
		}
		if (dto.getNation() != null) {
			row.setNation(dto.getNation());
		}
		if (dto.getAcad() != null) {
			row.setAcad(dto.getAcad());
		}
		if (dto.getAcadImage() != null) {
			row.setAcadImage(dto.getAcadImage());
		}
		if (dto.getPolitic() != null) {
			row.setPolitic(dto.getPolitic());
		}
		if (dto.getNativePlace() != null) {
			row.setNativePlace(dto.getNativePlace());
		}
		if (dto.getAddress() != null) {
			row.setAddress(dto.getAddress());
		}
		if (dto.getMarriage() != null) {
			row.setMarriage(dto.getMarriage());
		}
		if (dto.getWorkYears() != null) {
			row.setWorkYears(parseIntOrNull(dto.getWorkYears()));
		}
		if (dto.getSpareName() != null) {
			row.setSpareName(dto.getSpareName());
		}
		if (dto.getSpareTel() != null) {
			row.setSpareTel(dto.getSpareTel());
		}
		if (dto.getEmail() != null) {
			row.setEmail(dto.getEmail());
		}
		if (dto.getWorkTime() != null) {
			row.setWorkTime(dto.getWorkTime());
		}
		if (dto.getTrialTime() != null) {
			row.setTrialTime(dto.getTrialTime());
		}
		if (dto.getFormalTime() != null) {
			row.setFormalTime(dto.getFormalTime());
		}
		if (dto.getTreatyTime() != null) {
			row.setTreatyTime(dto.getTreatyTime());
		}
		if (dto.getIsPart() != null) {
			row.setIsPart(dto.getIsPart());
		}
		if (dto.getSocialNum() != null) {
			row.setSocialNum(dto.getSocialNum());
		}
		if (dto.getFundNum() != null) {
			row.setFundNum(dto.getFundNum());
		}
		if (dto.getBankNum() != null) {
			row.setBankNum(dto.getBankNum());
		}
		if (dto.getBankName() != null) {
			row.setBankName(dto.getBankName());
		}
		if (dto.getGraduateName() != null) {
			row.setGraduateName(dto.getGraduateName());
		}
		if (dto.getGraduateDate() != null) {
			row.setGraduateDate(dto.getGraduateDate());
		}
		if (dto.getCardFront() != null) {
			row.setCardFront(dto.getCardFront());
		}
		if (dto.getCardBoth() != null) {
			row.setCardBoth(dto.getCardBoth());
		}
	}

	private static Integer parseIntOrNull(String s) {
		if (s == null || s.isBlank()) {
			return null;
		}
		try {
			return Integer.parseInt(s.trim());
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

}
