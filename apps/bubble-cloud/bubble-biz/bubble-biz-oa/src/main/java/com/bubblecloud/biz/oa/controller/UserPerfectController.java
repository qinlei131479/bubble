package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.UserPerfectBizService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.vo.perfect.UserPerfectIndexVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邀请完善档案（对齐 PHP {@code ent/user/perfect}）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user/perfect")
@Tag(name = "邀请完善档案")
public class UserPerfectController {

	private final UserPerfectBizService userPerfectBizService;

	@GetMapping("/index")
	@Operation(summary = "邀请记录列表")
	public R<UserPerfectIndexVO> index(@RequestParam(required = false) Integer status,
			@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit) {
		try {
			return R
				.phpOk(userPerfectBizService.listForCurrentUser(OaSecurityUtil.currentUserId(), status, page, limit));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/agree/{id}")
	@Operation(summary = "同意同步个人资料")
	public R<String> agree(@PathVariable Long id) {
		try {
			userPerfectBizService.agree(OaSecurityUtil.currentUserId(), id);
			return R.phpOk("操作成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/refuse/{id}")
	@Operation(summary = "拒绝")
	public R<String> refuse(@PathVariable Long id) {
		try {
			userPerfectBizService.refuse(id);
			return R.phpOk("操作成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
