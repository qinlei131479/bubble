package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseEntInfoVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseQuantityVO;

/**
 * 企业管理（对齐 PHP {@code CompanyController} / {@code CompanyService}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
public interface EnterpriseService extends UpService<Enterprise> {

	EnterpriseEntInfoVO getEntAndUserInfo(Long entId);

	EnterpriseQuantityVO getQuantity(String type, Long entId);

}
