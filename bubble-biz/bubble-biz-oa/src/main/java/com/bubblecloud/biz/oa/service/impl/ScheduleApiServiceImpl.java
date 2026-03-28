package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bubblecloud.biz.oa.service.ScheduleApiService;
import org.springframework.stereotype.Service;

/**
 * 日程与待办占位实现。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
@Service
public class ScheduleApiServiceImpl implements ScheduleApiService {

	@Override
	public List<Map<String, Object>> typeList() {
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(typeRow(1L, "默认", "default", "#409EFF"));
		list.add(typeRow(2L, "个人提醒", "personal", "#1890FF"));
		list.add(typeRow(3L, "客户跟进", "client_track", "#19BE6B"));
		return list;
	}

	private Map<String, Object> typeRow(long id, String name, String key, String color) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("id", id);
		m.put("name", name);
		m.put("key", key);
		m.put("color", color);
		m.put("info", "");
		m.put("is_public", 1);
		return m;
	}

	@Override
	public List<Map<String, Object>> scheduleIndex(Map<String, Object> body) {
		return new ArrayList<>();
	}

	@Override
	public void updateStatus(long id, Map<String, Object> body) {
		// 占位
	}

	@Override
	public List<Map<String, Object>> userScheduleList(Map<String, String> query) {
		List<Map<String, Object>> outer = new ArrayList<>();
		Map<String, Object> day = new LinkedHashMap<>();
		day.put("list", new ArrayList<>());
		outer.add(day);
		return outer;
	}

}
