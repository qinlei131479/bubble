package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.vo.ScanKeyVO;
import com.bubblecloud.oa.api.vo.ScanStatusResultVO;

/**
 * 扫码登录 Redis 状态（对齐 PHP AdminService::scanCodeKey / keyStatus）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
public interface ScanLoginService extends UpService<Admin> {

	ScanKeyVO createScanKey();

	ScanStatusResultVO pollStatus(String key);

	void bindScanUserId(String key, long userId);

}
