package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.oa.api.entity.SystemMenus;
import com.bubblecloud.oa.api.vo.menu.MenuAdminTreeNodeVO;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 企业菜单管理 CRUD（对齐 PHP {@code MenuController}，无 Casbin）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface MenusAdminService {

	/**
	 * 企业菜单树（与 PHP 列表一致）。
	 * @param menuName 菜单名模糊（可选）
	 * @param entid 企业 ID
	 */
	List<MenuAdminTreeNodeVO> listMenuTree(String menuName, int entid);

	void saveMenu(SystemMenus menu);

	void updateMenu(SystemMenus menu);

	void deleteMenu(long id);

	void updateIsShow(long id, int isShow);

	JsonNode getNotSaveMenus(int entid);

}
