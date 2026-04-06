package com.bubblecloud.biz.oa.service.impl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bubblecloud.biz.oa.crm.CrmScheduleLinkHelper;
import com.bubblecloud.biz.oa.mapper.ClientRemindMapper;
import com.bubblecloud.biz.oa.mapper.ContractMapper;
import com.bubblecloud.biz.oa.service.ClientRemindCrmService;
import com.bubblecloud.biz.oa.service.OaCrmAsyncNotifyService;
import com.bubblecloud.biz.oa.service.OaCrmTimerTaskNameSyncService;
import com.bubblecloud.biz.oa.service.ScheduleApiService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.ClientRemind;
import com.bubblecloud.oa.api.entity.Contract;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 付款提醒实现；创建/修改/删除时与日程联动（对齐 PHP {@code ClientRemindService}）。
 *
 * @author qinlei
 * @date 2026/4/3 14:00
 */
@Service
@RequiredArgsConstructor
public class ClientRemindCrmServiceImpl extends UpServiceImpl<ClientRemindMapper, ClientRemind>
		implements ClientRemindCrmService {

	private final ScheduleApiService scheduleApiService;

	private final ContractMapper contractMapper;

	private final OaCrmAsyncNotifyService oaCrmAsyncNotifyService;

	private final OaCrmTimerTaskNameSyncService oaCrmTimerTaskNameSyncService;

	@Override
	public ClientRemind getActiveById(long id) {
		return getOne(
				new LambdaQueryWrapper<ClientRemind>().eq(ClientRemind::getId, id).isNull(ClientRemind::getDeletedAt),
				false);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(ClientRemind dto) {
		validateStore(dto);
		Long uid = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(uid)) {
			throw new IllegalStateException("未登录");
		}
		dto.setUserId(uid.intValue());
		if (dto.getEntid() == null) {
			dto.setEntid(1);
		}
		dto.setUniqued(md5Hex(uniquedPayload(dto) + System.currentTimeMillis()));
		if (dto.getStatus() == null) {
			dto.setStatus(0);
		}
		if (dto.getBillId() == null) {
			dto.setBillId(0);
		}
		if (dto.getCateId() == null) {
			dto.setCateId(0);
		}
		R res = super.create(dto);
		int entid = ObjectUtil.defaultIfNull(dto.getEntid(), 1);
		scheduleApiService.saveSchedule(uid, entid, CrmScheduleLinkHelper.forClientRemind(dto));
		return res;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(ClientRemind dto) {
		ClientRemind ex = getActiveById(dto.getId());
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (dto.getTime() == null) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		String oldUniqued = StrUtil.nullToEmpty(ex.getUniqued());
		if (dto.getNum() != null) {
			ex.setNum(dto.getNum());
		}
		if (dto.getTypes() != null) {
			ex.setTypes(dto.getTypes());
		}
		if (dto.getMark() != null) {
			ex.setMark(dto.getMark());
		}
		ex.setTime(dto.getTime());
		if (dto.getCateId() != null) {
			ex.setCateId(dto.getCateId());
		}
		ex.setUniqued(md5Hex(uniquedPayload(ex) + System.currentTimeMillis()));
		R res = super.update(ex);
		if (StrUtil.isNotBlank(oldUniqued)) {
			scheduleApiService.deleteRemindByUniqued(ObjectUtil.defaultIfNull(ex.getUserId(), 0).longValue(),
					oldUniqued);
		}
		int entid = ObjectUtil.defaultIfNull(ex.getEntid(), 1);
		scheduleApiService.saveSchedule(ObjectUtil.defaultIfNull(ex.getUserId(), 0).longValue(), entid,
				CrmScheduleLinkHelper.forClientRemind(ex));
		return res;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R deleteById(Long id) {
		ClientRemind ex = getActiveById(id);
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		Long cur = OaSecurityUtil.currentUserId();
		if (StrUtil.isNotBlank(ex.getUniqued())) {
			long delUid = cur != null ? cur : ObjectUtil.defaultIfNull(ex.getUserId(), 0).longValue();
			scheduleApiService.deleteRemindByUniqued(delUid, ex.getUniqued());
		}
		maybeClearContractRenew(ex);
		boolean ok = update(new LambdaUpdateWrapper<ClientRemind>().eq(ClientRemind::getId, id)
			.isNull(ClientRemind::getDeletedAt)
			.set(ClientRemind::getDeletedAt, LocalDateTime.now()));
		if (ok) {
			int contractCid = ObjectUtil.defaultIfNull(ex.getCid(), 0);
			oaCrmAsyncNotifyService.clientRemindDeleted(contractCid);
		}
		return ok ? R.ok() : R.failed("common.operation.fail");
	}

	private void maybeClearContractRenew(ClientRemind ex) {
		Integer cid = ex.getCid();
		if (cid == null || cid <= 0) {
			return;
		}
		if (!Integer.valueOf(1).equals(ObjectUtil.defaultIfNull(ex.getTypes(), 0))) {
			return;
		}
		long others = count(Wrappers.lambdaQuery(ClientRemind.class)
			.eq(ClientRemind::getCid, cid)
			.eq(ClientRemind::getTypes, 1)
			.ne(ClientRemind::getId, ex.getId())
			.isNull(ClientRemind::getDeletedAt));
		if (others > 0) {
			return;
		}
		contractMapper.update(null,
				Wrappers.lambdaUpdate(Contract.class).eq(Contract::getId, cid.longValue()).set(Contract::getRenew, 0));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setMark(long id, String mark) {
		ClientRemind ex = getActiveById(id);
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		String m = mark == null ? "" : mark;
		update(new LambdaUpdateWrapper<ClientRemind>().eq(ClientRemind::getId, id)
			.isNull(ClientRemind::getDeletedAt)
			.set(ClientRemind::getMark, m));
		if (StrUtil.isNotBlank(ex.getUniqued())) {
			oaCrmTimerTaskNameSyncService.updateNameByUniqued(ex.getUniqued(), m);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void abjure(long id) {
		ClientRemind ex = getActiveById(id);
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		update(new LambdaUpdateWrapper<ClientRemind>().eq(ClientRemind::getId, id)
			.isNull(ClientRemind::getDeletedAt)
			.set(ClientRemind::getStatus, 1));
	}

	private static String uniquedPayload(ClientRemind r) {
		return String.valueOf(r.getEid()) + "|" + r.getCid() + "|" + r.getNum() + "|" + r.getTypes() + "|"
				+ (r.getMark() == null ? "" : r.getMark()) + "|" + r.getTime();
	}

	private static String md5Hex(String s) {
		return md5Hex(s.getBytes(StandardCharsets.UTF_8));
	}

	private static String md5Hex(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] d = md.digest(input);
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

	private static void validateStore(ClientRemind dto) {
		if (dto.getTime() == null) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		if (dto.getEid() == null || dto.getEid() <= 0) {
			throw new IllegalArgumentException("请填写客户ID");
		}
		if (dto.getCid() == null) {
			dto.setCid(0);
		}
		if (dto.getNum() == null || dto.getNum().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("请填写金额");
		}
		if (dto.getTypes() == null) {
			throw new IllegalArgumentException("请选择提醒类型");
		}
		if (StrUtil.isBlank(dto.getMark())) {
			throw new IllegalArgumentException("请填写备注信息");
		}
	}

}
