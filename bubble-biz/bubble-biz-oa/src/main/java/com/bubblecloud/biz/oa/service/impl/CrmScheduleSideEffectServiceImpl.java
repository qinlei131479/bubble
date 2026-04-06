package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.ScheduleConstants;
import com.bubblecloud.biz.oa.mapper.ClientFollowMapper;
import com.bubblecloud.biz.oa.mapper.ClientRemindMapper;
import com.bubblecloud.biz.oa.schedule.SchedulePeriodMutationHelper;
import com.bubblecloud.biz.oa.service.CrmScheduleSideEffectService;
import com.bubblecloud.oa.api.entity.ClientFollow;
import com.bubblecloud.oa.api.entity.ClientRemind;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 日程对客户跟进 / 付款提醒表的副作用实现。
 *
 * @author qinlei
 * @date 2026/4/6 18:40
 */
@Service
@RequiredArgsConstructor
public class CrmScheduleSideEffectServiceImpl implements CrmScheduleSideEffectService {

	private static final Logger log = LoggerFactory.getLogger(CrmScheduleSideEffectServiceImpl.class);

	private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final ClientRemindMapper clientRemindMapper;

	private final ClientFollowMapper clientFollowMapper;

	private final SchedulePeriodMutationHelper periodMutationHelper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void syncRemindPeriodAfterScheduleInsert(String uniqued, String startTime, String endTime) {
		updatePeriod(1, uniqued, new String[] { startTime, endTime });
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void syncRemindPeriodAfterParticipantStatus(int taskStatus, String uniqued, String timeZoneStart,
			String timeZoneEnd, int schedulePeriod, int scheduleRate, String scheduleDaysJson, long scheduleTypeCid) {
		if (StrUtil.isBlank(uniqued)) {
			return;
		}
		String second = "";
		if (taskStatus == 3 && scheduleTypeCid == ScheduleConstants.TYPE_CLIENT_RENEW) {
			LocalDateTime st = parseDateTime(timeZoneStart);
			LocalDateTime et = parseDateTime(timeZoneEnd);
			if (st != null && et != null) {
				LocalDateTime[] next = periodMutationHelper.getNextPeriod(st, et, schedulePeriod, scheduleRate,
						scheduleDaysJson);
				if (next != null && next.length > 0 && next[0] != null) {
					second = DT.format(next[0]);
				}
			}
		}
		updatePeriod(taskStatus, uniqued, new String[] { timeZoneStart, second });
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void afterClientTrackScheduleDeleted(String uniqued) {
		if (StrUtil.isBlank(uniqued)) {
			return;
		}
		clientFollowMapper.update(null,
				Wrappers.lambdaUpdate(ClientFollow.class)
					.eq(ClientFollow::getUniqued, uniqued)
					.set(ClientFollow::getTypes, 0)
					.set(ClientFollow::getUniqued, ""));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void afterClientRemindScheduleDeleted(String uniqued) {
		if (StrUtil.isBlank(uniqued)) {
			return;
		}
		clientRemindMapper.delete(Wrappers.lambdaQuery(ClientRemind.class).eq(ClientRemind::getUniqued, uniqued));
	}

	/**
	 * 与 PHP {@code ClientRemindService::updatePeriod} 分支一致。
	 */
	private void updatePeriod(int status, String uniqued, String[] nextPeriod) {
		ClientRemind info = clientRemindMapper
			.selectOne(Wrappers.lambdaQuery(ClientRemind.class).eq(ClientRemind::getUniqued, uniqued).last("LIMIT 1"));
		if (info == null) {
			log.warn("日程状态同步付款提醒记录 uniqued={}", uniqued);
			return;
		}
		if (status < 1) {
			info.setThisPeriod(null);
			info.setNextPeriod(null);
		}
		else {
			LocalDateTime p0 = nextPeriod.length > 0 ? parseDateTime(nextPeriod[0]) : null;
			info.setThisPeriod(p0);
			LocalDateTime p1 = nextPeriod.length > 1 ? parseDateTime(nextPeriod[1]) : null;
			if (ObjectUtil.defaultIfNull(info.getTypes(), 0) == 0) {
				if (status == 1) {
					info.setNextPeriod(p1);
				}
				else {
					info.setNextPeriod(null);
				}
			}
			else {
				info.setNextPeriod(p1);
			}
		}
		info.setUpdatedAt(LocalDateTime.now());
		clientRemindMapper.updateById(info);
	}

	private static LocalDateTime parseDateTime(String s) {
		if (StrUtil.isBlank(s)) {
			return null;
		}
		String x = s.trim();
		try {
			if (x.length() <= 10) {
				return java.time.LocalDate.parse(x.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
			}
			return LocalDateTime.parse(x, DT);
		}
		catch (DateTimeParseException e) {
			try {
				return java.time.LocalDate.parse(x.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
			}
			catch (Exception e2) {
				return null;
			}
		}
	}

}
