package com.bubblecloud.biz.oa.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.oa.api.vo.crm.CrmBillRankRowVO;
import com.bubblecloud.oa.api.vo.crm.CrmContractCategoryAggVO;
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

	BigDecimal sumBillJoinContract(@Param("entid") int entid, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end, @Param("uids") List<Integer> uids, @Param("types") List<Integer> types,
			@Param("categories") List<String> categories);

	int countCustomerCreatedBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
			@Param("uids") List<Integer> uids);

	int countContractByStartDateBetween(@Param("start") java.time.LocalDate start,
			@Param("end") java.time.LocalDate end, @Param("uids") List<Integer> uids,
			@Param("contractIds") List<Long> contractIds);

	BigDecimal sumContractPriceByStartDateBetween(@Param("start") java.time.LocalDate start,
			@Param("end") java.time.LocalDate end, @Param("uids") List<Integer> uids,
			@Param("contractIds") List<Long> contractIds);

	BigDecimal sumContractSurplusUncollected(@Param("uids") List<Integer> uids,
			@Param("contractIds") List<Long> contractIds);

	List<NamedCountVO> billTrendGrouped(@Param("entid") int entid, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end, @Param("uids") List<Integer> uids, @Param("types") List<Integer> types,
			@Param("categories") List<String> categories, @Param("dateFormat") String dateFormat);

	List<CrmBillRankRowVO> billRankByUid(@Param("entid") int entid, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end, @Param("uids") List<Integer> uids,
			@Param("categories") List<String> categories, @Param("offset") int offset, @Param("limit") int limit);

	int countBillRankGroups(@Param("entid") int entid, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end, @Param("uids") List<Integer> uids,
			@Param("categories") List<String> categories);

	List<CrmContractCategoryAggVO> contractCategoryAgg(@Param("entid") int entid, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end, @Param("uids") List<Integer> uids,
			@Param("categories") List<String> categories);

	List<Long> selectContractIdsByCategories(@Param("categories") List<String> categories);

}
