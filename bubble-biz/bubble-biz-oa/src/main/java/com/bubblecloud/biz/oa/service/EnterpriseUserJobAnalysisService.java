package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.EnterpriseUserJobAnalysis;

/**
 * 企业员工工作分析表 CRUD。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
public interface EnterpriseUserJobAnalysisService extends UpService<EnterpriseUserJobAnalysis> {

	/**
	 * 按企业与员工 admin 主键写入或更新分析内容（对齐 PHP resourceUpdate）。
	 */
	void upsertByEntAndUser(long entid, long adminId, String rawData);

}
