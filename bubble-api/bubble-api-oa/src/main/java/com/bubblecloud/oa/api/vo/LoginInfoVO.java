package com.bubblecloud.oa.api.vo;

import com.bubblecloud.oa.api.vo.auth.EnterpriseLoginVO;
import com.bubblecloud.oa.api.vo.auth.LoginUserInfoPayloadVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录后用户信息（与 PHP AdminService::loginInfo 一致：userInfo + enterprise）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@Schema(description = "登录会话信息")
public class LoginInfoVO {

	@Schema(description = "当前用户完整信息（含 frames、job、roles、real_name 等）")
	private LoginUserInfoPayloadVO userInfo;

	@Schema(description = "企业信息")
	private EnterpriseLoginVO enterprise;

}
