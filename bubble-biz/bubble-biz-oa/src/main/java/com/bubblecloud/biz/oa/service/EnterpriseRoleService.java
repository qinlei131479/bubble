package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.EnterpriseRole;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 企业角色与成员（对齐 PHP {@code RoleController}，无 Casbin）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface EnterpriseRoleService extends UpService<EnterpriseRole> {

	/**
	 * 角色列表（筛选）。
	 * @param roleName 角色名模糊（可选）
	 * @param entId 企业 ID
	 */
	List<EnterpriseRole> listRoles(String roleName, Long entId);

	Long saveRole(Long entId, JsonNode body);

	void updateRole(Long id, Long entId, JsonNode body);

	void deleteRole(Long id, Long entId);

	void changeRoleStatus(Long entId, Long roleId, Integer status);

	List<Admin> getRoleUsers(Long roleId, Long entId);

	JsonNode getUserRoleData(Long entId, Long userId);

	void changeUserRole(Long entId, Long userId, JsonNode roleIds);

	void addRoleUsers(Long entId, Long roleId, List<Long> userIds);

	void changeRoleUserStatus(Long uid, Long entId, Long roleId, Integer status);

	void delRoleUser(Long uid, Long entId, Long roleId);

}
