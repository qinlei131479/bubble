package com.bubblecloud.oa.api.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 将 eb_system_menus.path 中层级串（如 132/578/394/400）序列化为 JSON 数字数组，与 PHP 前端 row.path[0] 语义一致。
 *
 * @author qinlei
 * @date 2026/3/28 16:05
 */
public class MenuPathSerializer extends JsonSerializer<String> {

	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartArray();
		if (value == null || value.isBlank()) {
			gen.writeEndArray();
			return;
		}
		String trimmed = value.trim();
		// 库中占位 "0" 表示无层级路径，输出 [] 而非 [0]
		if ("0".equals(trimmed)) {
			gen.writeEndArray();
			return;
		}
		for (String part : trimmed.split("/")) {
			if (part.isEmpty()) {
				continue;
			}
			String p = part.trim();
			try {
				gen.writeNumber(Long.parseLong(p));
			}
			catch (NumberFormatException e) {
				gen.writeString(p);
			}
		}
		gen.writeEndArray();
	}

}
