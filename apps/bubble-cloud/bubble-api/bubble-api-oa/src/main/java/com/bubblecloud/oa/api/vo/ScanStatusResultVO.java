package com.bubblecloud.oa.api.vo;

import lombok.Data;

/**
 * POST /ent/user/scan_status 返回：轮询状态或登录成功数据。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
public class ScanStatusResultVO {

	/** 未登录完成时：0 未扫码，1 已扫码，-1 参数失效 */
	private Integer status;

	private String msg;

	/** 扫码确认后返回与账号密码登录一致的 token 信息 */
	private LoginVO login;

}
