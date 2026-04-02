package com.bubblecloud.biz.oa.controller;

import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.service.SystemStorageService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.SystemStorage;
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
public class SystemStorageController {

	private final SystemStorageService systemStorageService;

	@GetMapping("/index")
	@Operation(summary = "云存储列表")
	public R<?> list(@RequestParam(required = false) Integer type) {
		return R.phpOk(systemStorageService.list(Wrappers.lambdaQuery(SystemStorage.class)
			.eq(SystemStorage::getIsDelete, 0)
			.orderByDesc(SystemStorage::getId)));
	}

	@GetMapping("/create/{type}")
	@Operation(summary = "创建表单占位")
	public R<String> createForm(@PathVariable Integer type) {
		return R.phpOk("ok");
	}

	@GetMapping("/form/{type}")
	@Operation(summary = "配置表单占位")
	public R<String> form(@PathVariable Integer type) {
		return R.phpOk("ok");
	}

	@GetMapping("/config")
	@Operation(summary = "当前上传方式")
	public R<Map<String, Integer>> getConfig() {
		return R.phpOk(Map.of("type", systemStorageService.getUploadType()));
	}

	@PostMapping("/config")
	@Operation(summary = "保存 access 等（占位写入扩展表）")
	public R<String> saveConfig(@RequestBody JsonNode body) {
		return R.phpOk("保存成功");
	}

	@GetMapping("/sync/{type}")
	@Operation(summary = "同步云存储（占位）")
	public R<String> sync(@PathVariable Integer type) {
		return R.phpOk("同步成功");
	}

	@PostMapping("/{type}")
	@Operation(summary = "保存云存储")
	public R<String> create(@PathVariable Integer type, @RequestBody JsonNode body) {
		SystemStorage obj = new SystemStorage();
		obj.setType(type);
		obj.setAccessKey(text(body, "accessKey"));
		obj.setName(text(body, "name"));
		obj.setRegion(text(body, "region"));
		obj.setAcl(text(body, "acl"));
		systemStorageService.create(obj);
		return R.phpOk("添加成功");
	}

	@PutMapping("/status/{id}")
	@Operation(summary = "启用该存储")
	public R<String> updateStatus(@PathVariable Long id) {
		systemStorageService.updateStatus(id);
		return R.phpOk("修改成功");
	}

	@GetMapping("/domain/{id}")
	@Operation(summary = "域名表单占位")
	public R<String> getUpdateDomainForm(@PathVariable Long id) {
		return R.phpOk("ok");
	}

	@GetMapping("/method")
	@Operation(summary = "详细配置项占位")
	public R<String> getStorageConfig() {
		return R.phpOk("ok");
	}

	@PostMapping("/domain/{id}")
	@Operation(summary = "修改域名")
	public R<String> updateDomain(@PathVariable Long id, @RequestBody JsonNode body) {
		SystemStorage obj = new SystemStorage();
		obj.setId(id);
		obj.setDomain(text(body, "domain"));
		obj.setCdn(text(body, "cdn"));
		systemStorageService.updateDomain(obj);
		return R.phpOk("修改成功");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除")
	public R<String> removeById(@PathVariable Long id) {
		systemStorageService.deleteById(id);
		return R.phpOk("删除成功");
	}

	@PutMapping("/save_type/{type}")
	@Operation(summary = "切换存储方式")
	public R<String> uploadType(@PathVariable Integer type) {
		try {
			systemStorageService.setUploadType(type);
			return R.phpOk(type != 1 ? "切换云存储成功,请检查是否开启使用了存储空间" : "切换本地存储成功");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PutMapping("/save_basic")
	@Operation(summary = "保存云存储详细配置")
	public R<String> updateConfig(@RequestBody JsonNode body) {
		systemStorageService.saveBasicConfig(body);
		return R.phpOk("保存成功");
	}

	private static String text(JsonNode n, String field) {
		if (ObjectUtil.isNull(n) || !n.has(field) || n.get(field).isNull()) {
			return "";
		}
		return n.get(field).asText("");
	}

}
