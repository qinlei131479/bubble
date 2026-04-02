package com.bubblecloud.biz.oa.controller;

import java.util.Map;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.FormCategoryService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.biz.oa.service.FormDataService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.FormCategory;
import com.bubblecloud.oa.api.entity.FormData;
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
public class FormDataController {

	private final FormDataService formDataService;

	private final FormCategoryService formCategoryService;

	@GetMapping("/cate")
	@Operation(summary = "表单分组与字段列表")
	public R<?> list(@RequestParam String types) {
		return R.phpOk(formDataService.listByTypes(Integer.parseInt(types)));
	}

	@PostMapping("/cate/{types}")
	@Operation(summary = "新增分组")
	public R<Map<String, Long>> create(@PathVariable Integer types, @RequestBody JsonNode body) {
		FormCategory req = new FormCategory();
		req.setTypes(types);
		req.setTitle(text(body, "title"));
		req.setSort(intOrNull(body, "sort"));
		req.setStatus(body.has("status") ? body.get("status").asInt(1) : 1);
		formCategoryService.create(req);
		return R.phpOk(Map.of("id", 0L));
	}

	@PutMapping("/cate/{id}")
	@Operation(summary = "修改分组")
	public R<String> update(@PathVariable Long id, @RequestBody JsonNode body) {
		FormCategory req = new FormCategory();
		req.setId(id);
		req.setTitle(text(body, "title"));
		req.setSort(intOrNull(body, "sort"));
		req.setStatus(body.has("status") ? body.get("status").asInt(1) : 1);
		formCategoryService.update(req);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@DeleteMapping("/cate/{id}")
	@Operation(summary = "删除分组")
	public R<String> removeById(@PathVariable Long id) {
		formDataService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/cate/{id}")
	@Operation(summary = "修改分组显示状态（PHP 的 show）")
	public R<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
		formDataService.updateStatus(id, status);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PostMapping("/data/{types}")
	@Operation(summary = "保存表单字段")
	public R<String> createData(@PathVariable Integer types, @RequestBody JsonNode body) {
		try {
			JsonNode data = body.get("data");
			if (ObjectUtil.isNull(data)) {
				data = body;
			}
			formDataService.saveFormData(types, data);
			return R.phpOk(OaConstants.OPT_SUCC);
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PutMapping("/data/move/{types}")
	@Operation(summary = "字段移动分组")
	public R<String> move(@PathVariable Integer types, @RequestBody JsonNode body) {
		try {
			Long id = body.has("id") ? body.get("id").asLong() : 0L;
			Long cateId = body.has("cate_id") ? body.get("cate_id").asLong() : 0;
			formDataService.moveFormData(types, id, cateId);
			return R.phpOk(OaConstants.OPT_SUCC);
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@GetMapping("/data/fields/{customType}")
	@Operation(summary = "业务员自定义字段列表")
	public R<JsonNode> getSalesmanCustom(@PathVariable Integer customType) {
		Long uid = OaSecurityUtil.currentUserId();
		return R.phpOk(formDataService.getSalesmanCustomFields(uid, customType));
	}

	@PutMapping("/data/fields/{customType}")
	@Operation(summary = "保存业务员自定义字段")
	public R<String> saveSalesmanCustom(@PathVariable Integer customType, @RequestBody JsonNode body) {
		Long uid = OaSecurityUtil.currentUserId();
		String selectType = body.has("select_type") ? body.get("select_type").asText() : "";
		JsonNode data = body.get("data");
		formDataService.saveSalesmanCustomFields(uid, customType, selectType, data);
		return R.phpOk(OaConstants.UPDATE_SUCC);
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
