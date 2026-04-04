package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Message;
import com.bubblecloud.oa.api.vo.message.MessageCategoryCountVO;
import com.bubblecloud.oa.api.vo.message.SystemMessageListResultVO;

/**
 * 系统消息配置（eb_message），对齐 PHP ent/system/message。
 *
 * @author qinlei
 * @date 2026/3/31
 */
public interface MessageService extends UpService<Message> {

	/**
	 * 对齐 PHP {@code MessageController::index} / {@code MessageService::getList}。
	 */
	SystemMessageListResultVO getSystemMessageList(long entid, int page, int limit, Long cateId, String title);

	/**
	 * 对齐 PHP {@code MessageController::cate} / {@code getMessageCateCount}。
	 */
	List<MessageCategoryCountVO> getMessageCateCount(long entId, long adminId, String uuid);

}
