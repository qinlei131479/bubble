package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.CommonEntUploadService;
import com.bubblecloud.biz.oa.service.CommonService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.dto.InitDataQueryDTO;
import com.bubblecloud.oa.api.dto.SmsVerifySendDTO;
import com.bubblecloud.oa.api.vo.CaptchaVO;
import com.bubblecloud.oa.api.vo.ConfigVO;
import com.bubblecloud.oa.api.vo.SiteVO;
import com.bubblecloud.oa.api.vo.SmsVerifyKeyVO;
import com.bubblecloud.oa.api.vo.common.CityTreeNodeVO;
import com.bubblecloud.oa.api.vo.common.CommonAuthVO;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;
import com.bubblecloud.oa.api.vo.common.CommonSiteAddressVO;
import com.bubblecloud.oa.api.vo.common.CommonDownloadUrlVO;
import com.bubblecloud.oa.api.vo.common.CommonUploadResultVO;
import com.bubblecloud.oa.api.vo.common.CommonVersionVO;
import com.bubblecloud.oa.api.vo.common.InitDataUrlVO;
import com.bubblecloud.oa.api.vo.common.UploadTempKeysVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

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

	private final CommonEntUploadService commonEntUploadService;

	private final AdminService adminService;

	@GetMapping("/captcha")
	@Operation(summary = "图形验证码（Redis 缓存）")
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
	 * 商业授权（PHP common/auth；前端工作台 entAuth 仅 status===-1 时弹窗）。
	 */
	@GetMapping("/auth")
	@Operation(summary = "授权信息")
	public R<CommonAuthVO> auth() {
		return R.phpOk(commonService.auth());
	}

	@GetMapping("/logout")
	@Operation(summary = "退出登录（清除当前安全上下文）")
	public R<String> logout() {
		commonService.logoutSession();
		return R.phpOk("退出成功");
	}

	@GetMapping("/site_address")
	@Operation(summary = "获取站点网址与默认展示信息")
	public R<CommonSiteAddressVO> siteAddress() {
		return R.phpOk(commonService.siteAddress());
	}

	@PostMapping("/initData")
	@Operation(summary = "获取默认数据包路径")
	public R<InitDataUrlVO> initData(@RequestBody(required = false) InitDataQueryDTO body) {
		String version = body != null && body.getVersion() != null ? body.getVersion() : "";
		return R.phpOk(commonService.initData(version));
	}

	@GetMapping("/city")
	@Operation(summary = "省市区树形数据")
	public R<List<CityTreeNodeVO>> city() {
		return R.phpOk(commonService.cityTree());
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
	public R<CommonMessageVO> message(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer limit, @RequestParam(required = false) String cate_id,
			@RequestParam(required = false) String title) {
		return R.phpOk(commonService.messageList(page, limit, cate_id, title));
	}

	@PutMapping("/message/{id}/{isRead}")
	@Operation(summary = "修改消息已读状态")
	public R<String> updateMessage(@PathVariable Long id, @PathVariable Integer isRead) {
		commonService.updateMessageRead(id, isRead);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@GetMapping("/upload_key")
	@Operation(summary = "获取上传 token/站点信息")
	public R<UploadTempKeysVO> uploadKey(@RequestParam(required = false) String key,
			@RequestParam(required = false) String path, @RequestParam(required = false) String contentType) {
		try {
			return R.phpOk(commonEntUploadService.getTempKeys(key, path, contentType));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(StrUtil.blankToDefault(e.getMessage(), "获取失败"));
		}
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "上传文件（本地存储，支持分片）")
	public R<?> upload(@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "cid", defaultValue = "0") Integer cid,
			@RequestParam(value = "relation_type", required = false) String relationType,
			@RequestParam(value = "relation_id", defaultValue = "0") Integer relationId,
			@RequestParam(value = "way", defaultValue = "2") Integer way,
			@RequestParam(value = "eid", required = false) String eid,
			@RequestParam(value = "md5", required = false) String md5,
			@RequestParam(value = "chunk_index", required = false) Integer chunkIndex,
			@RequestParam(value = "chunk_total", required = false) Integer chunkTotal,
			@RequestParam(value = "entid", defaultValue = "1") Integer entid, HttpServletRequest request) {
		Long adminId = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(adminId)) {
			return R.phpFailed("未登录");
		}
		var admin = adminService.getById(adminId);
		if (ObjectUtil.isNull(admin)) {
			return R.phpFailed("用户不存在");
		}
		String uid = StrUtil.blankToDefault(admin.getUid(), "");
		Integer eidInt = null;
		if (StrUtil.isNotBlank(eid)) {
			try {
				eidInt = Integer.parseInt(eid.trim());
			}
			catch (NumberFormatException ignored) {
				eidInt = null;
			}
		}
		try {
			CommonUploadResultVO vo = commonEntUploadService.upload(file, ObjectUtil.defaultIfNull(cid, 0),
					relationType, relationId, ObjectUtil.defaultIfNull(way, 2), eidInt, md5, chunkIndex, chunkTotal,
					ObjectUtil.defaultIfNull(entid, 1), uid, request.getRemoteAddr());
			if (ObjectUtil.isNull(vo)) {
				return R.phpOk("ok");
			}
			return R.phpOk(vo);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(StrUtil.blankToDefault(e.getMessage(), "上传失败"));
		}
	}

	@GetMapping("/download_url")
	@Operation(summary = "获取带签名的下载地址")
	public R<CommonDownloadUrlVO> downloadUrl(@RequestParam(value = "version", required = false) Integer version,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "file_id", required = false) String fileId) {
		try {
			return R.phpOk(commonEntUploadService.buildDownloadUrl(version, type, fileId));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(StrUtil.blankToDefault(e.getMessage(), "获取失败"));
		}
	}

	@RequestMapping(value = "/download", method = { RequestMethod.GET, RequestMethod.POST })
	@Operation(summary = "签名下载（无需登录）")
	public ResponseEntity<Resource> download(@RequestParam("signature") String signature) {
		return commonEntUploadService.download(signature);
	}

}
