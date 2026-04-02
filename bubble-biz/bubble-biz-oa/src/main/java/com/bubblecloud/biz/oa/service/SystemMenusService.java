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

}
