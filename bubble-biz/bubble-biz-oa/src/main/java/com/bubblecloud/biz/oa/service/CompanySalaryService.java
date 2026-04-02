package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.CompanySalarySaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserSalary;

/**
 * 调薪记录。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
public interface CompanySalaryService extends UpService<EnterpriseUserSalary> {

	Page<EnterpriseUserSalary> pageSalary(Integer entid, Integer cardId, Integer linkId, Long id,
			Page<EnterpriseUserSalary> page);

	EnterpriseUserSalary getForEdit(Long id);

	boolean saveSalary(CompanySalarySaveDTO dto);

	boolean updateSalary(Long id, CompanySalarySaveDTO dto);

	boolean removeSalary(Long id);

	List<EnterpriseUserSalary> lastByCardId(Integer cardId);

}
