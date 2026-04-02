package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.MessageMapper;
import com.bubblecloud.biz.oa.service.MessageService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Message;
import org.springframework.stereotype.Service;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统消息配置实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
public class MessageServiceImpl extends UpServiceImpl<MessageMapper, Message> implements MessageService {

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
