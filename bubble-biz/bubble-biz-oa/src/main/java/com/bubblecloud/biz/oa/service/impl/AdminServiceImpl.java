package com.bubblecloud.biz.oa.service.impl;

import java.security.SecureRandom;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Admin;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;

/**
 * eb_admin 员工账号服务实现。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
public class AdminServiceImpl extends UpServiceImpl<AdminMapper, Admin> implements AdminService {

	private static final String AVATAR_BASE = "https://shmily-album.oss-cn-shenzhen.aliyuncs.com/admin_face/face%d.png";

	private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

	private final SecureRandom random = new SecureRandom();

	@Override
	public Admin getByAccount(String account) {
		Admin admin = this.getOne(
				Wrappers.lambdaQuery(Admin.class).eq(Admin::getAccount, account).isNull(Admin::getDeletedAt), false);
		if (ObjectUtil.isNull(admin)) {
			admin = this.getOne(
					Wrappers.lambdaQuery(Admin.class).eq(Admin::getPhone, account).isNull(Admin::getDeletedAt), false);
		}
		return admin;
	}

	@Override
	public long countByPhone(String phone) {
		return this.count(Wrappers.lambdaQuery(Admin.class).eq(Admin::getPhone, phone).isNull(Admin::getDeletedAt));
	}

	@Override
	public Admin createRegisteredUser(String phone, String encodedPassword) {
		Admin a = new Admin();
		a.setUid(java.util.UUID.randomUUID().toString().replace("-", ""));
		a.setAccount(phone);
		a.setPhone(phone);
		a.setName(phone.substring(0, 3) + "****" + phone.substring(7));
		a.setPassword(encodedPassword);
		a.setAvatar(String.format(AVATAR_BASE, random.nextInt(10) + 1));
		a.setStatus(1);
		a.setIsInit(1);
		a.setIsAdmin(0);
		a.setRoles("[]");
		this.save(a);
		return a;
	}

	@Override
	public Admin ensureUserForPhoneLogin(String phone) {
		Admin existing = getByAccount(phone);
		if (ObjectUtil.isNotNull(existing)) {
			return existing;
		}
		return createRegisteredUser(phone, ENCODER.encode(java.util.UUID.randomUUID().toString()));
	}

	@Override
	public void updatePasswordByUid(String uid, String rawPassword) {
		Admin admin = this.getOne(Wrappers.lambdaQuery(Admin.class).eq(Admin::getUid, uid).isNull(Admin::getDeletedAt),
				false);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("用户不存在");
		}
		admin.setPassword(ENCODER.encode(rawPassword));
		admin.setIsInit(0);
		this.updateById(admin);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Admin req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Admin req) {
		return super.update(req);
	}

}
