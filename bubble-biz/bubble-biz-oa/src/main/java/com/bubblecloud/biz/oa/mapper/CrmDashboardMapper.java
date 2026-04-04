package com.bubblecloud.biz.oa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.oa.api.vo.crm.NamedCountVO;

/**
 * CRM 统计看板 SQL（客户趋势、合同分类、业务员排行等）。
 *
 * @author qinlei
 * @date 2026/4/3 12:00
 */
@Mapper
public interface CrmDashboardMapper {

	List<NamedCountVO> customerCreatedByMonth();

	List<NamedCountVO> contractCategoryRank();

	List<NamedCountVO> customerCountBySalesman();

	List<NamedCountVO> billIncomeByMonth();

}
