package com.bubblecloud.biz.oa.crm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.bubblecloud.biz.oa.constant.ScheduleConstants;
import com.bubblecloud.oa.api.dto.ScheduleStoreDTO;
import com.bubblecloud.oa.api.entity.ClientFollow;
import com.bubblecloud.oa.api.entity.ClientRemind;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * CRM 侧构造与 PHP 一致的「全天 + 提醒」日程请求体（客户跟进 / 付款提醒）。
 *
 * @author qinlei
 * @date 2026/4/6 16:20
 */
public final class CrmScheduleLinkHelper {

	private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private CrmScheduleLinkHelper() {
	}

	/**
	 * 付款提醒关联日程（续费 / 回款）。
	 */
	public static ScheduleStoreDTO forClientRemind(ClientRemind r) {
		if (r.getTime() == null) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		LocalDateTime dayStart = r.getTime().toLocalDate().atStartOfDay();
		LocalDateTime dayEnd = r.getTime().toLocalDate().atTime(23, 59, 59);
		String remindTimeStr = r.getTime().format(DT);
		int types = ObjectUtil.defaultIfNull(r.getTypes(), 0);
		long cid = types != 0 ? ScheduleConstants.TYPE_CLIENT_RENEW : ScheduleConstants.TYPE_CLIENT_RETURN;
		Integer contractId = ObjectUtil.defaultIfNull(r.getCid(), 0);
		ScheduleStoreDTO d = new ScheduleStoreDTO();
		String mark = StrUtil.nullToEmpty(r.getMark());
		d.setTitle(mark);
		d.setContent(mark);
		d.setRemind(1);
		d.setRemindTime(remindTimeStr);
		d.setAllDay(1);
		d.setCid(cid);
		d.setPeriod(0);
		d.setRate(1);
		d.setDays(new ArrayList<>());
		d.setStartTime(dayStart.format(DT));
		d.setEndTime(dayEnd.format(DT));
		if (types != 0) {
			d.setFailTime(null);
		}
		else {
			d.setFailTime(dayEnd.format(DT));
		}
		d.setLinkId(contractId.longValue());
		d.setUniqued(StrUtil.nullToEmpty(r.getUniqued()));
		Integer uid = r.getUserId();
		if (uid == null || uid <= 0) {
			throw new IllegalStateException("未登录");
		}
		d.setMember(List.of(uid.longValue()));
		return d;
	}

	/**
	 * 客户跟进「提醒」类型关联日程（跟进日历）。
	 */
	public static ScheduleStoreDTO forClientFollowTrack(ClientFollow r) {
		if (r.getTime() == null) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		LocalDateTime dayStart = r.getTime().toLocalDate().atStartOfDay();
		LocalDateTime dayEnd = r.getTime().toLocalDate().atTime(23, 59, 59);
		String remindTimeStr = r.getTime().format(DT);
		ScheduleStoreDTO d = new ScheduleStoreDTO();
		String content = StrUtil.nullToEmpty(r.getContent());
		d.setTitle(content);
		d.setContent(content);
		d.setRemind(1);
		d.setRemindTime(remindTimeStr);
		d.setAllDay(1);
		d.setCid((long) ScheduleConstants.TYPE_CLIENT_TRACK);
		d.setPeriod(0);
		d.setRate(1);
		d.setDays(new ArrayList<>());
		d.setStartTime(dayStart.format(DT));
		d.setEndTime(dayEnd.format(DT));
		d.setFailTime(dayEnd.format(DT));
		Integer eid = ObjectUtil.defaultIfNull(r.getEid(), 0);
		d.setLinkId(eid.longValue());
		d.setUniqued(StrUtil.nullToEmpty(r.getUniqued()));
		Integer uid = r.getUserId();
		if (uid == null || uid <= 0) {
			throw new IllegalStateException("未登录");
		}
		d.setMember(List.of(uid.longValue()));
		return d;
	}

}
