package com.bubblecloud.biz.oa.controller;

import java.util.Map;

import com.bubblecloud.biz.oa.service.CrmClientFileService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.SystemAttach;
import com.bubblecloud.oa.api.vo.ListCountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
import org.springframework.http.MediaType;
import cn.hutool.core.util.ObjectUtil;

/**
 * 客户文件（对齐 PHP {@code ent/client/file}）。
 *
 * @author qinlei
 * @date 2026/4/3 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/client/file")
@Tag(name = "CRM客户文件")
public class CrmClientFileController {

	private final CrmClientFileService crmClientFileService;

	private static int entidOr1(Integer v) {
		return ObjectUtil.defaultIfNull(v, 1);
	}

	@GetMapping("/index")
	@Operation(summary = "客户文件列表")
	public R<ListCountVO<SystemAttach>> index(@ParameterObject Pg<SystemAttach> pg,
			@RequestParam(required = false) Integer eid, @RequestParam(defaultValue = "1") Integer entid) {
		if (pg.getSize() <= 0) {
			pg.setSize(20);
		}
		return R.phpOk(crmClientFileService.index(entidOr1(entid), eid, pg));
	}

	@DeleteMapping("/delete/{id}")
	@Operation(summary = "删除客户文件")
	public R<String> delete(@PathVariable int id, @RequestParam(defaultValue = "1") Integer entid) {
		crmClientFileService.deleteAttach(entidOr1(entid), id);
		return R.phpOk("common.delete.succ");
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "上传客户文件")
	public R<Map<String, Object>> upload(@RequestParam(defaultValue = "0") int cid,
			@RequestParam(defaultValue = "0") int eid, @RequestParam(defaultValue = "0") int fid,
			@RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "1") Integer entid)
			throws Exception {
		Long uid = OaSecurityUtil.currentUserId();
		String uidStr = uid == null ? "" : String.valueOf(uid);
		Map<String, Object> data = crmClientFileService.upload(entidOr1(entid), cid, eid, fid, file, uidStr);
		return R.phpOk(data);
	}

	@PutMapping("/real_name/{id}")
	@Operation(summary = "客户文件重命名")
	public R<String> realName(@PathVariable int id, @RequestParam(defaultValue = "1") Integer entid,
			@RequestBody Map<String, String> body) {
		String realName = body == null ? "" : body.getOrDefault("real_name", "");
		crmClientFileService.setRealName(entidOr1(entid), id, realName);
		return R.phpOk("common.operation.succ");
	}

}
