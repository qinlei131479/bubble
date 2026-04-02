package com.bubblecloud.biz.oa.controller;

import java.util.Map;

import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.biz.oa.service.FormAdminService;
import com.bubblecloud.common.core.util.R;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 自定义表单（对齐 PHP {@code ent/config/form}）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/config/form")
@Tag(name = "自定义表单配置")
public class FormAdminController {

	private final FormAdminService formAdminService;

	@GetMapping("/cate")
	@Operation(summary = "表单分组与字段列表")
	public R<?> list(@RequestParam String types) {
		if (StrUtil.isBlank(types)) {
			return R.phpFailed("common.empty.attrs");
		}
		try {
			return R.phpOk(formAdminService.listByTypes(Integer.parseInt(types.trim())));
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PostMapping("/cate/{types}")
	@Operation(summary = "新增分组")
	public R<Map<String, Long>> create(@PathVariable Integer types, @RequestBody JsonNode body) {
		try {
			String title = text(body, "title");
			Integer sort = intOrNull(body, "sort");
			int status = body.has("status") ? body.get("status").asInt(1) : 1;
			long id = formAdminService.saveCate(types, title, sort, status);
			return R.phpOk(Map.of("id", id));
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PutMapping("/cate/{id}")
	@Operation(summary = "修改分组")
	public R<String> update(@PathVariable Long id, @RequestBody JsonNode body) {
		try {
			String title = text(body, "title");
			Integer sort = intOrNull(body, "sort");
			int status = body.has("status") ? body.get("status").asInt(1) : 1;
			formAdminService.updateCate(id, title, sort, status);
			return R.phpOk("common.operation.succ");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@DeleteMapping("/cate/{id}")
	@Operation(summary = "删除分组")
	public R<String> removeById(@PathVariable Long id) {
		try {
			formAdminService.deleteCate(id);
			return R.phpOk("common.delete.succ");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@GetMapping("/cate/{id}")
	@Operation(summary = "修改分组显示状态（PHP 的 show）")
	public R<String> show(@PathVariable Long id, @RequestParam Integer status) {
		try {
			formAdminService.updateCateStatus(id, status);
			return R.phpOk("common.operation.succ");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PostMapping("/data/{types}")
	@Operation(summary = "保存表单字段")
	public R<String> createData(@PathVariable Integer types, @RequestBody JsonNode body) {
		try {
			JsonNode data = body.get("data");
			if (ObjectUtil.isNull(data)) {
				data = body;
			}
			formAdminService.saveFormData(types, data);
			return R.phpOk("common.operation.succ");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PutMapping("/data/move/{types}")
	@Operation(summary = "字段移动分组")
	public R<String> move(@PathVariable Integer types, @RequestBody JsonNode body) {
		try {
			long id = body.has("id") ? body.get("id").asLong() : 0L;
			int cateId = body.has("cate_id") ? body.get("cate_id").asInt() : 0;
			formAdminService.moveFormData(types, id, cateId);
			return R.phpOk("common.operation.succ");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@GetMapping("/data/fields/{customType}")
	@Operation(summary = "业务员自定义字段列表")
	public R<JsonNode> getSalesmanCustom(@PathVariable Integer customType) {
		Long uid = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(uid)) {
			return R.phpFailed("未登录");
		}
		return R.phpOk(formAdminService.getSalesmanCustomFields(uid, customType));
	}

	@PutMapping("/data/fields/{customType}")
	@Operation(summary = "保存业务员自定义字段")
	public R<String> saveSalesmanCustom(@PathVariable Integer customType, @RequestBody JsonNode body) {
		Long uid = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(uid)) {
			return R.phpFailed("未登录");
		}
		try {
			String selectType = body.has("select_type") ? body.get("select_type").asText() : "";
			JsonNode data = body.get("data");
			formAdminService.saveSalesmanCustomFields(uid, customType, selectType, data);
			return R.phpOk(OaConstants.UPDATE_SUCC);
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	private static String text(JsonNode n, String field) {
		if (ObjectUtil.isNull(n) || !n.has(field) || n.get(field).isNull()) {
			return "";
		}
		return n.get(field).asText("");
	}

	private static Integer intOrNull(JsonNode n, String field) {
		if (ObjectUtil.isNull(n) || !n.has(field) || n.get(field).isNull()) {
			return null;
		}
		return n.get(field).asInt();
	}

}
