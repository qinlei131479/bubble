package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.EnterprisePaytype;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;

/**
 * 企业支付方式服务。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
public interface EnterprisePaytypeService extends UpService<EnterprisePaytype> {

	/** 对齐 PHP {@code PaytypeService::resourceCreate} elForm */
	OaElFormVO buildCreateForm();

}
