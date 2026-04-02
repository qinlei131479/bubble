package com.bubblecloud.biz.oa.service;

import java.util.List;
import java.util.Map;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.EnterpriseUserDaily;
import com.bubblecloud.oa.api.entity.UserPending;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchCountVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchDailyDayVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchFastEntryVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchStatisticCardVO;
import com.bubblecloud.oa.api.vo.workbench.WorkbenchStatisticsTypeVO;

/**
 * 工作台接口（兼容 PHP ent/user/work）。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
public interface EnterpriseUserDailyService extends UpService<EnterpriseUserDaily> {

	/**
	 * 快捷入口：cates + checkd（默认含工作台、我的日程、企业通讯录）。
	 */
	WorkbenchFastEntryVO getFastEntry();

	/**
	 * 顶部四宫格数量。
	 */
	WorkbenchCountVO getWorkCount();

	/**
	 * 业绩统计卡片。
	 */
	List<WorkbenchStatisticCardVO> getStatistics(Integer types);

	/**
	 * 业绩统计类型管理数据。
	 */
	WorkbenchStatisticsTypeVO getStatisticsType();

	/**
	 * 保存快捷入口（占位成功）。
	 */
	void saveFastEntry(List<Integer> ids);

	/**
	 * 保存业绩统计类型（占位成功）。
	 */
	void saveStatisticsType(List<String> keys);

	/**
	 * 某月每日汇报摘要（对齐 PHP ReportService::getMonthDailyList）。
	 */
	Map<Integer, WorkbenchDailyDayVO> getMonthDaily(String uid, Integer entid, String yearMonth);

	/**
	 * 待办列表（对齐 PHP UserPendingService::getPendingList）。
	 */
	List<UserPending> getPendingList(String uid, Integer entid, String status);

}
