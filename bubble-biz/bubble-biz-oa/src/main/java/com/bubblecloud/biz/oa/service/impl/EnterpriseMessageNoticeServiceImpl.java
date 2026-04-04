package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseMessageNoticeMapper;
import com.bubblecloud.biz.oa.mapper.MessageTemplateMapper;
import com.bubblecloud.biz.oa.service.EnterpriseMessageNoticeService;
import com.bubblecloud.biz.oa.util.OaNoticeButtonBuilder;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.entity.EnterpriseMessageNotice;
import com.bubblecloud.oa.api.entity.MessageTemplate;
import com.bubblecloud.oa.api.vo.common.CommonMessageItemVO;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 企业消息列表。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
@RequiredArgsConstructor
public class EnterpriseMessageNoticeServiceImpl
		extends UpServiceImpl<EnterpriseMessageNoticeMapper, EnterpriseMessageNotice>
		implements EnterpriseMessageNoticeService {

	private final MessageTemplateMapper messageTemplateMapper;

	private final EnterpriseMapper enterpriseMapper;

	private final AdminMapper adminMapper;

	private final ObjectMapper objectMapper;

	@Override
	public CommonMessageVO getMessageList(Long adminId, String uid, Long entId, Integer page, Integer limit,
			String cateId, String title, Integer isReadFilter) {
		return getMessageListInternal(adminId, uid, entId, page, limit, cateId, title, isReadFilter, true);
	}

	@Override
	public CommonMessageVO getCompanyMessage(Long adminId, String uid, Long entId, Integer page, Integer limit,
			String cateId, String title, String isReadRaw) {
		return getMessageListInternal(adminId, uid, entId, page, limit, nullToEmpty(cateId), nullToEmpty(title),
				parseOptionalIsRead(isReadRaw), true);
	}

	@Override
	public CommonMessageVO getCompanyMessageAll(Long entId, Integer page, Integer limit, String cateId, String title,
			String isReadRaw) {
		return getMessageListInternal(0L, "", entId, page, limit, nullToEmpty(cateId), nullToEmpty(title),
				parseOptionalIsRead(isReadRaw), false);
	}

	private static String nullToEmpty(String s) {
		return ObjectUtil.isNull(s) ? "" : s;
	}

	/**
	 * 与 PHP 请求 {@code is_read} 一致：空串不按已读过滤；{@code 0}/{@code 1} 等解析为整数。
	 */
	private static Integer parseOptionalIsRead(String raw) {
		if (ObjectUtil.isNull(raw) || raw.isBlank()) {
			return null;
		}
		String s = raw.trim();
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	private CommonMessageVO getMessageListInternal(Long adminId, String uid, Long entId, Integer page, Integer limit,
			String cateId, String title, Integer isReadFilter, boolean restrictToRecipient) {
		LambdaQueryWrapper<EnterpriseMessageNotice> q = baseQuery(adminId, uid, entId, cateId, title, isReadFilter,
				restrictToRecipient);
		long total = this.count(q);
		q.orderByDesc(EnterpriseMessageNotice::getId);
		Page<EnterpriseMessageNotice> p = this.page(new Page<>(page, limit), q);
		List<EnterpriseMessageNotice> rows = p.getRecords();
		CommonMessageVO vo = new CommonMessageVO();
		if (rows.isEmpty()) {
			vo.setList(Collections.emptyList());
			vo.setMessageNum(total);
			return vo;
		}
		Set<Integer> msgIds = rows.stream()
			.map(EnterpriseMessageNotice::getMessageId)
			.filter(Objects::nonNull)
			.collect(Collectors.toCollection(HashSet::new));
		Map<Integer, MessageTemplate> tplByMsgId = loadType0Templates(msgIds);
		Set<Long> entIds = rows.stream()
			.map(EnterpriseMessageNotice::getEntid)
			.filter(e -> ObjectUtil.isNotNull(e) && e > 0)
			.collect(Collectors.toCollection(HashSet::new));
		Map<Long, Enterprise> entMap = loadEnterprises(entIds);
		Set<String> toUids = rows.stream().map(EnterpriseMessageNotice::getToUid).collect(Collectors.toSet());
		Map<String, Admin> adminByToUid = loadAdminsForToUids(toUids);
		List<CommonMessageItemVO> list = new ArrayList<>(rows.size());
		for (EnterpriseMessageNotice row : rows) {
			list.add(toItem(row, tplByMsgId, entMap, adminByToUid));
		}
		vo.setList(list);
		vo.setMessageNum(total);
		return vo;
	}

	private LambdaQueryWrapper<EnterpriseMessageNotice> baseQuery(Long adminId, String uid, Long entId, String cateId,
			String title, Integer isReadFilter, boolean restrictToRecipient) {
		LambdaQueryWrapper<EnterpriseMessageNotice> q = Wrappers.lambdaQuery(EnterpriseMessageNotice.class);
		q.and(w -> w.eq(EnterpriseMessageNotice::getEntid, entId).or().eq(EnterpriseMessageNotice::getEntid, 0L));
		if (restrictToRecipient) {
			String adminIdStr = String.valueOf(adminId);
			String uidStr = StrUtil.nullToEmpty(uid);
			q.and(w -> w.eq(EnterpriseMessageNotice::getToUid, uidStr).or()
					.eq(EnterpriseMessageNotice::getToUid, adminIdStr));
		}
		if (StrUtil.isNotBlank(cateId) && !"0".equals(cateId)) {
			try {
				q.eq(EnterpriseMessageNotice::getCateId, Integer.parseInt(cateId.trim()));
			}
			catch (NumberFormatException ignored) {
				// ignore
			}
		}
		if (StrUtil.isNotBlank(title)) {
			String t = "%" + title.trim() + "%";
			q.and(w -> w.like(EnterpriseMessageNotice::getTitle, t).or().like(EnterpriseMessageNotice::getMessage, t));
		}
		if (isReadFilter != null) {
			q.eq(EnterpriseMessageNotice::getIsRead, isReadFilter);
		}
		return q;
	}

	private Map<Integer, MessageTemplate> loadType0Templates(Set<Integer> messageIds) {
		if (messageIds.isEmpty()) {
			return Collections.emptyMap();
		}
		List<MessageTemplate> tpls = messageTemplateMapper.selectList(Wrappers.lambdaQuery(MessageTemplate.class)
			.in(MessageTemplate::getMessageId, messageIds.stream().map(Integer::longValue).toList())
			.eq(MessageTemplate::getType, 0)
			.isNull(MessageTemplate::getDeletedAt));
		Map<Integer, MessageTemplate> map = new HashMap<>();
		for (MessageTemplate t : tpls) {
			if (ObjectUtil.isNotNull(t.getMessageId())) {
				map.putIfAbsent(t.getMessageId().intValue(), t);
			}
		}
		return map;
	}

	private Map<Long, Enterprise> loadEnterprises(Set<Long> entIds) {
		if (entIds.isEmpty()) {
			return Collections.emptyMap();
		}
		List<Enterprise> list = enterpriseMapper
			.selectList(Wrappers.lambdaQuery(Enterprise.class).in(Enterprise::getId, entIds));
		return list.stream().collect(Collectors.toMap(Enterprise::getId, e -> e, (a, b) -> a));
	}

	private Map<String, Admin> loadAdminsForToUids(Set<String> toUids) {
		Map<String, Admin> map = new HashMap<>();
		Set<Long> ids = new HashSet<>();
		Set<String> uuids = new HashSet<>();
		for (String tu : toUids) {
			if (StrUtil.isBlank(tu)) {
				continue;
			}
			if (tu.chars().allMatch(Character::isDigit)) {
				try {
					ids.add(Long.parseLong(tu));
				}
				catch (NumberFormatException ignored) {
					uuids.add(tu);
				}
			}
			else {
				uuids.add(tu);
			}
		}
		if (!ids.isEmpty()) {
			List<Admin> byId = adminMapper.selectList(Wrappers.lambdaQuery(Admin.class)
				.in(Admin::getId, ids)
				.isNull(Admin::getDeletedAt));
			for (Admin a : byId) {
				map.put(String.valueOf(a.getId()), a);
			}
		}
		for (String u : uuids) {
			Admin a = adminMapper.selectOne(Wrappers.lambdaQuery(Admin.class)
				.eq(Admin::getUid, u)
				.isNull(Admin::getDeletedAt)
				.last("LIMIT 1"));
			if (ObjectUtil.isNotNull(a)) {
				map.put(u, a);
			}
		}
		return map;
	}

	private CommonMessageItemVO toItem(EnterpriseMessageNotice row, Map<Integer, MessageTemplate> tplByMsgId,
			Map<Long, Enterprise> entMap, Map<String, Admin> adminByToUid) {
		CommonMessageItemVO vo = new CommonMessageItemVO();
		BeanUtil.copyProperties(row, vo);
		vo.setButtonTemplate(parseJsonField(row.getButtonTemplate()));
		vo.setOther(parseJsonField(row.getOther()));
		MessageTemplate tpl = ObjectUtil.isNull(row.getMessageId()) ? null : tplByMsgId.get(row.getMessageId());
		String effUrl = row.getUrl();
		String effUni = row.getUniUrl();
		if (ObjectUtil.isNotNull(tpl)) {
			if (StrUtil.isNotBlank(tpl.getUrl())) {
				effUrl = tpl.getUrl();
			}
			if (StrUtil.isNotBlank(tpl.getUniUrl())) {
				effUni = tpl.getUniUrl();
			}
		}
		vo.setUrl(StrUtil.nullToEmpty(effUrl));
		vo.setUniUrl(StrUtil.nullToEmpty(effUni));
		int st = OaNoticeButtonBuilder.linkStatusOrZero(row.getLinkStatus());
		int lid = OaNoticeButtonBuilder.linkIdOrZero(row.getLinkId());
		String tt = StrUtil.nullToEmpty(row.getTemplateType());
		vo.setButtons(OaNoticeButtonBuilder.buildButtons(tt, st, lid, vo.getUrl(), vo.getUniUrl()));
		vo.setDetail(Collections.emptyList());
		if (ObjectUtil.isNotNull(row.getEntid()) && row.getEntid() > 0) {
			Enterprise e = entMap.get(row.getEntid());
			if (ObjectUtil.isNotNull(e)) {
				Map<String, Object> ent = new HashMap<>(2);
				ent.put("id", e.getId());
				ent.put("enterprise_name", StrUtil.nullToEmpty(e.getName()));
				vo.setEnterprise(ent);
			}
		}
		Admin adm = adminByToUid.get(StrUtil.nullToEmpty(row.getToUid()));
		if (ObjectUtil.isNotNull(adm)) {
			Map<String, Object> u = new HashMap<>(2);
			u.put("id", adm.getId());
			u.put("name", StrUtil.nullToEmpty(adm.getName()));
			vo.setUser(u);
		}
		return vo;
	}

	private Object parseJsonField(String raw) {
		if (StrUtil.isBlank(raw)) {
			return null;
		}
		String s = raw.trim();
		try {
			JsonNode n = objectMapper.readTree(s);
			if (n.isMissingNode() || n.isNull()) {
				return null;
			}
			return n;
		}
		catch (Exception ignored) {
			return raw;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchUpdateCompanyMessageRead(long adminId, String uid, long entId, int isRead, Long cateId,
			List<Long> ids) {
		List<Long> idList = ObjectUtil.defaultIfNull(ids, Collections.emptyList());
		boolean hasCate = ObjectUtil.isNotNull(cateId) && cateId > 0;
		boolean hasIds = !idList.isEmpty();
		if (hasCate) {
			LambdaUpdateWrapper<EnterpriseMessageNotice> uw = companyBatchWrapper(adminId, uid, entId)
				.set(EnterpriseMessageNotice::getIsRead, isRead)
				.eq(EnterpriseMessageNotice::getCateId, cateId.intValue());
			int n = baseMapper.update(null, uw);
			if (n <= 0) {
				throw new IllegalArgumentException("修改失败");
			}
			return;
		}
		if (hasIds) {
			LambdaUpdateWrapper<EnterpriseMessageNotice> uw = companyBatchWrapper(adminId, uid, entId)
				.set(EnterpriseMessageNotice::getIsRead, isRead)
				.in(EnterpriseMessageNotice::getId, idList);
			int n = baseMapper.update(null, uw);
			if (n <= 0) {
				throw new IllegalArgumentException("修改失败");
			}
			return;
		}
		if (isRead == 1) {
			LambdaUpdateWrapper<EnterpriseMessageNotice> uw = companyBatchWrapper(adminId, uid, entId)
				.set(EnterpriseMessageNotice::getIsRead, isRead);
			int n = baseMapper.update(null, uw);
			if (n <= 0) {
				throw new IllegalArgumentException("修改失败");
			}
			return;
		}
		throw new IllegalArgumentException("修改失败");
	}

	private static LambdaUpdateWrapper<EnterpriseMessageNotice> companyBatchWrapper(long adminId, String uid,
			long entId) {
		return Wrappers.lambdaUpdate(EnterpriseMessageNotice.class)
			.and(w -> w.eq(EnterpriseMessageNotice::getEntid, entId).or().eq(EnterpriseMessageNotice::getEntid, 0L))
			.and(w -> w.eq(EnterpriseMessageNotice::getToUid, StrUtil.nullToEmpty(uid)).or()
					.eq(EnterpriseMessageNotice::getToUid, String.valueOf(adminId)));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchDeleteCompanyMessages(long adminId, String uid, long entId, List<Long> ids) {
		List<Long> idList = ObjectUtil.defaultIfNull(ids, Collections.emptyList());
		if (idList.isEmpty()) {
			throw new IllegalArgumentException("缺少参数");
		}
		LambdaQueryWrapper<EnterpriseMessageNotice> dq = Wrappers.lambdaQuery(EnterpriseMessageNotice.class)
			.in(EnterpriseMessageNotice::getId, idList)
			.and(w -> w.eq(EnterpriseMessageNotice::getEntid, entId).or().eq(EnterpriseMessageNotice::getEntid, 0L))
			.and(w -> w.eq(EnterpriseMessageNotice::getToUid, StrUtil.nullToEmpty(uid)).or()
					.eq(EnterpriseMessageNotice::getToUid, String.valueOf(adminId)));
		int n = baseMapper.delete(dq);
		if (n <= 0) {
			throw new IllegalArgumentException("删除失败");
		}
	}

	@Override
	public void updateMessageRead(Long adminId, String uid, Long messageId, Integer isRead) {
		EnterpriseMessageNotice row = this.getById(messageId);
		if (ObjectUtil.isNull(row)) {
			throw new IllegalArgumentException("消息不存在");
		}
		String idStr = String.valueOf(adminId);
		boolean ok = idStr.equals(row.getToUid()) || (StrUtil.isNotBlank(uid) && uid.equals(row.getToUid()));
		if (!ok) {
			throw new IllegalArgumentException("无权操作该消息");
		}
		row.setIsRead(isRead);
		this.updateById(row);
	}

	/**
	 * 未读消息按分类计数（对齐 PHP MessageNoticeDao::getMessageGroupCount + 双 to_uid 形态）。
	 */
	@Override
	public List<Map<String, Object>> countUnreadByCate(long entId, long adminId, String uuid) {
		QueryWrapper<EnterpriseMessageNotice> qw = new QueryWrapper<>();
		qw.select("cate_id", "COUNT(*) AS cnt")
			.eq("entid", entId)
			.eq("is_read", 0)
			.and(w -> w.eq("to_uid", String.valueOf(adminId)).or().eq("to_uid", StrUtil.nullToEmpty(uuid)))
			.groupBy("cate_id");
		return baseMapper.selectMaps(qw);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(EnterpriseMessageNotice req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(EnterpriseMessageNotice req) {
		return super.update(req);
	}

}
