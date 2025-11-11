package com.bubblecloud.biz.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubblecloud.backend.api.entity.SysRoleMenu;

/**
 * 角色菜单表服务接口
 *
 * @author lengleng
 * @since 2017-10-29
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

	/**
	 * 更新角色菜单
	 * @param roleId 角色ID
	 * @param menuIds 菜单ID字符串，以逗号分隔
	 * @return 更新是否成功
	 */
	Boolean saveRoleMenus(Long roleId, String menuIds);

}
