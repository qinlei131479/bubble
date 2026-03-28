package com.bubblecloud.oa.api.vo;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 系统配置键值对（序列化为扁平 JSON，兼容 PHP 前端）。
 */
@Schema(description = "系统配置键值")
public class ConfigVO {

	@JsonIgnore
	private final Map<String, String> entries = new LinkedHashMap<>();

	@JsonAnyGetter
	public Map<String, String> getEntries() {
		return entries;
	}

	@JsonAnySetter
	public void setEntry(String key, String value) {
		this.entries.put(key, value);
	}

}
