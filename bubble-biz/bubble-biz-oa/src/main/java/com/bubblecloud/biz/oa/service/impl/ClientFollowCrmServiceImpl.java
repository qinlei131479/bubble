package com.bubblecloud.biz.oa.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.crm.CrmScheduleLinkHelper;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.ClientFollowMapper;
import com.bubblecloud.biz.oa.mapper.CustomerMapper;
import com.bubblecloud.biz.oa.mapper.ScheduleMapper;
import com.bubblecloud.biz.oa.mapper.ScheduleRemindMapper;
import com.bubblecloud.biz.oa.mapper.SystemAttachMapper;
import com.bubblecloud.biz.oa.service.ClientFollowCrmService;
import com.bubblecloud.biz.oa.service.FollowAttachRelationService;
import com.bubblecloud.biz.oa.service.OaCrmAsyncNotifyService;
import com.bubblecloud.biz.oa.service.ScheduleApiService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.ScheduleStatusUpdateDTO;
import com.bubblecloud.oa.api.entity.ClientFollow;
import com.bubblecloud.oa.api.entity.Customer;
import com.bubblecloud.oa.api.entity.Schedule;
import com.bubblecloud.oa.api.entity.ScheduleRemind;
import com.bubblecloud.oa.api.entity.SystemAttach;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 客户跟进实现；提醒类跟进与日程联动（对齐 PHP {@code ClientFollowService}）。
 *
 * @author qinlei
 * @date 2026/4/3 14:00
 */
