package com.bubblecloud.biz.oa.service.impl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bubblecloud.biz.oa.mapper.ClientRemindMapper;
import com.bubblecloud.biz.oa.service.ClientRemindCrmService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.ClientRemind;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 付款提醒实现（不写日程/Task，与 PHP 全量行为可后续对齐）。
 *
 * @author qinlei
 * @date 2026/4/3 14:00
 */
@Service
public class ClientRemindCrmServiceImpl extends UpServiceImpl<ClientRemindMapper, ClientRemind>
		implements ClientRemindCrmService {

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
		return super.create(dto);
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
		return super.update(ex);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R deleteById(Long id) {
		ClientRemind ex = getActiveById(id);
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		boolean ok = update(new LambdaUpdateWrapper<ClientRemind>().eq(ClientRemind::getId, id)
			.isNull(ClientRemind::getDeletedAt)
			.set(ClientRemind::getDeletedAt, LocalDateTime.now()));
		return ok ? R.ok() : R.failed("common.operation.fail");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setMark(long id, String mark) {
		ClientRemind ex = getActiveById(id);
		if (ex == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		update(new LambdaUpdateWrapper<ClientRemind>().eq(ClientRemind::getId, id)
			.isNull(ClientRemind::getDeletedAt)
			.set(ClientRemind::getMark, mark == null ? "" : mark));
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
