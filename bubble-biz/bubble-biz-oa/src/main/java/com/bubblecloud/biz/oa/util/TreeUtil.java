package com.bubblecloud.biz.oa.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 通用树组装（对齐 PHP {@code get_tree_children}，键类型统一归一化）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
public final class TreeUtil {

	private TreeUtil() {
	}

	/**
	 * @param idGetter  节点主键
	 * @param pidGetter 父主键，根节点为 0 或 null
	 * @param childrenGetter 子列表（须可修改，一般为 Lombok 生成的 {@code getChildren()}）
	 */
	public static <T> List<T> buildTree(List<T> flat, Function<T, Object> idGetter, Function<T, Object> pidGetter,
			Function<T, List<T>> childrenGetter) {
		if (flat == null || flat.isEmpty()) {
			return List.of();
		}
		Map<Object, T> map = new LinkedHashMap<>();
		for (T item : flat) {
			map.put(normalizeKey(idGetter.apply(item)), item);
			List<T> ch = childrenGetter.apply(item);
			if (ch != null) {
				ch.clear();
			}
		}
		List<T> roots = new ArrayList<>();
		for (T item : flat) {
			Object pidRaw = pidGetter.apply(item);
			Object pid = normalizeKey(pidRaw);
			if (pidRaw != null && !isZeroPid(pidRaw) && map.containsKey(pid)) {
				List<T> parentChildren = childrenGetter.apply(map.get(pid));
				if (parentChildren != null) {
					parentChildren.add(item);
				}
			}
			else {
				roots.add(item);
			}
		}
		return roots;
	}

	private static boolean isZeroPid(Object pid) {
		if (pid instanceof Number n) {
			return n.longValue() == 0L;
		}
		return Objects.equals(pid, "0");
	}

	private static Object normalizeKey(Object key) {
		if (key == null) {
			return null;
		}
		if (key instanceof Number n) {
			return n.longValue();
		}
		return key;
	}

}
