package com.bubblecloud.biz.oa.controller;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.biz.oa.service.SystemAttachAdminService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.AttachSaveDTO;
import com.bubblecloud.oa.api.entity.SystemAttach;
import com.bubblecloud.oa.api.vo.AttachFileSrcVO;
import com.bubblecloud.oa.api.vo.AttachInfoVO;
import com.bubblecloud.oa.api.vo.AttachUploadResultVO;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 系统附件（对齐 PHP {@code ent/system/attach}）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/attach")
@Tag(name = "系统附件")
public class SystemAttachController {

	private final SystemAttachAdminService systemAttachAdminService;

	private static int entidOr1(Integer v) {
		return ObjectUtil.defaultIfNull(v, 1);
	}

	private static String uidStr() {
		Long uid = OaSecurityUtil.currentUserId();
		return uid == null ? "" : String.valueOf(uid);
	}

	@GetMapping("/list")
	@Operation(summary = "素材库列表")
	public R<ListCountVO<SystemAttach>> list(@RequestParam(required = false) Integer cid,
			@RequestParam(required = false) Integer pid, @RequestParam(defaultValue = "2") Integer way,
			@RequestParam(required = false) Integer upType, @RequestParam(required = false) String realName,
			@RequestParam(defaultValue = "1") Integer entid, @RequestParam(required = false) List<String> fileExt,
			@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "20") Integer limit) {
		return R.phpOk(systemAttachAdminService.imageList(entidOr1(entid), ObjectUtil.defaultIfNull(way, 2), cid, pid,
				upType, realName, fileExt, ObjectUtil.defaultIfNull(page, 1), ObjectUtil.defaultIfNull(limit, 20)));
	}

	@PostMapping("/save")
	@Operation(summary = "新建附件记录（云存储回调后落库）")
	public R<AttachUploadResultVO> save(@RequestBody AttachSaveDTO dto,
			@RequestParam(defaultValue = "1") Integer entid) {
		return R.phpOk(systemAttachAdminService.saveRecord(dto, entidOr1(entid), uidStr()));
	}

	@DeleteMapping("/delete")
	@Operation(summary = "删除附件")
	public R<String> delete(@RequestBody(required = false) JsonNode body,
			@RequestParam(defaultValue = "1") Integer entid) {
		systemAttachAdminService.deleteByIdsBody(body, entidOr1(entid));
		return R.phpOk("删除成功");
	}

	@PostMapping(value = { "/upload", "/upload/{attachType}", "/upload/{attachType}/{type}" },
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "本地上传图片")
	public R<Object> upload(@PathVariable(required = false) Integer attachType,
			@PathVariable(required = false) Integer type, @RequestParam(defaultValue = "0") int cid,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(defaultValue = "2") int way, @RequestParam(required = false) String relationType,
			@RequestParam(defaultValue = "0") int relationId, @RequestParam(required = false) String eid,
			@RequestParam(defaultValue = "1") Integer entid,
			@RequestParam(value = "chunk_index", required = false) Integer chunkIndex,
			@RequestParam(value = "chunk_total", required = false) Integer chunkTotal,
			@RequestParam(required = false) String md5) throws Exception {
		int at = attachType == null ? 1 : attachType;
		int tp = type == null ? 0 : type;
		Object data = systemAttachAdminService.uploadMultipart(entidOr1(entid), cid, way, relationType, relationId, eid,
				uidStr(), at, tp, file, chunkIndex, chunkTotal, md5);
		boolean emptyChunk = data instanceof List && ((List<?>) data).isEmpty();
		return emptyChunk ? R.phpOk(Collections.emptyList()) : R.phpOk(data);
	}

	@PutMapping("/move")
	@Operation(summary = "移动附件分类")
	public R<String> move(@RequestBody JsonNode body, @RequestParam(defaultValue = "1") Integer entid) {
		int cid = body.path("cid").asInt(0);
		String images = body.path("images").asText("");
		systemAttachAdminService.moveToCategory(entidOr1(entid), cid, images);
		return R.phpOk("移动成功");
	}

	@PutMapping("/update/{attachId}")
	@Operation(summary = "修改附件名称（旧接口）")
	public R<String> updateLegacy(@PathVariable int attachId, @RequestBody JsonNode body,
			@RequestParam(defaultValue = "1") Integer entid) {
		String realName = body.path("real_name").asText("");
		systemAttachAdminService.updateRealNameLegacy(attachId, entidOr1(entid), realName);
		return R.phpOk("common.update.succ");
	}

	@PostMapping(value = { "/file/{attachType}" }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "附件文件上传（返回 src）")
	public R<AttachFileSrcVO> fileUpload(@PathVariable(required = false) Integer attachType,
			@RequestParam("file") MultipartFile file) throws Exception {
		int at = attachType == null ? 1 : attachType;
		return R.phpOk(systemAttachAdminService.fileUpload(file, at));
	}

	@GetMapping("/cover")
	@Operation(summary = "考核封面图列表")
	public R<ListCountVO<SystemAttach>> coverList(@RequestParam(defaultValue = "1") Integer entid,
			@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "20") Integer limit) {
		return R.phpOk(systemAttachAdminService.assessCoverList(entidOr1(entid), ObjectUtil.defaultIfNull(page, 1),
				ObjectUtil.defaultIfNull(limit, 20)));
	}

	@PostMapping(value = { "/cover/{attachType}", "/cover/{attachType}/{type}" },
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "考核封面上传")
	public R<Object> uploadCover(@PathVariable(required = false) Integer attachType,
			@PathVariable(required = false) Integer type, @RequestParam("file") MultipartFile file,
			@RequestParam(defaultValue = "1") Integer entid,
			@RequestParam(value = "chunk_index", required = false) Integer chunkIndex,
			@RequestParam(value = "chunk_total", required = false) Integer chunkTotal,
			@RequestParam(required = false) String md5) throws Exception {
		int at = attachType == null ? 1 : attachType;
		int tp = type == null ? 0 : type;
		Object data = systemAttachAdminService.uploadCover(entidOr1(entid), uidStr(), at, tp, file, chunkIndex,
				chunkTotal, md5);
		boolean emptyChunk = data instanceof List && ((List<?>) data).isEmpty();
		return emptyChunk ? R.phpOk(Collections.emptyList()) : R.phpOk(data);
	}

	@DeleteMapping("/cover")
	@Operation(summary = "删除考核封面")
	public R<String> deleteCover(@RequestBody(required = false) JsonNode body,
			@RequestParam(defaultValue = "1") Integer entid) {
		systemAttachAdminService.deleteCoverBody(body, entidOr1(entid), uidStr());
		return R.phpOk("删除成功");
	}

	@PutMapping("/real_name/{id}")
	@Operation(summary = "附件重命名")
	public R<String> realName(@PathVariable Integer id, @RequestBody JsonNode body,
			@RequestParam(defaultValue = "1") Integer entid) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		String realName = body.path("real_name").asText("");
		systemAttachAdminService.realNamePut(id, entidOr1(entid), realName);
		return R.phpOk("common.operation.succ");
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "附件详情")
	public R<AttachInfoVO> info(@PathVariable Integer id) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		return R.phpOk(systemAttachAdminService.getAttachInfo(id));
	}

	@PutMapping(value = "/info/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "附件内容更新（PHP 含 Word 转换；Java 明确不支持在线编辑）")
	public R<String> updateInfo(@PathVariable Integer id,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "is_file", defaultValue = "0") int isFile) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		if (StrUtil.isNotBlank(content) || isFile == 1) {
			return R.phpFailed("附件在线编辑迁移中，请使用本地上传替换");
		}
		return R.phpOk("common.operation.succ");
	}

}
