package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.ClientRemind;

/**
 * 付款/续费提醒（对齐 PHP {@code ClientRemindService} 核心表操作）。
 *
 * @author qinlei
 * @date 2026/4/3 14:00
 */
public interface ClientRemindCrmService extends UpService<ClientRemind> {

	/**
	 * 未删除的记录；不存在返回 null。
	 */
	ClientRemind getActiveById(long id);

	void setMark(long id, String mark);

	void abjure(long id);

}
