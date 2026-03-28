package com.bubblecloud.oa.api.vo;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录后用户信息（与 PHP AdminService::loginInfo 一致：userInfo + enterprise）。
 *
 * @author qinlei
 */
@Data
@Schema(description = "登录会话信息")
public class LoginInfoVO {

	@Schema(description = "当前用户完整信息（含 frames、job、roles 数组、real_name 等）")
	private Map<String, Object> userInfo;

	@Schema(description = "企业信息（含 entid、maxScore、culture、compute_mode 等）")
	private Map<String, Object> enterprise;

}
