package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.SystemQuick;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;

/**
 * 快捷入口（eb_system_quick）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface SystemQuickService extends UpService<SystemQuick> {

	OaElFormVO buildCreateForm(long entId, Integer defaultCid);

	OaElFormVO buildEditForm(long entId, int id);

	/**
	 * 对齐 PHP Resource {@code show}：更新 {@code status} 字段。
	 */
	void updateShowStatus(int id, int status);

}
