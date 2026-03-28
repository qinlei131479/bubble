package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.security.OaCurrentUser;
import com.bubblecloud.biz.oa.service.MenusService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.dto.MenusQueryDTO;
import com.bubblecloud.oa.api.vo.MenusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OA 用户模块。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user")
@Tag(name = "用户模块")
public class UserController {

	private final MenusService menusService;

	@GetMapping("/menus")
	@Operation(summary = "获取当前用户菜单")
	public PhpResponse<MenusVO> menus(Authentication authentication) {
		if (authentication == null || !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return PhpResponse.failed("未登录");
		}
		MenusQueryDTO dto = new MenusQueryDTO();
		dto.setUserId(currentUser.getId());
		return PhpResponse.ok(menusService.menus(dto));
	}

}
