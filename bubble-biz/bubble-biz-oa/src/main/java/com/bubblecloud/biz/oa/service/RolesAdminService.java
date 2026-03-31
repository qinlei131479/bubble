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
public interface RolesAdminService extends UpService<EnterpriseRole> {

	/**
	 * 角色列表（筛选）。
	 * @param roleName 角色名模糊（可选）
	 * @param entid 企业 ID
	 */
	List<EnterpriseRole> listRoles(String roleName, int entid);

	long saveRole(int entid, JsonNode body);

	void updateRole(long id, int entid, JsonNode body);

	void deleteRole(long id, int entid);

	void changeRoleStatus(int entid, long roleId, int status);

	List<Admin> getRoleUsers(long roleId, int entid);

	JsonNode getUserRoleData(int entid, long userId);

	void changeUserRole(int entid, long userId, JsonNode roleIds);

	void addRoleUsers(int entid, int roleId, List<Integer> userIds);

	void changeRoleUserStatus(int uid, int entid, int roleId, int status);

	void delRoleUser(int uid, int entid, int roleId);

}
