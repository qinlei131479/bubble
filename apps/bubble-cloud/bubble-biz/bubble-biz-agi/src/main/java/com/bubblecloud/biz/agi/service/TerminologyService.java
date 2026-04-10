package com.bubblecloud.biz.agi.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.agi.api.entity.Terminology;

/**
 * service接口：术语表
 *
 * @author Rampart Qin
 * @date 2026/02/11 22:35
 */
public interface TerminologyService extends UpService<Terminology> {

	/**
	 * 填充术语内容（同义词、数据源）
	 *
	 * @param req 术语对象
	 * @return 术语
	 */
	Terminology fill(Terminology req);
}
