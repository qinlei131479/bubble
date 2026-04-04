package com.bubblecloud.biz.oa.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.bubblecloud.oa.api.vo.common.CommonDownloadUrlVO;
import com.bubblecloud.oa.api.vo.common.CommonUploadResultVO;
import com.bubblecloud.oa.api.vo.common.UploadTempKeysVO;

/**
 * 企业公共上传/下载（对齐 PHP {@code ent/common} 的 upload_key、upload、download_url、download）。
 *
 * @author qinlei
 * @date 2026/4/4 18:00
 */
public interface CommonEntUploadService {

	/**
	 * 获取上传配置；非本地上传类型若未实现则抛出 {@link IllegalArgumentException}。
	 */
	UploadTempKeysVO getTempKeys(String key, String path, String contentType);

	/**
	 * 上传或分片上传。分片未完成时返回 null，由 Controller 返回 ok 字符串。
	 */
	CommonUploadResultVO upload(MultipartFile file, int cid, String relationType, Integer relationId, int way,
			Integer eid, String md5, Integer chunkIndex, Integer chunkTotal, int entid, String uid, String clientIp);

	/**
	 * 构建带签名的下载 URL（签名由 Java HMAC 生成，与 PHP Laravel Crypt 不互通）。
	 */
	CommonDownloadUrlVO buildDownloadUrl(Integer version, String type, String fileId);

	/**
	 * 按签名输出文件流；失败时抛出 {@link IllegalArgumentException}。
	 */
	ResponseEntity<Resource> download(String signature);

}
