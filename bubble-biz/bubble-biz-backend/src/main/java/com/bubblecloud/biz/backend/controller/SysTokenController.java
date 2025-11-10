package com.bubblecloud.biz.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.api.backend.feign.RemoteTokenService;
import com.bubblecloud.api.backend.vo.TokenVO;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author lengleng
 * @date 2018/9/4 getTokenPage 管理
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sys-token")
@Tag(description = "token", name = "令牌管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysTokenController {

	private final RemoteTokenService remoteTokenService;

	/**
	 * 分页token 信息
	 * @param params 参数集
	 * @return token集合
	 */
	@Operation(summary = "分页查询", description = "分页查询")
	@PostMapping("/page")
	public R<Page<TokenVO>> getTokenPage(@RequestBody Map<String, Object> params) {
		return remoteTokenService.getTokenPage(params);
	}

	/**
	 * 删除
	 * @param tokens tokens
	 * @return success/false
	 */
	@Operation(summary = "删除用户token", description = "删除用户token")
	@SysLog("删除用户token")
	@DeleteMapping("/delete")
	@PreAuthorize("@pms.hasPermission('sys_token_del')")
	public R removeById(@RequestBody String[] tokens) {
		for (String token : tokens) {
			remoteTokenService.removeTokenById(token);
		}
		return R.ok();
	}

}
