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
	List<EnterpriseRole> listRoles(String roleName, Integer entid);

	Long saveRole(Integer entid, JsonNode body);

	void updateRole(Long id, Integer entid, JsonNode body);

	void deleteRole(Long id, Integer entid);

	void changeRoleStatus(Integer entid, Long roleId, Integer status);

	List<Admin> getRoleUsers(Long roleId, Integer entid);

	JsonNode getUserRoleData(Integer entid, Long userId);

	void changeUserRole(Integer entid, Long userId, JsonNode roleIds);

	void addRoleUsers(Integer entid, Integer roleId, List<Integer> userIds);

	void changeRoleUserStatus(Integer uid, Integer entid, Integer roleId, Integer status);

	void delRoleUser(Integer uid, Integer entid, Integer roleId);

}
