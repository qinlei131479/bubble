package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.EnterpriseUserService;
import com.bubblecloud.biz.oa.service.MenusService;
import com.bubblecloud.biz.oa.service.ScheduleApiService;
import com.bubblecloud.biz.oa.service.UserProfileService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.*;
import com.bubblecloud.oa.api.vo.MenusVO;
import com.bubblecloud.oa.api.vo.schedule.UserScheduleDayWrapperVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseUserProfileVO;
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

	private final EnterpriseUserService enterpriseUserService;

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
	 * PHP {@code User\UserController::userInfo}：无 {@code entid} 时返回账号+扩展信息；带 {@code entid}
	 * 时返回企业关联用户视图（对齐原 {@code EnterpriseUserController} 行为，与 PHP 企业端用法一致）。
	 */
	@GetMapping("/userInfo")
	@Operation(summary = "获取用户信息（个人资料 / 企业关联视图）")
	public R<Object> userInfoGet(@RequestParam(required = false) Long entid) {
		Long uid = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNotNull(entid)) {
			EnterpriseUserProfileVO vo = enterpriseUserService.userInfo(uid, entid);
			return R.phpOk(vo);
		}
		UserSelfInfoVO vo = userProfileService.getSelfInfo(uid);
		return ObjectUtil.isNull(vo) ? R.phpFailed("用户不存在") : R.phpOk(vo);
	}

	/**
	 * PHP {@code User\UserController::update}，仅更新当前登录账号主表与 {@code eb_admin_info}。
	 */
	@PutMapping("/userInfo")
	@Operation(summary = "修改当前用户账号资料")
	public R<String> userInfoPut(@RequestBody(required = false) UserSelfUpdateDTO dto) {
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