@Service
@RequiredArgsConstructor
public class ClientFollowCrmServiceImpl extends UpServiceImpl<ClientFollowMapper, ClientFollow>
		implements ClientFollowCrmService {

	private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final int DEFAULT_ENTID = 1;

	private final CustomerMapper customerMapper;

	private final ScheduleApiService scheduleApiService;

	private final ScheduleRemindMapper scheduleRemindMapper;

	private final ScheduleMapper scheduleMapper;

	private final FollowAttachRelationService followAttachRelationService;

	private final SystemAttachMapper systemAttachMapper;

	private final OaCrmAsyncNotifyService oaCrmAsyncNotifyService;

	@Override
	public List<ClientFollow> listByEid(Integer eid, Integer status) {
		LambdaQueryWrapper<ClientFollow> w = Wrappers.lambdaQuery(ClientFollow.class)
			.eq(ClientFollow::getEid, eid)
			.isNull(ClientFollow::getDeletedAt)
			.orderByDesc(ClientFollow::getId);
		if (ObjectUtil.isNotNull(status) && status != 0) {
			w.eq(ClientFollow::getStatus, status);
		}
		List<ClientFollow> rows = list(w);
		if (rows.isEmpty()) {
			return rows;
		}
		List<Integer> fids = new ArrayList<>();
		for (ClientFollow f : rows) {
			if (f.getId() != null) {
				fids.add(f.getId().intValue());
			}
		}
		if (fids.isEmpty()) {
			return rows;
		}
		List<SystemAttach> all = systemAttachMapper.selectList(Wrappers.lambdaQuery(SystemAttach.class)
			.eq(SystemAttach::getRelationType, OaConstants.RELATION_TYPE_FOLLOW)
			.in(SystemAttach::getRelationId, fids));
		Map<Integer, List<SystemAttach>> byRel = all.stream()
			.collect(Collectors.groupingBy(SystemAttach::getRelationId));
		for (ClientFollow f : rows) {
			if (f.getId() == null) {
				continue;
			}
			f.setAttachs(byRel.getOrDefault(f.getId().intValue(), List.of()));
		}
		return rows;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(ClientFollow dto) {
		Long uid = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(uid)) {
			throw new IllegalStateException("未登录");
		}
		dto.setUserId(uid.intValue());
		Customer customer = customerMapper.selectById(dto.getEid().longValue());
		if (ObjectUtil.isNull(customer) || ObjectUtil.isNotNull(customer.getDeletedAt())) {
			throw new IllegalArgumentException("客户数据异常");
		}
		if (customer.getUid() == null || customer.getUid() < 1) {
			throw new IllegalArgumentException("公海客户不支持填写跟进");
		}
		dto.setFollowVersion(ObjectUtil.defaultIfNull(customer.getReturnNum(), 0));
		int types = ObjectUtil.defaultIfNull(dto.getTypes(), 0);
		if (types == 1) {
			if (dto.getTime() == null) {
				throw new IllegalArgumentException("common.empty.attrs");
			}
			dto.setAttachIds(null);
			dto.setFollowId(null);
			dto.setUniqued(md5Hex(uniquedPayload(dto) + System.currentTimeMillis()));
			dto.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 0));
			R res = super.create(dto);
			scheduleApiService.saveSchedule(uid, DEFAULT_ENTID, CrmScheduleLinkHelper.forClientFollowTrack(dto));
			return res;
		}
		completeDueTrackSchedules(dto.getEid());
		if (dto.getFollowId() != null && dto.getFollowId() > 0) {
			update(new LambdaUpdateWrapper<ClientFollow>().eq(ClientFollow::getId, dto.getFollowId())
				.set(ClientFollow::getStatus, 2));
		}
		dto.setTime(null);
		List<Integer> attachIds = dto.getAttachIds();
		dto.setFollowId(null);
		dto.setAttachIds(null);
		R created = super.create(dto);
		if (dto.getId() != null && attachIds != null) {
			followAttachRelationService.saveRelation(DEFAULT_ENTID, String.valueOf(uid), dto.getId(), attachIds);
		}
		return created;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(ClientFollow dto) {
		ClientFollow ex = getOne(Wrappers.lambdaQuery(ClientFollow.class)
			.eq(ClientFollow::getId, dto.getId())
			.isNull(ClientFollow::getDeletedAt), false);
		if (ObjectUtil.isNull(ex)) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		Long uid = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(uid)) {
			throw new IllegalStateException("未登录");
		}
		int exTypes = ObjectUtil.defaultIfNull(ex.getTypes(), 0);
		if (exTypes == 1) {
			if (dto.getTime() == null) {
				throw new IllegalArgumentException("common.empty.attrs");
			}
			dto.setAttachIds(null);
			String oldU = StrUtil.nullToEmpty(ex.getUniqued());
			if (StrUtil.isNotBlank(dto.getContent())) {
				ex.setContent(dto.getContent());
			}
			ex.setTime(dto.getTime());
			if (dto.getStatus() != null) {
				ex.setStatus(dto.getStatus());
			}
			ex.setUniqued(md5Hex(uniquedPayload(ex) + System.currentTimeMillis()));
			ex.setUserId(uid.intValue());
			R res = super.update(ex);
			if (StrUtil.isNotBlank(oldU)) {
				scheduleApiService.deleteRemindByUniqued(uid, oldU);
			}
			scheduleApiService.saveSchedule(uid, DEFAULT_ENTID, CrmScheduleLinkHelper.forClientFollowTrack(ex));
			return res;
		}
		List<Integer> attachIds = dto.getAttachIds();
		dto.setAttachIds(null);
		R res = super.update(dto);
		if (attachIds != null && dto.getId() != null) {
			followAttachRelationService.saveRelation(DEFAULT_ENTID, String.valueOf(uid), dto.getId(), attachIds);
		}
		return res;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void softDeleteById(Long id) {
		ClientFollow ex = getById(id);
		if (ObjectUtil.isNull(ex)) {
			return;
		}
		Long cur = OaSecurityUtil.currentUserId();
		if (ObjectUtil.defaultIfNull(ex.getTypes(), 0) == 1 && StrUtil.isNotBlank(ex.getUniqued())) {
			long delUid = cur != null ? cur : ObjectUtil.defaultIfNull(ex.getUserId(), 0).longValue();
			scheduleApiService.deleteRemindByUniqued(delUid, ex.getUniqued());
		}
		lambdaUpdate().eq(ClientFollow::getId, id).set(ClientFollow::getDeletedAt, LocalDateTime.now()).update();
		if (ex.getEid() != null) {
			oaCrmAsyncNotifyService.clientFollowDeleted(ex.getEid());
		}
	}

	private void completeDueTrackSchedules(Integer eid) {
		if (eid == null || eid < 1) {
			return;
		}
		LocalDateTime now = LocalDateTime.now();
		List<ClientFollow> due = list(Wrappers.lambdaQuery(ClientFollow.class)
			.eq(ClientFollow::getEid, eid)
			.eq(ClientFollow::getTypes, 1)
			.eq(ClientFollow::getStatus, 0)
			.isNull(ClientFollow::getDeletedAt)
			.lt(ClientFollow::getTime, now));
		for (ClientFollow row : due) {
			if (StrUtil.isBlank(row.getUniqued())) {
				continue;
			}
			update(new LambdaUpdateWrapper<ClientFollow>().eq(ClientFollow::getId, row.getId())
				.set(ClientFollow::getStatus, 2));
			ScheduleRemind rem = scheduleRemindMapper.selectOne(Wrappers.lambdaQuery(ScheduleRemind.class)
				.eq(ScheduleRemind::getUniqued, row.getUniqued())
				.last("LIMIT 1"));
			if (rem == null || rem.getSid() == null) {
				continue;
			}
			Schedule sch = scheduleMapper.selectById(rem.getSid());
			if (sch == null || !Integer.valueOf(0).equals(ObjectUtil.defaultIfNull(sch.getStatus(), 0))) {
				continue;
			}
			ScheduleStatusUpdateDTO body = new ScheduleStatusUpdateDTO();
			body.setId(rem.getSid());
			body.setStatus(3);
			body.setStart(fmtDt(sch.getStartTime()));
			body.setEnd(fmtDt(sch.getEndTime()));
			long participant = parseUid(rem.getUid());
			if (participant <= 0) {
				continue;
			}
			try {
				scheduleApiService.updateStatus(participant, body);
			}
			catch (Exception ignored) {
				// 与 PHP catch 一致：不因单条日程异常阻断跟进保存
			}
		}
	}

	private static long parseUid(String raw) {
		if (StrUtil.isBlank(raw)) {
			return 0L;
		}
		try {
			return Long.parseLong(raw.trim());
		}
		catch (NumberFormatException e) {
			return 0L;
		}
	}

	private static String fmtDt(LocalDateTime t) {
		return t == null ? "" : DT.format(t);
	}

	private static String uniquedPayload(ClientFollow f) {
		return String.valueOf(f.getEid()) + "|" + f.getUserId() + "|" + StrUtil.nullToEmpty(f.getContent()) + "|"
				+ f.getTime() + "|" + f.getTypes();
	}

	private static String md5Hex(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : d) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

}
