package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.SiteService;
import com.bubblecloud.biz.oa.service.SystemConfigService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.vo.CaptchaVO;
import com.bubblecloud.oa.api.vo.ConfigVO;
import com.bubblecloud.oa.api.vo.SiteVO;
import com.bubblecloud.oa.api.vo.common.CommonAuthVO;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OA 公共接口。
 *
 * @author qinlei
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/common")
@Tag(name = "公共接口")
public class CommonController {

	private final SystemConfigService systemConfigService;

	private final SiteService siteService;

	@GetMapping("/captcha")
	@Operation(summary = "验证码占位接口")
	public PhpResponse<CaptchaVO> captcha() {
		return PhpResponse.ok(new CaptchaVO("oa-captcha-key", ""));
	}

	@GetMapping("/config")
	@Operation(summary = "读取系统配置")
	public PhpResponse<ConfigVO> config(@RequestParam(defaultValue = "system") String type) {
		ConfigQueryDTO dto = new ConfigQueryDTO();
		dto.setType(type);
		return PhpResponse.ok(systemConfigService.config(dto));
	}

	@GetMapping("/site")
	@Operation(summary = "站点配置")
	public PhpResponse<SiteVO> site() {
		return PhpResponse.ok(siteService.site());
	}

	/**
	 * 商业授权占位（PHP common/auth，前端 entAuth 判断 status）。
	 */
	@GetMapping("/auth")
	@Operation(summary = "授权信息占位")
	public PhpResponse<CommonAuthVO> auth() {
		return PhpResponse.ok(new CommonAuthVO(1, 999));
	}

	/**
	 * 与 PHP {@code ent/common/message} 一致：{@code list} + {@code messageNum}。
	 */
	@GetMapping("/message")
	@Operation(summary = "消息列表（工作台角标）")
	public PhpResponse<CommonMessageVO> message(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer limit,
			@RequestParam(required = false) String cate_id,
			@RequestParam(required = false) String title) {
		return PhpResponse.ok(new CommonMessageVO());
	}

}
