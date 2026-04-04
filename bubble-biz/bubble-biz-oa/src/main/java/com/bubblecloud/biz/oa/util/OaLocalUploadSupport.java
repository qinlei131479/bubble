package com.bubblecloud.biz.oa.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

/**
 * OA 本地上传根目录与落盘路径（CRM 客户文件、系统附件等共用）。
 *
 * @author qinlei
 * @date 2026/4/4 12:00
 */
public final class OaLocalUploadSupport {

	private OaLocalUploadSupport() {
	}

	/**
	 * 上传根目录，可通过 JVM 参数 {@code -Doa.upload.dir=} 覆盖，默认 {@code user.dir/data/oa-upload}。
	 */
	public static Path uploadRoot() {
		String base = System.getProperty("oa.upload.dir", System.getProperty("user.dir") + "/data/oa-upload");
		return Path.of(base);
	}

	/**
	 * 将磁盘相对路径（如 {@code attach/2026/04/x.png}）解析为绝对路径并删除文件（忽略异常）。
	 */
	public static void deleteRelativeFileIfExists(String attDir) {
		if (StrUtil.isBlank(attDir)) {
			return;
		}
		try {
			Path p = uploadRoot().resolve(attDir.replaceFirst("^/+", ""));
			Files.deleteIfExists(p);
		}
		catch (IOException ignored) {
		}
	}

	/**
	 * 保存到 {@code attach/yyyy/MM/uuid.ext}（与 PHP AttachService 分片路径类型 2 一致）。
	 */
	public static String saveMultipartAsAttachYm(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		String orig = file.getOriginalFilename();
		String ext = FileUtil.extName(orig);
		java.time.LocalDate d = java.time.LocalDate.now();
		String sub = d.getYear() + "/" + String.format("%02d", d.getMonthValue());
		String name = UUID.randomUUID().toString().replace("-", "") + (StrUtil.isBlank(ext) ? "" : "." + ext);
		Path dir = uploadRoot().resolve("attach").resolve(Path.of(sub));
		Files.createDirectories(dir);
		Path target = dir.resolve(name);
		file.transferTo(target.toFile());
		return "attach/" + sub + "/" + name;
	}

	/**
	 * 保存到 {@code source/yyyy/MM/uuid.ext}（与 PHP fileUpload {@code source} 路径一致）。
	 */
	public static String saveMultipartAsSourceYm(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		String orig = file.getOriginalFilename();
		String ext = FileUtil.extName(orig);
		java.time.LocalDate d = java.time.LocalDate.now();
		String sub = d.getYear() + "/" + String.format("%02d", d.getMonthValue());
		String name = UUID.randomUUID().toString().replace("-", "") + (StrUtil.isBlank(ext) ? "" : "." + ext);
		Path dir = uploadRoot().resolve("source").resolve(Path.of(sub));
		Files.createDirectories(dir);
		Path target = dir.resolve(name);
		file.transferTo(target.toFile());
		return "source/" + sub + "/" + name;
	}

}
