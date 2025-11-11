package com.bubblecloud.biz.backend.service.impl;

import java.util.Arrays;
import java.util.List;

import com.bubblecloud.biz.backend.service.SysRoleMenuService;
import com.bubblecloud.backend.api.entity.SysRoleMenu;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubblecloud.biz.backend.mapper.SysRoleMenuMapper;
import com.bubblecloud.common.core.constant.CacheConstants;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * 角色菜单表服务实现类
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Service
@AllArgsConstructor
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

	private final CacheManager cacheManager;

	/**
	 * @param roleId 角色
	 * @param menuIds 菜单ID拼成的字符串，每个id之间根据逗号分隔
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.MENU_DETAILS, key = "#roleId")
	public Boolean saveRoleMenus(Long roleId, String menuIds) {
		this.remove(Wrappers.<SysRoleMenu>query().lambda().eq(SysRoleMenu::getRoleId, roleId));

		if (StrUtil.isBlank(menuIds)) {
			return Boolean.TRUE;
		}
		List<SysRoleMenu> roleMenuList = Arrays.stream(menuIds.split(StrUtil.COMMA)).map(menuId -> {
			SysRoleMenu roleMenu = new SysRoleMenu();
			roleMenu.setRoleId(roleId);
			roleMenu.setMenuId(Long.valueOf(menuId));
			return roleMenu;
		}).toList();

		// 清空userinfo
		cacheManager.getCache(CacheConstants.USER_DETAILS).clear();
		this.saveBatch(roleMenuList);
		return Boolean.TRUE;
	}

}
