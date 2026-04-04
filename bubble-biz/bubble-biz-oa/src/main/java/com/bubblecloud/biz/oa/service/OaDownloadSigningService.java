package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.dto.common.OaDownloadPayloadDTO;

/**
 * 通用下载链接 HMAC 签名（Java 签发、Java 验签；与 PHP Laravel Crypt 不互通，全量切 Java 后由本服务生成 URL）。
 *
 * @author qinlei
 * @date 2026/4/4 18:00
 */
public interface OaDownloadSigningService {

	/**
	 * 生成 query 参数 signature 值。
	 */
	String sign(OaDownloadPayloadDTO payload);

	/**
	 * 验签并反序列化；失败抛出 IllegalArgumentException。
	 */
	OaDownloadPayloadDTO verify(String signature);

}
