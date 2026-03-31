package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.CommonService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.dto.SmsVerifySendDTO;
import com.bubblecloud.oa.api.vo.CaptchaVO;
import com.bubblecloud.oa.api.vo.ConfigVO;
import com.bubblecloud.oa.api.vo.SiteVO;
import com.bubblecloud.oa.api.vo.SmsVerifyKeyVO;
import com.bubblecloud.oa.api.vo.common.CommonAuthVO;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;
import com.bubblecloud.oa.api.vo.common.CommonVersionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OA 公共接口（仅路由与参数绑定，业务见 {@link CommonService}）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/common")
@Tag(name = "公共接口")
public class CommonController {

	private final CommonService commonService;

	@GetMapping("/captcha")
	@Operation(summary = "验证码占位接口")
	public R<CaptchaVO> captcha() {
		return R.phpOk(commonService.captcha());
	}

	@GetMapping("/config")
	@Operation(summary = "读取系统配置")
	public R<ConfigVO> config(@RequestParam(defaultValue = "system") String type) {
		ConfigQueryDTO dto = new ConfigQueryDTO();
		dto.setType(type);
		return R.phpOk(commonService.config(dto));
	}

	@GetMapping("/site")
	@Operation(summary = "站点配置")
	public R<SiteVO> site() {
		return R.phpOk(commonService.site());
	}

	/**
	 * 商业授权占位（PHP common/auth，前端 entAuth 判断 status）。
	 */
	@GetMapping("/auth")
	@Operation(summary = "授权信息占位")
	public R<CommonAuthVO> auth() {
		return R.phpOk(commonService.auth());
	}

	@GetMapping("/verify/key")
	@Operation(summary = "获取短信发送 KEY")
	public R<SmsVerifyKeyVO> verifyKey() {
		return R.phpOk(commonService.verifyKey());
	}

	@PostMapping("/verify")
	@Operation(summary = "发送短信验证码")
	public R<String> verify(@RequestBody @Valid SmsVerifySendDTO dto) {
		commonService.sendVerifySms(dto);
		return R.phpOk("短信发送成功");
	}

	@GetMapping("/version")
	@Operation(summary = "版本信息")
	public R<CommonVersionVO> version() {
		return R.phpOk(commonService.version());
	}

	@GetMapping("/message")
	@Operation(summary = "消息列表（工作台角标）")
	public R<CommonMessageVO> message(Authentication authentication, @RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer limit, @RequestParam(required = false) String cate_id,
			@RequestParam(required = false) String title) {
		return R.phpOk(commonService.messageList(authentication, page, limit, cate_id, title));
	}

	@PutMapping("/message/{id}/{isRead}")
	@Operation(summary = "修改消息已读状态")
	public R<String> updateMessage(Authentication authentication, @PathVariable long id, @PathVariable int isRead) {
		commonService.updateMessageRead(authentication, id, isRead);
		return R.phpOk("common.update.succ");
	}

}
