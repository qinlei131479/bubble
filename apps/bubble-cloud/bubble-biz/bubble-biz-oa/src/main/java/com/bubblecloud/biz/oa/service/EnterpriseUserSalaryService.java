package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.EnterpriseUserSalary;

/**
 * 调薪记录。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
public interface EnterpriseUserSalaryService extends UpService<EnterpriseUserSalary> {

	/**
	 * 某名片最近一条调薪记录（对齐 PHP {@code last/{card_id}}）。
	 */
	List<EnterpriseUserSalary> listLastByCardId(Long cardId);

}
