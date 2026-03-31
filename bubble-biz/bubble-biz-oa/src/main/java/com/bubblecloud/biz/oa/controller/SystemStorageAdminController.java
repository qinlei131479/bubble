package com.bubblecloud.biz.oa.controller;

import java.util.Map;

import com.bubblecloud.biz.oa.service.SystemStorageAdminService;
import com.bubblecloud.biz.oa.support.PhpResponse;
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

/**
 * 云存储（对齐 PHP {@code ent/config/storage}，无真实云 SDK）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/config/storage")
@Tag(name = "云存储配置")
public class SystemStorageAdminController {

	private final SystemStorageAdminService systemStorageAdminService;

	@GetMapping("/index")
	@Operation(summary = "云存储列表")
	public PhpResponse<?> index(@RequestParam(required = false) Integer type) {
		return PhpResponse.ok(systemStorageAdminService.list(type));
	}

	@GetMapping("/create/{type}")
	@Operation(summary = "创建表单占位")
	public PhpResponse<String> create(@PathVariable int type) {
		return PhpResponse.ok("ok");
	}

	@GetMapping("/form/{type}")
	@Operation(summary = "配置表单占位")
	public PhpResponse<String> form(@PathVariable int type) {
		return PhpResponse.ok("ok");
	}

	@GetMapping("/config")
	@Operation(summary = "当前上传方式")
	public PhpResponse<Map<String, Integer>> getConfig() {
		return PhpResponse.ok(Map.of("type", systemStorageAdminService.getUploadType()));
	}

	@PostMapping("/config")
	@Operation(summary = "保存 access 等（占位写入扩展表）")
	public PhpResponse<String> saveConfig(@RequestBody JsonNode body) {
		return PhpResponse.ok("保存成功");
	}

	@GetMapping("/sync/{type}")
	@Operation(summary = "同步云存储（占位）")
	public PhpResponse<String> sync(@PathVariable int type) {
		return PhpResponse.ok("同步成功");
	}

	@PostMapping("/{type}")
	@Operation(summary = "保存云存储")
	public PhpResponse<String> save(@PathVariable int type, @RequestBody JsonNode body) {
		String accessKey = text(body, "accessKey");
		String name = text(body, "name");
		String region = text(body, "region");
		String acl = text(body, "acl");
		systemStorageAdminService.saveStorage(type, accessKey, name, region, acl);
		return PhpResponse.ok("添加成功");
	}

	@PutMapping("/status/{id}")
	@Operation(summary = "启用该存储")
	public PhpResponse<String> status(@PathVariable int id) {
		try {
			systemStorageAdminService.setActiveStatus(id);
			return PhpResponse.ok("修改成功");
		}
		catch (IllegalArgumentException ex) {
			return PhpResponse.failed(ex.getMessage());
		}
	}

	@GetMapping("/domain/{id}")
	@Operation(summary = "域名表单占位")
	public PhpResponse<String> getUpdateDomainForm(@PathVariable int id) {
		return PhpResponse.ok("ok");
	}

	@GetMapping("/method")
	@Operation(summary = "详细配置项占位")
	public PhpResponse<String> getStorageConfig() {
		return PhpResponse.ok("ok");
	}

	@PostMapping("/domain/{id}")
	@Operation(summary = "修改域名")
	public PhpResponse<String> updateDomain(@PathVariable int id, @RequestBody JsonNode body) {
		String domain = text(body, "domain");
		String cdn = text(body, "cdn");
		try {
			systemStorageAdminService.updateDomain(id, domain, cdn);
			return PhpResponse.ok("修改成功");
		}
		catch (IllegalArgumentException ex) {
			return PhpResponse.failed(ex.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除")
	public PhpResponse<String> delete(@PathVariable int id) {
		systemStorageAdminService.deleteStorage(id);
		return PhpResponse.ok("删除成功");
	}

	@PutMapping("/save_type/{type}")
	@Operation(summary = "切换存储方式")
	public PhpResponse<String> uploadType(@PathVariable int type) {
		try {
			systemStorageAdminService.setUploadType(type);
			return PhpResponse.ok(type != 1 ? "切换云存储成功,请检查是否开启使用了存储空间" : "切换本地存储成功");
		}
		catch (IllegalArgumentException ex) {
			return PhpResponse.failed(ex.getMessage());
		}
	}

	@PutMapping("/save_basic")
	@Operation(summary = "保存云存储详细配置")
	public PhpResponse<String> updateConfig(@RequestBody JsonNode body) {
		systemStorageAdminService.saveBasicConfig(body);
		return PhpResponse.ok("保存成功");
	}

	private static String text(JsonNode n, String field) {
		if (n == null || !n.has(field) || n.get(field).isNull()) {
			return "";
		}
		return n.get(field).asText("");
	}

}
