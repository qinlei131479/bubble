package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.EnterpriseRole;
import com.bubblecloud.oa.api.vo.role.EnterpriseRoleListItemVO;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 企业角色与成员（对齐 PHP {@code RoleController}，无 Casbin）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface EnterpriseRoleService extends UpService<EnterpriseRole> {

	/**
	 * 角色列表（含 frame 摘要，蛇形 JSON）。
	 */
	List<EnterpriseRoleListItemVO> listRoles(String roleName, Long entId);

	/**
	 * 创建/编辑抽屉数据：tree + rule + crud（crud 无低代码表时为空数组）。
	 */
	JsonNode getRoleInfo(Long entId, Long roleId);

	Long saveRole(Long entId, JsonNode body);

	void updateRole(Long id, Long entId, JsonNode body);

	void deleteRole(Long id, Long entId);

	void changeRoleStatus(Long entId, Long roleId, Integer status);

	/**
	 * 角色成员分页列表（蛇形 list + count）。
	 */
	JsonNode getRoleUserList(Long roleId, Long entId, Integer page, Integer limit);

	JsonNode getUserRoleData(Long entId, Long userId);

	void changeUserRole(Long entId, Long userId, JsonNode roleIds);

	void addRoleUsers(Long entId, Long roleId, List<Long> userIds, List<Integer> frameIds);

	void changeRoleUserStatus(Long uid, Long entId, Long roleId, Integer status);

	void delRoleUser(Long uid, Long entId, Long roleId);

	/**
	 * 修改企业超级角色权限（PHP updateSuperRole；无 system_role 表时仅校验 JSON）。
	 */
	void updateSuperRole(Long entId, JsonNode body);

	/**
	 * 超级角色菜单（PHP getSuperRole；当前返回空树占位）。
	 */
	JsonNode getSuperRoleMenus(Long entId, String menuName);

	/**
	 * 某菜单下按钮权限（PHP getMenusRule / 前端 rule_list）。
	 */
	JsonNode getMenusRuleByPid(Long pid);

}
