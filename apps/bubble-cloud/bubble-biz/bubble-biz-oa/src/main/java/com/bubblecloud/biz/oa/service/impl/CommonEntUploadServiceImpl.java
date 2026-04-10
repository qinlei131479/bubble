package com.bubblecloud.biz.oa.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.bubblecloud.biz.oa.mapper.SystemAttachMapper;
import com.bubblecloud.biz.oa.service.CommonEntUploadService;
import com.bubblecloud.biz.oa.service.OaDownloadSigningService;
import com.bubblecloud.biz.oa.service.SystemConfigService;
import com.bubblecloud.oa.api.dto.common.OaDownloadPayloadDTO;
import com.bubblecloud.oa.api.entity.SystemAttach;
import com.bubblecloud.oa.api.vo.common.CommonDownloadUrlVO;
import com.bubblecloud.oa.api.vo.common.CommonUploadResultVO;
import com.bubblecloud.oa.api.vo.common.UploadTempKeysVO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * {@link CommonEntUploadService} 实现：本地上传 + 分片合并 + HMAC 下载链接。
 *
 * @author qinlei
 * @date 2026/4/4 18:00
 */
@Service
@RequiredArgsConstructor
public class CommonEntUploadServiceImpl implements CommonEntUploadService {

	private static final Map<String, Integer> RELATION_TYPE = new HashMap<>();

	static {
		RELATION_TYPE.put("", 0);
		RELATION_TYPE.put("daily", 1);
		RELATION_TYPE.put("bill", 2);
		RELATION_TYPE.put("contract", 3);
		RELATION_TYPE.put("client", 4);
		RELATION_TYPE.put("follow", 5);
		RELATION_TYPE.put("invoice", 6);
		RELATION_TYPE.put("attendance_clock", 7);
		RELATION_TYPE.put("liaison", 8);
		RELATION_TYPE.put("program", 9);
		RELATION_TYPE.put("finance", 10);
	}

	private static final String API_PREFIX = "api/ent";

	private final SystemConfigService systemConfigService;

	private final SystemAttachMapper systemAttachMapper;

	private final OaDownloadSigningService oaDownloadSigningService;

	private final StringRedisTemplate stringRedisTemplate;

	private static Path uploadRoot() {
		String base = System.getProperty("oa.upload.dir", System.getProperty("user.dir") + "/data/oa-upload");
		return Path.of(base);
	}

	private static long secondsUntilEndOfDay() {
		LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
		return Math.max(60L, ChronoUnit.SECONDS.between(LocalDateTime.now(), end));
	}

	private int parseUploadType() {
		String v = systemConfigService.getConfigRawValue("upload_type");
		if (StrUtil.isBlank(v)) {
			return 1;
		}
		try {
			return Integer.parseInt(v.trim());
		}
		catch (NumberFormatException e) {
			return 1;
		}
	}

