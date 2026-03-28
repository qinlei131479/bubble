package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Admin;
import org.springframework.stereotype.Service;

/**
 * eb_admin 员工账号服务实现。
 *
 * @author qinlei
 */
@Service
public class AdminServiceImpl extends UpServiceImpl<AdminMapper, Admin> implements AdminService {

	@Override
	public Admin getByAccount(String account) {
		Admin admin = this.getOne(Wrappers.lambdaQuery(Admin.class)
				.eq(Admin::getAccount, account)
				.isNull(Admin::getDeletedAt), false);
		if (admin == null) {
			admin = this.getOne(Wrappers.lambdaQuery(Admin.class)
					.eq(Admin::getPhone, account)
					.isNull(Admin::getDeletedAt), false);
		}
		return admin;
	}

}
