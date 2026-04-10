package com.bubblecloud.biz.oa.service;

/**
 * 日程删除/写入对客户跟进、付款提醒表的副作用（对齐 PHP {@code ScheduleService} 与 {@code ClientRemindService}）。
 *
 * @author qinlei
 * @date 2026/4/6 18:40
 */
public interface CrmScheduleSideEffectService {

	/**
	 * 新建日程后同步付款提醒周期（PHP {@code saveSchedule} 内对续费/回款类型调用 {@code updatePeriod(1,...)}）。
	 */
	void syncRemindPeriodAfterScheduleInsert(String uniqued, String startTime, String endTime);

	/**
	 * 参与者更新日程任务状态时同步 {@code eb_client_remind.this_period/next_period}（PHP
	 * {@code ScheduleService::updateStatus}）。
	 */
	void syncRemindPeriodAfterParticipantStatus(int taskStatus, String uniqued, String timeZoneStart,
			String timeZoneEnd, int schedulePeriod, int scheduleRate, String scheduleDaysJson, long scheduleTypeCid);

	/**
	 * 从日历删除「客户跟进」类日程后，将跟进记录退化为普通说明（PHP {@code ClientFollowService::delScheduleAfter}）。
	 */
	void afterClientTrackScheduleDeleted(String uniqued);

	/**
	 * 从日历删除「续费/回款」类日程后删除付款提醒行（PHP {@code ClientRemindService::delScheduleAfter}）。
	 */
	void afterClientRemindScheduleDeleted(String uniqued);

}