	@Override
	public UploadTempKeysVO getTempKeys(String key, String path, String contentType) {
		int t = parseUploadType();
		String site = StrUtil.removeSuffix(systemConfigService.getConfigRawValue("site_url"), "/");
		if (StrUtil.isBlank(site)) {
			site = "";
		}
		if (t == 1) {
			return new UploadTempKeysVO(site, "local", site);
		}
		if (t == 5) {
			if (StrUtil.hasBlank(key, contentType)) {
				throw new IllegalArgumentException("缺少参数");
			}
			throw new IllegalArgumentException("京东云直传（upload_type=5）尚未在 Java 端实现，请改用本地上传");
		}
		if (t == 2 || t == 3 || t == 4 || t == 6 || t == 7) {
			throw new IllegalArgumentException("当前 Java 端仅完整支持本地上传（upload_type=1），请先在系统配置中切换为本地存储");
		}
		throw new IllegalArgumentException("您已关闭上传功能或上传类型未支持");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public CommonUploadResultVO upload(MultipartFile file, int cid, String relationType, Integer relationId, int way,
			Integer eid, String md5, Integer chunkIndex, Integer chunkTotal, int entid, String uid, String clientIp) {
		try {
			if (file == null || file.isEmpty()) {
				throw new IllegalArgumentException("上传的文件不存在");
			}
			int uploadType = parseUploadType();
			if (uploadType != 1) {
				throw new IllegalArgumentException("仅支持本地上传（upload_type=1）");
			}
			checkUploadRateLimit(clientIp);
			int relType = RELATION_TYPE.getOrDefault(StrUtil.nullToDefault(relationType, "").trim(), 0);
			int relId = ObjectUtil.defaultIfNull(relationId, 0);
			if (relId < 1 && ObjectUtil.isNotNull(eid) && eid > 0 && relType == 4) {
				relId = eid;
			}
			int ct = ObjectUtil.defaultIfNull(chunkTotal, 0);
			if (ct > 1) {
				boolean done = handleChunk(file, md5, ObjectUtil.defaultIfNull(chunkIndex, 0), ct);
				if (!done) {
					return null;
				}
				return finishChunkedFile(file, cid, way, entid, uid, relType, relId, uploadType, md5, ct);
			}
			return saveWholeFile(file, cid, way, entid, uid, relType, relId, uploadType);
		}
		catch (IOException e) {
			throw new IllegalArgumentException(StrUtil.blankToDefault(e.getMessage(), "上传失败"));
		}
	}

	private void checkUploadRateLimit(String clientIp) {
		String ip = StrUtil.blankToDefault(clientIp, "unknown");
		String k = "oa:upload:daily:" + ip;
		Long c = stringRedisTemplate.opsForValue().increment(k);
		if (Objects.equals(c, 1L)) {
			stringRedisTemplate.expire(k, secondsUntilEndOfDay(), TimeUnit.SECONDS);
		}
		if (c != null && c > 500) {
			throw new IllegalArgumentException("您今日上传文件的次数已达上限");
		}
	}

	private boolean handleChunk(MultipartFile file, String md5, int chunkIndex, int chunkTotal) throws IOException {
		String orig = file.getOriginalFilename();
		String safeOrig = sanitizeBaseName(orig);
		String folderKey = StrUtil.blankToDefault(md5, DigestUtil.sha256Hex(safeOrig + "_" + orig));
		Path chunkDir = uploadRoot().resolve("uploads").resolve("chunks").resolve(sanitizePathToken(folderKey));
		Files.createDirectories(chunkDir);
		Path part = chunkDir.resolve(safeOrig + "__" + chunkIndex);
		file.transferTo(part.toFile());
		try (Stream<Path> stream = Files.list(chunkDir)) {
			long n = stream.filter(p -> {
				String name = p.getFileName().toString();
				return name.startsWith(safeOrig + "__") && Files.isRegularFile(p);
			}).count();
			return n >= chunkTotal;
		}
	}

	private CommonUploadResultVO finishChunkedFile(MultipartFile file, int cid, int way, int entid, String uid,
			int relType, int relId, int uploadType, String md5, int chunkTotal) throws IOException {
		String orig = file.getOriginalFilename();
		String safeOrig = sanitizeBaseName(orig);
		String folderKey = StrUtil.blankToDefault(md5, DigestUtil.sha256Hex(safeOrig + "_" + orig));
		Path chunkDir = uploadRoot().resolve("uploads").resolve("chunks").resolve(sanitizePathToken(folderKey));
		LocalDate d = LocalDate.now();
		Path dayDir = uploadRoot().resolve("uploads")
			.resolve("attach")
			.resolve(String.valueOf(d.getYear()))
			.resolve(String.format("%02d", d.getMonthValue()))
			.resolve(String.format("%02d", d.getDayOfMonth()));
		Files.createDirectories(dayDir);
		String ext = FileUtil.extName(orig);
		String finalName = safeOrig + (StrUtil.isBlank(ext) ? "" : "." + ext);
		Path target = uniqueFile(dayDir, finalName);
		try (OutputStream out = Files.newOutputStream(target)) {
			for (int i = 0; i < chunkTotal; i++) {
				Path p = chunkDir.resolve(safeOrig + "__" + i);
				if (!Files.isRegularFile(p)) {
					Files.deleteIfExists(target);
					throw new IllegalArgumentException("分片不完整");
				}
				Files.copy(p, out);
				Files.delete(p);
			}
		}
		if (StrUtil.isNotBlank(md5)) {
			String hex = DigestUtil.md5Hex(target.toFile());
			if (!md5.equalsIgnoreCase(hex)) {
				Files.deleteIfExists(target);
				throw new IllegalArgumentException("校验文件失败");
			}
		}
		try {
			Files.deleteIfExists(chunkDir);
		}
		catch (IOException ignored) {
		}
		String relative = "/uploads/attach/" + d.getYear() + "/" + String.format("%02d", d.getMonthValue()) + "/"
				+ String.format("%02d", d.getDayOfMonth()) + "/" + target.getFileName();
		return insertAttachAndVo(target, orig, relative, cid, way, entid, uid, relType, relId, uploadType,
				Files.size(target));
	}

	private CommonUploadResultVO saveWholeFile(MultipartFile file, int cid, int way, int entid, String uid, int relType,
			int relId, int uploadType) throws IOException {
		String orig = file.getOriginalFilename();
		String ext = FileUtil.extName(orig);
		LocalDate d = LocalDate.now();
		Path dayDir = uploadRoot().resolve("uploads")
			.resolve("attach")
			.resolve(String.valueOf(d.getYear()))
			.resolve(String.format("%02d", d.getMonthValue()))
			.resolve(String.format("%02d", d.getDayOfMonth()));
		Files.createDirectories(dayDir);
		String name = UUID.randomUUID().toString().replace("-", "") + (StrUtil.isBlank(ext) ? "" : "." + ext);
		Path target = dayDir.resolve(name);
		file.transferTo(target.toFile());
		String relative = "/uploads/attach/" + d.getYear() + "/" + String.format("%02d", d.getMonthValue()) + "/"
				+ String.format("%02d", d.getDayOfMonth()) + "/" + name;
		return insertAttachAndVo(target, orig, relative, cid, way, entid, uid, relType, relId, uploadType,
				file.getSize());
	}

	private CommonUploadResultVO insertAttachAndVo(Path storedFile, String origName, String attDir, int cid, int way,
			int entid, String uid, int relType, int relId, int uploadType, long size) {
		String ext = FileUtil.extName(origName);
		String mime = "application/octet-stream";
		try {
			mime = java.nio.file.Files.probeContentType(storedFile);
		}
		catch (IOException ignored) {
		}
		if (StrUtil.isBlank(mime)) {
			mime = "application/octet-stream";
		}
		SystemAttach a = new SystemAttach();
		a.setEntid(entid);
		a.setUid(StrUtil.blankToDefault(uid, ""));
		a.setName(StrUtil.blankToDefault(origName, nameFromPath(storedFile)));
		a.setRealName(StrUtil.blankToDefault(origName, a.getName()));
		a.setAttDir(attDir);
		a.setThumbDir(attDir);
		a.setAttSize(String.valueOf(size));
		a.setAttType(mime);
		a.setFileExt(ext);
		a.setCid(cid);
		a.setUpType(uploadType);
		a.setWay(way);
		a.setRelationType(relType);
		a.setRelationId(relId);
		a.setCreatedAt(LocalDateTime.now());
		a.setUpdatedAt(LocalDateTime.now());
		systemAttachMapper.insert(a);
		CommonUploadResultVO vo = new CommonUploadResultVO();
		vo.setSrc(attDir);
		vo.setUrl(linkFile(attDir));
		vo.setAttachId(a.getId());
		vo.setId(a.getId());
		vo.setSize(String.valueOf(size));
		vo.setName(a.getRealName());
		return vo;
	}

	private static String nameFromPath(Path p) {
		return p.getFileName().toString();
	}

	private String linkFile(String attDir) {
		if (StrUtil.isBlank(attDir)) {
			return "";
		}
		if (attDir.startsWith("http://") || attDir.startsWith("https://")) {
			return attDir;
		}
		String base = StrUtil.removeSuffix(systemConfigService.getConfigRawValue("site_url"), "/");
		if (StrUtil.isBlank(base)) {
			return attDir;
		}
		return base + (attDir.startsWith("/") ? attDir : "/" + attDir);
	}

	private static Path uniqueFile(Path dir, String finalName) {
		Path t = dir.resolve(finalName);
		if (!Files.exists(t)) {
			return t;
		}
		String base = FileUtil.mainName(finalName);
		String ext = FileUtil.extName(finalName);
		String suffix = StrUtil.isBlank(ext) ? "" : "." + ext;
		for (int i = 0; i < 1000; i++) {
			Path c = dir.resolve(base + "_" + i + suffix);
			if (!Files.exists(c)) {
				return c;
			}
		}
		return dir.resolve(base + "_" + UUID.randomUUID() + suffix);
	}

	private static String sanitizeBaseName(String orig) {
		if (StrUtil.isBlank(orig)) {
			return "file";
		}
		String base = FileUtil.mainName(orig);
		String s = StrUtil.removeAll(base, "[\\\\/:*?\"<>|]");
		s = StrUtil.subPre(s, 80);
		return StrUtil.isBlank(s) ? "file" : s;
	}

	private static String sanitizePathToken(String s) {
		String t = StrUtil.removeAll(StrUtil.blankToDefault(s, "x"), "[^a-zA-Z0-9._-]");
		return StrUtil.isBlank(t) ? "chunk" : StrUtil.subPre(t, 64);
	}

	@Override
	public CommonDownloadUrlVO buildDownloadUrl(Integer version, String type, String fileId) {
		String site = StrUtil.removeSuffix(systemConfigService.getConfigRawValue("site_url"), "/");
		if (StrUtil.isBlank(site)) {
			throw new IllegalArgumentException("缺少站点地址 site_url 配置");
		}
		OaDownloadPayloadDTO p = new OaDownloadPayloadDTO();
		if ("apply".equals(type)) {
			p.setType("local");
			p.setUrl("/template/apply.xlsx");
			p.setName("邀请成员表格模板.xlsx");
		}
		else if ("folder".equals(type)) {
			throw new IllegalArgumentException("资源不存在");
		}
		else {
			if (StrUtil.isBlank(fileId)) {
				throw new IllegalArgumentException("缺少FileId");
			}
			p.setType("db");
			p.setFileId(fileId);
			if (ObjectUtil.isNotNull(version) && version > 0) {
				p.setVersion(version);
			}
		}
		String sig = oaDownloadSigningService.sign(p);
		String enc = URLEncoder.encode(sig, StandardCharsets.UTF_8);
		String u = site + "/" + API_PREFIX + "/common/download?signature=" + enc;
		return new CommonDownloadUrlVO(u);
	}

	@Override
	public ResponseEntity<Resource> download(String signature) {
		try {
			OaDownloadPayloadDTO p = oaDownloadSigningService.verify(signature);
			String t = StrUtil.blankToDefault(p.getType(), "db");
			if ("local".equals(t)) {
				return downloadLocal(p);
			}
			if ("db".equals(t)) {
				return downloadDb(p);
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private ResponseEntity<Resource> downloadLocal(OaDownloadPayloadDTO p) {
		String u = StrUtil.blankToDefault(p.getUrl(), "");
		if (StrUtil.isBlank(u)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		Path base = uploadRoot().normalize();
		String rel = u.replaceFirst("^/+", "");
		Path file = base.resolve(rel).normalize();
		if (!file.startsWith(base) || !Files.isRegularFile(file)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return toAttachment(file, StrUtil.blankToDefault(p.getName(), file.getFileName().toString()));
	}

	private ResponseEntity<Resource> downloadDb(OaDownloadPayloadDTO p) {
		String fid = p.getFileId();
		if (StrUtil.isBlank(fid)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		int id;
		try {
			id = Integer.parseInt(fid.trim());
		}
		catch (NumberFormatException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		SystemAttach row = systemAttachMapper.selectById(id);
		if (row == null || StrUtil.isBlank(row.getAttDir())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Path file = resolveAttachPath(row.getAttDir());
		if (file == null || !Files.isRegularFile(file)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		String dn = StrUtil.blankToDefault(row.getRealName(), row.getName());
		if (StrUtil.isBlank(dn)) {
			dn = file.getFileName().toString();
		}
		dn = dn.replace('/', '_').replace('\\', '_');
		return toAttachment(file, dn);
	}

	private Path resolveAttachPath(String attDir) {
		Path base = uploadRoot().normalize();
		String rel = attDir.replaceFirst("^/+", "");
		Path[] tries = new Path[] { base.resolve(rel), base.resolve("uploads").resolve(rel) };
		for (Path c : tries) {
			Path n = c.normalize();
			if (!n.startsWith(base)) {
				continue;
			}
			if (Files.isRegularFile(n)) {
				return n;
			}
		}
		return null;
	}

	private ResponseEntity<Resource> toAttachment(Path file, String downloadName) {
		try {
			InputStream in = Files.newInputStream(file);
			InputStreamResource body = new InputStreamResource(in);
			return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadName + "\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(body);
		}
		catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
