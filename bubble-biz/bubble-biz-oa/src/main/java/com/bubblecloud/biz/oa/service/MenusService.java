package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.dto.MenusQueryDTO;
import com.bubblecloud.oa.api.vo.MenusVO;

/**
 * 用户菜单与按钮权限（基于 eb_admin、eb_enterprise_role、eb_system_menus）。
 *
 * @author qinlei
 */
public interface MenusService {

	/**
	 * 当前用户菜单树与按钮权限标识。
	 *
	 * @param dto 查询参数（含用户ID）
	 * @return 菜单 VO
	 */
	MenusVO menus(MenusQueryDTO dto);

}
