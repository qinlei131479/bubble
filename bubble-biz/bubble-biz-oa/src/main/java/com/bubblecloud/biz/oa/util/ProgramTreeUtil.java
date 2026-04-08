package com.bubblecloud.biz.oa.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cn.hutool.core.util.ObjectUtil;

/**
 * 项目模块通用树构造（对齐 PHP get_tree_children）。
 *
 * @author qinlei
 * @date 2026/4/8 14:00
 */
public final class ProgramTreeUtil {

	private ProgramTreeUtil() {
	}

	public static <T> List<T> buildForest(List<T> flat, Function<T, Long> idGetter, Function<T, Long> pidGetter,
			Function<T, List<T>> childrenGetter, java.util.function.BiConsumer<T, List<T>> childrenSetter,
			Comparator<T> siblingOrder) {
		if (flat == null || flat.isEmpty()) {
			return List.of();
		}
		Map<Long, T> map = new HashMap<>();
		for (T item : flat) {
			map.put(idGetter.apply(item), item);
			childrenSetter.accept(item, new ArrayList<>());
		}
		List<T> roots = new ArrayList<>();
		for (T item : flat) {
			Long pid = ObjectUtil.defaultIfNull(pidGetter.apply(item), 0L);
			if (pid > 0 && map.containsKey(pid)) {
				childrenGetter.apply(map.get(pid)).add(item);
			}
			else {
				roots.add(item);
			}
		}
		sortRecursive(roots, childrenGetter, siblingOrder);
		return roots;
	}

	private static <T> void sortRecursive(List<T> nodes, Function<T, List<T>> childrenGetter, Comparator<T> order) {
		if (nodes == null || nodes.isEmpty()) {
			return;
		}
		nodes.sort(order);
		for (T n : nodes) {
			sortRecursive(childrenGetter.apply(n), childrenGetter, order);
		}
	}

}
