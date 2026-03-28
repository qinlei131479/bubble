package com.bubblecloud.oa.api.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 将菜单路由序列化为带 {@code /admin} 前缀的路径，与 PHP 原接口（如 {@code /admin/user/...}）一致。
 *
 * @author qinlei
 * @date 2026/3/28 下午4:30
 */
public class AdminMenuPathSerializer extends JsonSerializer<String> {

	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(toAdminPath(value));
	}

	/**
	 * 库内多为 {@code /user/...}，接口输出需为 {@code /admin/user/...}；已带 {@code /admin} 的不重复添加。
	 */
	public static String toAdminPath(String raw) {
		if (raw == null) {
			return "";
		}
		String t = raw.trim();
		if (t.isEmpty()) {
			return "";
		}
		if (t.startsWith("/admin")) {
			return t;
		}
		if (t.startsWith("/")) {
			return "/admin" + t;
		}
		return "/admin/" + t;
	}

}
