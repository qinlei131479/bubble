package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.MessageMapper;
import com.bubblecloud.biz.oa.service.SystemMessageAdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Message;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 系统消息配置实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
public class SystemMessageAdminServiceImpl extends UpServiceImpl<MessageMapper, Message> implements SystemMessageAdminService {

	@Override
	public Page<Message> pageList(Integer cateId, String title, int entid, int page, int limit) {
		var q = Wrappers.lambdaQuery(Message.class).eq(Message::getEntid, entid).orderByDesc(Message::getId);
		if (ObjectUtil.isNotNull(cateId) && cateId > 0) {
			q.eq(Message::getCateId, cateId);
		}
		if (StrUtil.isNotBlank(title)) {
			q.like(Message::getTitle, title);
		}
		return baseMapper.selectPage(new Page<>(page, limit), q);
	}

	@Override
	public Message getById(long id) {
		return super.getById(id);
	}

}
