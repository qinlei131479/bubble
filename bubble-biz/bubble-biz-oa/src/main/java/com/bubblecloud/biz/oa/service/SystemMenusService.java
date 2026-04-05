package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.SystemMenus;
import com.bubblecloud.oa.api.vo.menu.SystemMenusTreeNodeVO;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 企业菜单管理 CRUD（对齐 PHP {@code MenuController}，无 Casbin）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface SystemMenusService extends UpService<SystemMenus> {

	/**
	 * 企业菜单树（与 PHP 列表一致）。
	 * @param menuName 菜单名模糊（可选）
	 * @param entId 企业 ID
	 */
	List<SystemMenusTreeNodeVO> listMenuTree(String menuName, Long entId);

	void updateIsShow(Long id, Integer isShow);

	JsonNode getNotSaveMenus(Long entId);

	/**
	 * 角色权限树（form-create cascader，含 value/label/is_default）。
	 */
	JsonNode buildRoleMenuCascader(Long entId, java.util.Collection<Long> defaultCheckedIds);

	/**
	 * 某菜单下子权限（一般为 type=B 按钮）。
	 */
	JsonNode listPidMenuRules(Long pid);

	/**
	 * 菜单管理抽屉：创建（form-create rule + action）。
	 */
	JsonNode getMenuDrawerCreateForm(Long entId);

	/**
	 * 菜单管理抽屉：编辑。
	 */
	JsonNode getMenuDrawerUpdateForm(Long id, Long entId);

	/**
	 * 保存企业菜单权限到超级角色（PHP saveMenusForCompany；当前无 system_role 时为空操作）。
	 */
	void saveMenusForCompany(Long entId);

	/**
	 * 菜单抽屉提交：解析 path 数组、pid、level（对齐 PHP store）。
	 */
	void saveMenuFromForm(Long entId, JsonNode body);

	/**
	 * 菜单抽屉修改提交（对齐 PHP update）。
	 */
	void updateMenuFromForm(Long id, Long entId, JsonNode body);

}
