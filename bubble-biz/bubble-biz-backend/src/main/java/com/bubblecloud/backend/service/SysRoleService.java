package com.bubblecloud.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubblecloud.backend.api.entity.SysRole;
import com.bubblecloud.backend.api.vo.RoleExcelVO;
import com.bubblecloud.backend.api.vo.RoleVO;
import com.bubblecloud.common.core.util.R;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * 系统角色服务接口
 * <p>
 * 提供角色相关的业务功能，包括角色查询、删除、更新菜单及导入导出等操作
 * </p>
 *
 * @author qinlei
 * @since 2017-10-29
 */
public interface SysRoleService extends IService<SysRole> {

	/**
	 * 通过用户ID查询角色信息
	 * @param userId 用户ID
	 * @return 角色信息列表
	 */
	List<SysRole> listRolesByUserId(Long userId);

	/**
	 * 根据角色ID列表查询角色信息
	 * @param roleIdList 角色ID列表，不能为空
	 * @param key 缓存键值
	 * @return 查询到的角色列表
	 */
	List<SysRole> listRolesByRoleIds(List<Long> roleIdList, String key);

	/**
	 * 通过角色ID数组删除角色
	 * @param ids 要删除的角色ID数组
	 * @return 删除是否成功
	 */
	Boolean removeRoleByIds(Long[] ids);

	/**
	 * 更新角色菜单列表
	 * @param roleVo 包含角色和菜单列表信息的VO对象
	 * @return 更新是否成功
	 */
	Boolean updateRoleMenus(RoleVO roleVo);

	/**
	 * 导入角色
	 * @param excelVOList 角色列表
	 * @param bindingResult 错误信息列表
	 * @return 导入结果
	 */
	R importRole(List<RoleExcelVO> excelVOList, BindingResult bindingResult);

	/**
	 * 查询全部角色列表
	 * @return 角色列表，包含角色Excel视图对象
	 */
	List<RoleExcelVO> listRoles();

}
