package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.dto.CompanyUpdateDTO;
import com.bubblecloud.oa.api.vo.company.CompanyEntInfoVO;
import com.bubblecloud.oa.api.vo.company.CompanyQuantityVO;

/**
 * 企业管理（对齐 PHP {@code CompanyController} / {@code CompanyService}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
public interface CompanyService extends UpService<Enterprise> {

	CompanyEntInfoVO getEntAndUserInfo(Integer entId);

	boolean updateEnt(Integer entId, CompanyUpdateDTO dto);

	CompanyQuantityVO getQuantity(String type, Integer entId);

}
