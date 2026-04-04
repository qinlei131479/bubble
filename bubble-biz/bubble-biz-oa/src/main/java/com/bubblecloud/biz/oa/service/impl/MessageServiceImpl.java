package com.bubblecloud.biz.oa.service.impl;

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
		List<MessageTemplate> ordered = tpls == null ? List.of() : tpls.stream()
			.sorted((a, b) -> Integer.compare(nz(a.getType()), nz(b.getType())))
			.toList();
		List<MessageTemplateJsonVO> jsonList = new ArrayList<>();
		for (MessageTemplate t : ordered) {
			MessageTemplateJsonVO j = toTplJson(t);
			jsonList.add(j);
			int ty = nz(t.getType());
			switch (ty) {
				case TYPE_SYSTEM :
					vo.setSystemTemplate(j);
					break;
				case TYPE_SMS :
					vo.setSmsTemplate(j);
					break;
				case TYPE_WORK :
					vo.setWorkTemplate(j);
					break;
				case TYPE_DING :
					vo.setDingTemplate(j);
					break;
				case TYPE_OTHER :
					vo.setOtherTemplate(j);
					break;
				default :
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
	public List<MessageCategoryCountVO> getMessageCateCount(long entId, long adminId, String uuid) {
		List<MessageCategory> cats = messageCategoryMapper
			.selectList(Wrappers.lambdaQuery(MessageCategory.class)
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
