package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.MessageMapper;
import com.bubblecloud.biz.oa.service.SystemMessageAdminService;
import com.bubblecloud.oa.api.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统消息配置实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
@RequiredArgsConstructor
public class SystemMessageAdminServiceImpl implements SystemMessageAdminService {

	private final MessageMapper messageMapper;

	@Override
	public Page<Message> pageList(Integer cateId, String title, int entid, int page, int limit) {
		var q = Wrappers.lambdaQuery(Message.class).eq(Message::getEntid, entid).orderByDesc(Message::getId);
		if (cateId != null && cateId > 0) {
			q.eq(Message::getCateId, cateId);
		}
		if (StringUtils.hasText(title)) {
			q.like(Message::getTitle, title);
		}
		return messageMapper.selectPage(new Page<>(page, limit), q);
	}

	@Override
	public Message getById(long id) {
		return messageMapper.selectById(id);
	}

}
