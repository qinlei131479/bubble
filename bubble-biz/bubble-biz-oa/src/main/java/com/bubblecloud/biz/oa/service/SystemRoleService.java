package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.SystemRole;

/**
 * 系统角色（eb_system_role），承载企业超级角色 rules/apis。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
public interface SystemRoleService extends UpService<SystemRole> {

	/**
	 * 平台企业默认权限模板（entid=0，type 为 0 或空）。
	 */
	SystemRole getDefaultEnterpriseTemplate();

	/**
	 * 当前企业超级角色（type=enterprise，level=0）。
	 */
	SystemRole getEnterpriseSuperRole(Long entId);

	/**
	 * 写入或更新企业超级角色的 rules、apis（JSON 数组序列化入库）。
	 */
	void saveEnterpriseSuperRole(Long entId, List<Long> rules, List<Long> apis);

}
