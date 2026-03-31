package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.security.OaCurrentUser;
import com.bubblecloud.biz.oa.service.MenusService;
import com.bubblecloud.biz.oa.service.UserProfileService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.CheckPwdDTO;
import com.bubblecloud.oa.api.dto.MenusQueryDTO;
import com.bubblecloud.oa.api.dto.UserJoinDTO;
import com.bubblecloud.oa.api.dto.UserResumeSaveDTO;
import com.bubblecloud.oa.api.dto.UserSelfUpdateDTO;
import com.bubblecloud.oa.api.vo.MenusVO;
import com.bubblecloud.oa.api.vo.user.UserResumeDetailVO;
import com.bubblecloud.oa.api.vo.user.UserSelfInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;

/**
 * OA 用户模块。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user")
@Tag(name = "用户模块")
public class UserController {

	private final MenusService menusService;

	private final UserProfileService userProfileService;

	@GetMapping("/menus")
	@Operation(summary = "获取当前用户菜单")
	public R<MenusVO> menus(Authentication authentication) {
		if (ObjectUtil.isNull(authentication)
				|| !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return R.phpFailed("未登录");
		}
		MenusQueryDTO dto = new MenusQueryDTO();
		dto.setUserId(currentUser.getId());
		return R.phpOk(menusService.menus(dto));
	}

	/**
	 * 企业邀请加入（PHP PUT ent/user/user/join）。
	 */
	@PutMapping("/user/join")
	@Operation(summary = "处理企业邀请")
	public R<String> userJoin(@RequestBody(required = false) UserJoinDTO body) {
		return R.phpOk("ok");
	}

	/**
	 * 个人账号资料（PHP User\UserController::userInfo）。与 {@code CompanyUserController} 的
	 * {@code /userInfo}（企业维度）区分，避免同路径双注册。
	 */
	@GetMapping("/account_info")
	@Operation(summary = "获取当前用户账号资料（含邮箱扩展）")
	public R<UserSelfInfoVO> accountInfo(Authentication authentication) {
		if (ObjectUtil.isNull(authentication)
				|| !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return R.phpFailed("未登录");
		}
		UserSelfInfoVO vo = userProfileService.getSelfInfo(currentUser.getId());
		return ObjectUtil.isNull(vo) ? R.phpFailed("用户不存在") : R.phpOk(vo);
	}

	@PutMapping("/account_info")
	@Operation(summary = "修改当前用户账号资料")
	public R<String> updateAccountInfo(Authentication authentication,
			@RequestBody(required = false) UserSelfUpdateDTO dto) {
		if (ObjectUtil.isNull(authentication)
				|| !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return R.phpFailed("未登录");
		}
		if (ObjectUtil.isNull(dto)) {
			// 兼容 PHP empty body
			return R.phpOk("common.update.succ");
		}
		try {
			userProfileService.updateSelf(currentUser.getId(), dto);
			return R.phpOk("common.update.succ");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PostMapping("/checkpwd")
	@Operation(summary = "验证密码规范")
	public R<String> checkPwd(@RequestBody @Valid CheckPwdDTO dto) {
		try {
			userProfileService.checkPwd(dto);
			return R.phpOk("验证成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/resume")
	@Operation(summary = "获取个人简历")
	public R<UserResumeDetailVO> resume(Authentication authentication) {
		if (ObjectUtil.isNull(authentication)
				|| !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return R.phpFailed("未登录");
		}
		return R.phpOk(userProfileService.getResume(currentUser.getId()));
	}

	@PutMapping("/resume_save")
	@Operation(summary = "保存个人简历")
	public R<String> resumeSave(Authentication authentication, @RequestBody UserResumeSaveDTO dto) {
		if (ObjectUtil.isNull(authentication)
				|| !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return R.phpFailed("未登录");
		}
		try {
			userProfileService.saveResume(currentUser.getId(), dto);
			return R.phpOk("保存成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
