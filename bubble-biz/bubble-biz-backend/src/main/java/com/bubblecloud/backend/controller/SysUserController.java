package com.bubblecloud.backend.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.api.backend.dto.UserDTO;
import com.bubblecloud.api.backend.dto.UserInfo;
import com.bubblecloud.api.backend.entity.SysUser;
import com.bubblecloud.api.backend.vo.UserExcelVO;
import com.bubblecloud.api.backend.vo.UserVO;
import com.bubblecloud.backend.service.SysUserService;
import com.bubblecloud.common.core.constant.CommonConstants;
import com.bubblecloud.common.core.exception.ErrorCodes;
import com.bubblecloud.common.core.util.MsgUtils;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.log.annotation.SysLog;
import com.bubblecloud.common.security.annotation.Inner;
import com.bubblecloud.common.security.util.SecurityUtils;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author lengleng
 * @date 2018/12/16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user")
@Tag(description = "user", name = "用户管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysUserController {

	private final SysUserService userService;

	/**
	 * 获取指定用户全部信息
	 * @return 用户信息
	 */
	@Operation(summary = "获取指定用户全部信息", description = "获取指定用户全部信息")
	@Inner
	@GetMapping(value = { "/info/query" })
	public R<UserInfo> info(@RequestParam(required = false) String username, @RequestParam(required = false) String phone) {
		SysUser user = userService.getOne(Wrappers.<SysUser>query()
			.lambda()
			.eq(StrUtil.isNotBlank(username), SysUser::getUsername, username)
			.eq(StrUtil.isNotBlank(phone), SysUser::getPhone, phone));
		if (user == null) {
			return R.failed(MsgUtils.getMessage(ErrorCodes.SYS_USER_USERINFO_EMPTY, username));
		}
		return R.ok(userService.findUserInfo(user));
	}

	/**
	 * 获取当前用户全部信息
	 * @return 用户信息
	 */
	@Operation(summary = "获取当前用户全部信息", description = "获取当前用户全部信息")
	@GetMapping(value = { "/info" })
	public R<UserInfo> info() {
		String username = SecurityUtils.getUser().getUsername();
		SysUser user = userService.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
		if (user == null) {
			return R.failed(MsgUtils.getMessage(ErrorCodes.SYS_USER_QUERY_ERROR));
		}
		return R.ok(userService.findUserInfo(user));
	}

	/**
	 * 通过ID查询用户信息
	 * @param id ID
	 * @return 用户信息
	 */
	@Operation(summary = "通过ID查询用户信息", description = "通过ID查询用户信息")
	@GetMapping("/details/{id}")
	public R<UserVO> user(@PathVariable Long id) {
		return R.ok(userService.selectUserVoById(id));
	}

	/**
	 * 查询用户信息
	 * @param query 查询条件
	 * @return 不为空返回用户名
	 */
	@Operation(summary = "查询用户信息", description = "查询用户信息")
	@Inner(value = false)
	@GetMapping("/details")
	public R getDetails(@ParameterObject SysUser query) {
		SysUser sysUser = userService.getOne(Wrappers.query(query), false);
		return R.ok(sysUser == null ? null : CommonConstants.SUCCESS);
	}

	/**
	 * 删除用户信息
	 * @param ids ID
	 * @return R
	 */
	@Operation(summary = "删除用户", description = "根据ID删除用户")
	@SysLog("删除用户信息")
	@DeleteMapping
	@PreAuthorize("@pms.hasPermission('sys_user_del')")
	public R userDel(@RequestBody Long[] ids) {
		return R.ok(userService.deleteUserByIds(ids));
	}

	/**
	 * 添加用户
	 * @param userDto 用户信息
	 * @return success/false
	 */
	@Operation(summary = "添加用户", description = "添加用户")
	@SysLog("添加用户")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_user_add')")
	public R user(@RequestBody UserDTO userDto) {
		return R.ok(userService.saveUser(userDto));
	}

	/**
	 * 更新用户信息
	 * @param userDto 用户信息
	 * @return R
	 */
	@Operation(summary = "更新用户信息", description = "更新用户信息")
	@SysLog("更新用户信息")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('sys_user_edit')")
	public R updateUser(@Valid @RequestBody UserDTO userDto) {
		return R.ok(userService.updateUser(userDto));
	}

	/**
	 * 分页查询用户
	 * @param page 参数集
	 * @param userDTO 查询参数列表
	 * @return 用户集合
	 */
	@Operation(summary = "分页查询用户", description = "分页查询用户")
	@GetMapping("/page")
	public R<IPage<UserVO>> getUserPage(@ParameterObject Page page, @ParameterObject UserDTO userDTO) {
		return R.ok(userService.getUsersWithRolePage(page, userDTO));
	}

	/**
	 * 修改个人信息
	 * @param userDto userDto
	 * @return success/false
	 */
	@Operation(summary = "修改个人信息", description = "修改个人信息")
	@SysLog("修改个人信息")
	@PutMapping("/edit")
	public R updateUserInfo(@Valid @RequestBody UserDTO userDto) {
		return userService.updateUserInfo(userDto);
	}

	/**
	 * 导出excel 表格
	 * @param userDTO 查询条件
	 * @return
	 */
	@Operation(summary = "导出用户", description = "导出用户")
	@ResponseExcel
	@GetMapping("/export")
	@PreAuthorize("@pms.hasPermission('sys_user_export')")
	public List export(UserDTO userDTO) {
		return userService.listUser(userDTO);
	}

	/**
	 * 导入用户
	 * @param excelVOList 用户列表
	 * @param bindingResult 错误信息列表
	 * @return R
	 */
	@Operation(summary = "导入用户", description = "导入用户")
	@PostMapping("/import")
	@PreAuthorize("@pms.hasPermission('sys_user_export')")
	public R importUser(@RequestExcel List<UserExcelVO> excelVOList, BindingResult bindingResult) {
		return userService.importUser(excelVOList, bindingResult);
	}

	/**
	 * 锁定指定用户
	 * @param username 用户名
	 * @return R
	 */
	@Operation(summary = "锁定指定用户", description = "锁定指定用户")
	@PutMapping("/lock/{username}")
	public R lockUser(@PathVariable String username) {
		return userService.lockUser(username);
	}

	@Operation(summary = "更改用户密码", description = "更改用户密码")
	@PutMapping("/password")
	public R password(@RequestBody UserDTO userDto) {
		String username = SecurityUtils.getUser().getUsername();
		userDto.setUsername(username);
		return userService.changePassword(userDto);
	}

	@Operation(summary = "检查密码", description = "检查密码")
	@PostMapping("/check")
	public R check(String password) {
		return userService.checkPassword(password);
	}

}
