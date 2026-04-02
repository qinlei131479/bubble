package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.EnterpriseMessageNotice;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;

/**
 * 公共消息列表（对齐 PHP NoticeRecordService::getMessageList）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
public interface MessageService extends UpService<EnterpriseMessageNotice> {

	CommonMessageVO getMessageList(Long adminId, String uid, Integer entid, Integer page, Integer limit, String cateId,
			String title);

	void updateMessageRead(Long adminId, String uid, Long messageId, Integer isRead);

}
