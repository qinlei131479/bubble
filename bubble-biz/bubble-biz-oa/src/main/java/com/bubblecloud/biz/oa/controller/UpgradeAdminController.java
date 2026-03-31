package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在线升级占位（对齐 PHP {@code ent/system/upgrade}，无远程升级服务）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/upgrade")
@Tag(name = "在线升级")
public class UpgradeAdminController {

	private final ObjectMapper objectMapper;

	@GetMapping("/status")
	@Operation(summary = "升级状态")
	public PhpResponse<JsonNode> status() {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("enabled", false);
		return PhpResponse.ok(n);
	}

	@GetMapping("/agreement")
	@Operation(summary = "升级协议占位")
	public PhpResponse<String> agreement() {
		return PhpResponse.ok("");
	}

	@GetMapping("/list")
	@Operation(summary = "可升级列表")
	public PhpResponse<?> list() {
		return PhpResponse.ok(java.util.List.of());
	}

	@GetMapping("/key")
	@Operation(summary = "升级包数据占位")
	public PhpResponse<String> enableData() {
		return PhpResponse.failed("暂无升级数据");
	}

	@PostMapping("/start")
	@Operation(summary = "开始升级占位")
	public PhpResponse<String> start() {
		return PhpResponse.failed("未接入升级服务");
	}

}
