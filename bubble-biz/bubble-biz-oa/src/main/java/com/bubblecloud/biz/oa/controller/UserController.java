package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.MenusService;
import com.bubblecloud.biz.oa.service.ScheduleApiService;
import com.bubblecloud.biz.oa.service.UserProfileService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.*;
import com.bubblecloud.oa.api.vo.MenusVO;
import com.bubblecloud.oa.api.vo.schedule.UserScheduleDayWrapperVO;
import com.bubblecloud.oa.api.vo.user.UserResumeDetailVO;
import com.bubblecloud.oa.api.vo.user.UserSelfInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import cn.hutool.core.util.ObjectUtil;

import java.util.List;

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

	private final ScheduleApiService scheduleApiService;

	@GetMapping("/menus")
	@Operation(summary = "获取当前用户菜单")
	public R<MenusVO> menus() {
		MenusQueryDTO dto = new MenusQueryDTO();
		dto.setUserId(OaSecurityUtil.currentUserId());
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
	public R<UserSelfInfoVO> accountInfo() {
		UserSelfInfoVO vo = userProfileService.getSelfInfo(OaSecurityUtil.currentUserId());
		return ObjectUtil.isNull(vo) ? R.phpFailed("用户不存在") : R.phpOk(vo);
	}

	@PutMapping("/account_info")
	@Operation(summary = "修改当前用户账号资料")
	public R<String> updateAccountInfo(@RequestBody(required = false) UserSelfUpdateDTO dto) {
		userProfileService.updateSelf(OaSecurityUtil.currentUserId(), dto);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@PostMapping("/checkpwd")
	@Operation(summary = "验证密码规范")
	public R<String> checkPwd(@RequestBody @Valid CheckPwdDTO dto) {
		userProfileService.checkPwd(dto);
		return R.phpOk("验证成功");
	}

	@GetMapping("/resume")
	@Operation(summary = "获取个人简历")
	public R<UserResumeDetailVO> resume() {
		return R.phpOk(userProfileService.getResume(OaSecurityUtil.currentUserId()));
	}

	@PutMapping("/resume_save")
	@Operation(summary = "保存个人简历")
	public R<String> resumeSave(@RequestBody UserResumeSaveDTO dto) {
		userProfileService.saveResume(OaSecurityUtil.currentUserId(), dto);
		return R.phpOk("保存成功");
	}

	@GetMapping("/schedule")
	@Operation(summary = "用户-日历待办列表")
	public R<List<UserScheduleDayWrapperVO>> schedule(@ModelAttribute UserScheduleQueryDTO query) {
		return R.phpOk(scheduleApiService.userScheduleList(OaSecurityUtil.currentUserId(), query));
	}

}
