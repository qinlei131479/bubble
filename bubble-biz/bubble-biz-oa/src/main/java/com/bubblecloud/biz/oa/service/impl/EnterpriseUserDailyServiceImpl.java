package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.EnterpriseUserDailyMapper;
import com.bubblecloud.biz.oa.mapper.UserPendingMapper;
import com.bubblecloud.biz.oa.service.EnterpriseUserDailyService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.EnterpriseUserDaily;
import com.bubblecloud.oa.api.entity.UserPending;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchCountVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchFastEntryVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchQuickCateVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchQuickItemVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchStatisticCardVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchStatisticOptionVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchDailyDayVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchStatisticsTypeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 工作台默认数据与占位实现。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
@Service
@RequiredArgsConstructor
public class EnterpriseUserDailyServiceImpl extends UpServiceImpl<EnterpriseUserDailyMapper, EnterpriseUserDaily>
		implements EnterpriseUserDailyService {

	private final UserPendingMapper userPendingMapper;

	private static final String IMG_WORKBENCH = "https://oamanage.oss-accelerate.aliyuncs.com/attach/2022/11/2548f202211281031201027.png";

	private static final String IMG_CALENDAR = "https://oamanage.oss-accelerate.aliyuncs.com/attach/2022/11/2548f202211281031201027.png";

	private static final String IMG_CONTACTS = "https://oamanage.oss-accelerate.aliyuncs.com/attach/2023/04/11d0520230407115609948.png";

	private static final String[][] STATISTICS = { { "本月业绩", "income" }, { "今日业绩", "today_income" },
			{ "昨日业绩", "yesterday_income" }, { "累计客户", "client" }, { "本月新增客户", "month_client" },
			{ "今日新增客户", "today_client" }, { "跟进未完成", "incomplete_follow" }, { "今日跟进记录", "today_follow" },
			{ "今日新增合同", "today_contract" }, };

	@Override
	public WorkbenchFastEntryVO getFastEntry() {
		List<WorkbenchQuickItemVO> checkd = new ArrayList<>();
		checkd.add(quickItem(1001L, "工作台", 1, "/user/workbench/index", "", 0));
		checkd.add(quickItem(1002L, "我的日程", 1, "/user/calendar/index", "", 1));
		checkd.add(quickItem(1003L, "企业通讯录", 1, "/user/ent/index", "", 2));

		List<WorkbenchQuickItemVO> fastEntry = new ArrayList<>();
		for (WorkbenchQuickItemVO m : checkd) {
			WorkbenchQuickItemVO copy = copyQuickItem(m);
			copy.setChecked(1);
			fastEntry.add(copy);
		}

		WorkbenchQuickCateVO cate = new WorkbenchQuickCateVO();
		cate.setId(1);
		cate.setCateName("办公");
		cate.setPic("");
		cate.setFastEntry(fastEntry);

		WorkbenchFastEntryVO data = new WorkbenchFastEntryVO();
		data.setCates(List.of(cate));
		data.setCheckd(checkd);
		return data;
	}

	private WorkbenchQuickItemVO quickItem(long id, String name, int cid, String pcUrl, String uniUrl, int sort) {
		WorkbenchQuickItemVO m = new WorkbenchQuickItemVO();
		m.setId(id);
		m.setName(name);
		m.setCid(cid);
		m.setPcUrl(pcUrl);
		m.setUniUrl(uniUrl);
		m.setImage(name.equals("企业通讯录") ? IMG_CONTACTS : (name.equals("我的日程") ? IMG_CALENDAR : IMG_WORKBENCH));
		m.setChecked(1);
		m.setSort(sort);
		return m;
	}

	private WorkbenchQuickItemVO copyQuickItem(WorkbenchQuickItemVO src) {
		WorkbenchQuickItemVO m = new WorkbenchQuickItemVO();
		m.setId(src.getId());
		m.setName(src.getName());
		m.setCid(src.getCid());
		m.setPcUrl(src.getPcUrl());
		m.setUniUrl(src.getUniUrl());
		m.setImage(src.getImage());
		m.setChecked(src.getChecked());
		m.setSort(src.getSort());
		return m;
	}

	@Override
	public WorkbenchCountVO getWorkCount() {
		WorkbenchCountVO m = new WorkbenchCountVO();
		m.setScheduleCount(0);
		m.setApplyCount(0);
		m.setApproveCount(0);
		m.setNoticeCount(0);
		return m;
	}

	@Override
	public List<WorkbenchStatisticCardVO> getStatistics(Integer types) {
		List<WorkbenchStatisticCardVO> list = new ArrayList<>();
		for (String[] row : STATISTICS) {
			list.add(new WorkbenchStatisticCardVO(row[0], "0"));
		}
		return list;
	}

	@Override
	public WorkbenchStatisticsTypeVO getStatisticsType() {
		List<WorkbenchStatisticOptionVO> list = new ArrayList<>();
		List<WorkbenchStatisticOptionVO> select = new ArrayList<>();
		for (String[] row : STATISTICS) {
			list.add(new WorkbenchStatisticOptionVO(row[1], row[0]));
		}
		WorkbenchStatisticsTypeVO data = new WorkbenchStatisticsTypeVO();
		data.setList(list);
		data.setSelect(select);
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

	@Override
	public Map<Integer, WorkbenchDailyDayVO> getMonthDaily(String uid, Integer entid, String yearMonth) {
		YearMonth ym = YearMonth.parse(yearMonth);
		LocalDateTime start = ym.atDay(1).atStartOfDay();
		LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();
		List<EnterpriseUserDaily> rows = baseMapper.selectList(Wrappers.lambdaQuery(EnterpriseUserDaily.class)
			.eq(EnterpriseUserDaily::getUid, uid)
			.eq(EnterpriseUserDaily::getEntid, entid.longValue())
			.ge(EnterpriseUserDaily::getCreatedAt, start)
			.lt(EnterpriseUserDaily::getCreatedAt, end)
			.orderByAsc(EnterpriseUserDaily::getCreatedAt));
		Map<Integer, WorkbenchDailyDayVO> map = new LinkedHashMap<>();
		for (EnterpriseUserDaily d : rows) {
			if (ObjectUtil.isNull(d.getCreatedAt())) {
				continue;
			}
			int day = d.getCreatedAt().getDayOfMonth();
			WorkbenchDailyDayVO vo = new WorkbenchDailyDayVO();
			vo.setDay(day);
			vo.setFinish(d.getFinish());
			vo.setPlan(d.getPlan());
			map.put(day, vo);
		}
		return map;
	}

	@Override
	public List<UserPending> getPendingList(String uid, Integer entid, String status) {
		var q = Wrappers.lambdaQuery(UserPending.class)
			.eq(UserPending::getUid, uid)
			.eq(UserPending::getEntid, entid.longValue());
		if (StrUtil.isNotBlank(status)) {
			try {
				q.eq(UserPending::getStatus, Integer.parseInt(status.trim()));
			}
			catch (NumberFormatException ignored) {
				// 不按 status 过滤
			}
		}
		q.orderByDesc(UserPending::getId);
		return userPendingMapper.selectList(q);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(EnterpriseUserDaily req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(EnterpriseUserDaily req) {
		return super.update(req);
	}

}
