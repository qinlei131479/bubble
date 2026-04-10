package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.MessageCategoryMapper;
import com.bubblecloud.biz.oa.mapper.MessageMapper;
import com.bubblecloud.biz.oa.mapper.MessageTemplateMapper;
import com.bubblecloud.biz.oa.service.EnterpriseMessageNoticeService;
import com.bubblecloud.biz.oa.service.MessageService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Message;
import com.bubblecloud.oa.api.entity.MessageCategory;
import com.bubblecloud.oa.api.entity.MessageTemplate;
import com.bubblecloud.oa.api.vo.message.MessageCategoryCountVO;
import com.bubblecloud.oa.api.vo.message.MessageTemplateJsonVO;
import com.bubblecloud.oa.api.vo.message.SystemMessageListItemVO;
import com.bubblecloud.oa.api.vo.message.SystemMessageListResultVO;
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
 * 系统消息配置实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends UpServiceImpl<MessageMapper, Message> implements MessageService {

	private static final int TYPE_SYSTEM = 0;

	private static final int TYPE_SMS = 1;

	private static final int TYPE_WORK = 2;

	private static final int TYPE_DING = 3;

	private static final int TYPE_OTHER = 4;

	private final MessageTemplateMapper messageTemplateMapper;

	private final MessageCategoryMapper messageCategoryMapper;

	private final EnterpriseMessageNoticeService enterpriseMessageNoticeService;

	private final ObjectMapper objectMapper;

	@Override
	public SystemMessageListResultVO getSystemMessageList(long entid, int page, int limit, Long cateId, String title) {
		var q = Wrappers.lambdaQuery(Message.class).eq(Message::getEntid, entid);
		if (ObjectUtil.isNotNull(cateId) && cateId > 0) {
			q.eq(Message::getCateId, cateId);
		}
		if (StrUtil.isNotBlank(title)) {
			String t = "%" + title.trim() + "%";
			q.and(w -> w.like(Message::getTitle, t).or().like(Message::getContent, t));
		}
		long total = this.count(q);
		q.orderByDesc(Message::getId);
		Page<Message> p = this.page(new Page<>(page, limit), q);
		SystemMessageListResultVO res = new SystemMessageListResultVO();
		res.setCount(total);
		List<Message> rows = p.getRecords();
		if (rows.isEmpty()) {
			res.setList(List.of());
			return res;
		}
		List<Long> msgIds = rows.stream().map(Message::getId).toList();
		List<MessageTemplate> tplRows = messageTemplateMapper.selectList(Wrappers.lambdaQuery(MessageTemplate.class)
			.in(MessageTemplate::getMessageId, msgIds)
			.isNull(MessageTemplate::getDeletedAt)
			.orderByAsc(MessageTemplate::getType));
		Map<Long, List<MessageTemplate>> byMsg = tplRows.stream()
			.collect(Collectors.groupingBy(MessageTemplate::getMessageId));
		List<SystemMessageListItemVO> list = new ArrayList<>(rows.size());
		for (Message m : rows) {
			list.add(toListItem(m, byMsg.get(m.getId())));
		}
		res.setList(list);
		return res;
	}

	private SystemMessageListItemVO toListItem(Message m, List<MessageTemplate> tpls) {
		SystemMessageListItemVO vo = new SystemMessageListItemVO();
		BeanUtil.copyProperties(m, vo);
		vo.setTemplateVar(parseTemplateVar(m.getTemplateVar()));
		List<MessageTemplate> ordered = tpls == null ? List.of()
				: tpls.stream().sorted((a, b) -> Integer.compare(nz(a.getType()), nz(b.getType()))).toList();
		List<MessageTemplateJsonVO> jsonList = new ArrayList<>();
		for (MessageTemplate t : ordered) {
			MessageTemplateJsonVO j = toTplJson(t);
			jsonList.add(j);
			int ty = nz(t.getType());
			switch (ty) {
				case TYPE_SYSTEM:
					vo.setSystemTemplate(j);
					break;
				case TYPE_SMS:
					vo.setSmsTemplate(j);
					break;
				case TYPE_WORK:
					vo.setWorkTemplate(j);
					break;
				case TYPE_DING:
					vo.setDingTemplate(j);
					break;
				case TYPE_OTHER:
					vo.setOtherTemplate(j);
					break;
				default:
			}
		}
		vo.setMessageTemplate(jsonList);
		return vo;
	}

	private static int nz(Integer x) {
		return ObjectUtil.defaultIfNull(x, -1);
	}

	private static MessageTemplateJsonVO toTplJson(MessageTemplate t) {
		MessageTemplateJsonVO j = new MessageTemplateJsonVO();
		BeanUtil.copyProperties(t, j);
		return j;
	}

	private Object parseTemplateVar(String raw) {
		if (StrUtil.isBlank(raw)) {
			return "";
		}
		String s = raw.trim();
		try {
			JsonNode n = objectMapper.readTree(s);
			if (n.isMissingNode() || n.isNull()) {
				return "";
			}
			return n;
		}
		catch (Exception ignored) {
			return raw;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMessage(long id, JsonNode body) {
		Message msg = baseMapper.selectById(id);
		if (ObjectUtil.isNull(msg)) {
			throw new IllegalArgumentException("消息不存在");
		}
		String remind = bodyText(body, "remind_time");
		if (StrUtil.isNotBlank(remind)) {
			msg.setRemindTime(remind);
			msg.setUpdatedAt(LocalDateTime.now());
			baseMapper.updateById(msg);
			return;
		}
		String contentTemplate = StrUtil.nullToEmpty(msg.getContent());
		MessageTemplate smsRow = getTemplate(id, TYPE_SMS);
		String urlFb = smsRow == null ? "" : StrUtil.nullToEmpty(smsRow.getUrl());
		String uniFb = smsRow == null ? "" : StrUtil.nullToEmpty(smsRow.getUniUrl());
		applySystemTemplateStatus(id, body);
		applySmsTemplate(id, body, contentTemplate, urlFb, uniFb);
		applyWebhookTemplate(id, body, TYPE_WORK, "work_status", "work_webhook_url", contentTemplate, urlFb, uniFb);
		applyWebhookTemplate(id, body, TYPE_DING, "ding_status", "ding_webhook_url", contentTemplate, urlFb, uniFb);
		applyWebhookTemplate(id, body, TYPE_OTHER, "other_status", "other_webhook_url", contentTemplate, urlFb, uniFb);
	}

	private void applySystemTemplateStatus(long messageId, JsonNode body) {
		Integer st = readOptionalInt(body, "status");
		if (ObjectUtil.isNull(st)) {
			return;
		}
		MessageTemplate t = getTemplate(messageId, TYPE_SYSTEM);
		if (ObjectUtil.isNull(t)) {
			return;
		}
		t.setStatus(st);
		touchTemplate(t);
	}

	private void applySmsTemplate(long messageId, JsonNode body, String contentTemplate, String urlFb, String uniFb) {
		Integer smsSt = readOptionalInt(body, "sms_status");
		String templateId = bodyText(body, "template_id");
		if (ObjectUtil.isNull(smsSt) && StrUtil.isBlank(templateId)) {
			return;
		}
		MessageTemplate t = getTemplate(messageId, TYPE_SMS);
		if (ObjectUtil.isNotNull(t)) {
			if (ObjectUtil.isNotNull(smsSt)) {
				t.setStatus(smsSt);
			}
			if (StrUtil.isNotBlank(templateId)) {
				t.setTemplateId(templateId);
			}
			touchTemplate(t);
			return;
		}
		MessageTemplate n = newTemplateRow(messageId, TYPE_SMS, contentTemplate, urlFb, uniFb);
		if (ObjectUtil.isNotNull(smsSt)) {
			n.setStatus(smsSt);
		}
		n.setTemplateId(StrUtil.nullToEmpty(templateId));
		messageTemplateMapper.insert(n);
	}

	private void applyWebhookTemplate(long messageId, JsonNode body, int type, String statusField, String urlField,
			String contentTemplate, String urlFb, String uniFb) {
		Integer st = readOptionalInt(body, statusField);
		String url = bodyText(body, urlField);
		if (ObjectUtil.isNull(st) && StrUtil.isBlank(url)) {
			return;
		}
		MessageTemplate t = getTemplate(messageId, type);
		if (ObjectUtil.isNotNull(t)) {
			if (ObjectUtil.isNotNull(st)) {
				t.setStatus(st);
			}
			if (StrUtil.isNotBlank(url)) {
				t.setWebhookUrl(url);
			}
			touchTemplate(t);
			return;
		}
		MessageTemplate n = newTemplateRow(messageId, type, contentTemplate, urlFb, uniFb);
		if (ObjectUtil.isNotNull(st)) {
			n.setStatus(st);
		}
		n.setWebhookUrl(StrUtil.nullToEmpty(url));
		messageTemplateMapper.insert(n);
	}

	private MessageTemplate getTemplate(long messageId, int type) {
		return messageTemplateMapper.selectOne(Wrappers.lambdaQuery(MessageTemplate.class)
			.eq(MessageTemplate::getMessageId, messageId)
			.eq(MessageTemplate::getType, type)
			.isNull(MessageTemplate::getDeletedAt)
			.last("LIMIT 1"));
	}

	private static MessageTemplate newTemplateRow(long messageId, int type, String contentTemplate, String url,
			String uniUrl) {
		MessageTemplate t = new MessageTemplate();
		t.setMessageId(messageId);
		t.setType(type);
		t.setContentTemplate(contentTemplate);
		t.setUrl(StrUtil.nullToEmpty(url));
		t.setUniUrl(StrUtil.nullToEmpty(uniUrl));
		t.setRelationStatus(1);
		t.setStatus(0);
		LocalDateTime now = LocalDateTime.now();
		t.setCreatedAt(now);
		t.setUpdatedAt(now);
		return t;
	}

	private void touchTemplate(MessageTemplate t) {
		t.setUpdatedAt(LocalDateTime.now());
		messageTemplateMapper.updateById(t);
	}

	private static Integer readOptionalInt(JsonNode body, String key) {
		if (ObjectUtil.isNull(body) || !body.has(key)) {
			return null;
		}
		JsonNode n = body.get(key);
		if (n.isNull()) {
			return null;
		}
		if (n.isIntegralNumber()) {
			return n.intValue();
		}
		String s = n.asText("").trim();
		if (StrUtil.isBlank(s)) {
			return null;
		}
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

	private static String bodyText(JsonNode body, String field) {
		if (ObjectUtil.isNull(body) || !body.has(field) || body.get(field).isNull()) {
			return "";
		}
		return body.get(field).asText("");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateChannelStatus(long messageId, int type, int status) {
		Message msg = baseMapper.selectById(messageId);
		if (ObjectUtil.isNull(msg)) {
			throw new IllegalArgumentException("消息信息获取失败");
		}
		MessageTemplate info = getTemplate(messageId, type);
		if (ObjectUtil.isNull(info)) {
			info = newTemplateRow(messageId, type, StrUtil.nullToEmpty(msg.getContent()), "", "");
			info.setMessageTitle(msg.getTitle());
			info.setRelationStatus(1);
			messageTemplateMapper.insert(info);
		}
		if (ObjectUtil.defaultIfNull(info.getRelationStatus(), 0) == 0
				&& ObjectUtil.defaultIfNull(info.getCrudEventId(), 0) == 0) {
			throw new IllegalArgumentException("平台消息已被关闭，无法修改消息状态");
		}
		info.setStatus(status);
		touchTemplate(info);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setUserSubscribe(long messageId, int status) {
		Message m = baseMapper.selectById(messageId);
		if (ObjectUtil.isNull(m)) {
			throw new IllegalArgumentException("消息不存在");
		}
		m.setUserSub(status);
		m.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(m);
	}

	@Override
	public void syncMessage(long entId) {
		// 未对接 crmeb 远程消息中心；与 PHP 成功响应保持一致，不做数据变更。
	}

	@Override
	public List<MessageCategoryCountVO> getMessageCateCount(long entId, long adminId, String uuid) {
		List<MessageCategory> cats = messageCategoryMapper.selectList(Wrappers.lambdaQuery(MessageCategory.class)
			.orderByAsc(MessageCategory::getSort)
			.orderByAsc(MessageCategory::getId));
		List<Map<String, Object>> grouped = enterpriseMessageNoticeService.countUnreadByCate(entId, adminId, uuid);
		Map<Long, Long> countByCate = new HashMap<>();
		for (Map<String, Object> row : grouped) {
			Object cid = row.get("cate_id");
			Object cnt = row.get("cnt");
			if (ObjectUtil.isNull(cid) || ObjectUtil.isNull(cnt)) {
				continue;
			}
			long key = ((Number) cid).longValue();
			long n = ((Number) cnt).longValue();
			countByCate.put(key, n);
		}
		List<MessageCategoryCountVO> out = new ArrayList<>();
		for (MessageCategory c : cats) {
			MessageCategoryCountVO v = new MessageCategoryCountVO();
			BeanUtil.copyProperties(c, v);
			v.setLabel(c.getCateName());
			v.setValue(c.getId());
			v.setCount(countByCate.getOrDefault(c.getId(), 0L));
			out.add(v);
		}
		return out;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Message req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Message req) {
		return super.update(req);
	}

}
