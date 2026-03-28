package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bubblecloud.biz.oa.service.WorkbenchService;
import org.springframework.stereotype.Service;

/**
 * 工作台默认数据与占位实现。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
@Service
public class WorkbenchServiceImpl implements WorkbenchService {

	private static final String IMG_WORKBENCH =
			"https://oamanage.oss-accelerate.aliyuncs.com/attach/2022/11/2548f202211281031201027.png";
	private static final String IMG_CALENDAR =
			"https://oamanage.oss-accelerate.aliyuncs.com/attach/2022/11/2548f202211281031201027.png";
	private static final String IMG_CONTACTS =
			"https://oamanage.oss-accelerate.aliyuncs.com/attach/2023/04/11d0520230407115609948.png";

	private static final String[][] STATISTICS = {
			{ "本月业绩", "income" },
			{ "今日业绩", "today_income" },
			{ "昨日业绩", "yesterday_income" },
			{ "累计客户", "client" },
			{ "本月新增客户", "month_client" },
			{ "今日新增客户", "today_client" },
			{ "跟进未完成", "incomplete_follow" },
			{ "今日跟进记录", "today_follow" },
			{ "今日新增合同", "today_contract" },
	};

	@Override
	public Map<String, Object> getFastEntry() {
		List<Map<String, Object>> checkd = new ArrayList<>();
		checkd.add(quickItem(1001L, "工作台", 1, "/user/workbench/index", "", 0));
		checkd.add(quickItem(1002L, "我的日程", 1, "/user/calendar/index", "", 1));
		checkd.add(quickItem(1003L, "企业通讯录", 1, "/user/ent/index", "", 2));

		List<Map<String, Object>> fastEntry = new ArrayList<>();
		for (Map<String, Object> m : checkd) {
			Map<String, Object> copy = new LinkedHashMap<>(m);
			copy.put("checked", 1);
			fastEntry.add(copy);
		}

		Map<String, Object> cate = new LinkedHashMap<>();
		cate.put("id", 1);
		cate.put("cate_name", "办公");
		cate.put("pic", "");
		cate.put("fast_entry", fastEntry);

		Map<String, Object> data = new LinkedHashMap<>();
		data.put("cates", List.of(cate));
		data.put("checkd", checkd);
		return data;
	}

	private Map<String, Object> quickItem(long id, String name, int cid, String pcUrl, String uniUrl, int sort) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("id", id);
		m.put("name", name);
		m.put("cid", cid);
		m.put("pc_url", pcUrl);
		m.put("uni_url", uniUrl);
		m.put("image", name.equals("企业通讯录") ? IMG_CONTACTS : (name.equals("我的日程") ? IMG_CALENDAR : IMG_WORKBENCH));
		m.put("checked", 1);
		m.put("sort", sort);
		return m;
	}

	@Override
	public Map<String, Object> getWorkCount() {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("scheduleCount", 0);
		m.put("applyCount", 0);
		m.put("approveCount", 0);
		m.put("noticeCount", 0);
		return m;
	}

	@Override
	public List<Map<String, Object>> getStatistics(int types) {
		List<Map<String, Object>> list = new ArrayList<>();
		for (String[] row : STATISTICS) {
			Map<String, Object> item = new LinkedHashMap<>();
			item.put("title", row[0]);
			item.put("value", "0");
			list.add(item);
		}
		return list;
	}

	@Override
	public Map<String, Object> getStatisticsType() {
		List<Map<String, Object>> list = new ArrayList<>();
		List<Map<String, Object>> select = new ArrayList<>();
		for (String[] row : STATISTICS) {
			Map<String, Object> item = new LinkedHashMap<>();
			item.put("id", row[1]);
			item.put("title", row[0]);
			list.add(item);
		}
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("list", list);
		data.put("select", select);
		return data;
	}

	@Override
	public void saveFastEntry(List<Integer> ids) {
		// 占位：后续落库 eb_user_quick
	}

	@Override
	public void saveStatisticsType(List<String> keys) {
		// 占位
	}

}
