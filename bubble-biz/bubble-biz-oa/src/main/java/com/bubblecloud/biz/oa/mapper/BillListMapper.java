package com.bubblecloud.biz.oa.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.dto.finance.BillListFinanceQuery;
import com.bubblecloud.oa.api.entity.BillList;
import com.bubblecloud.oa.api.vo.finance.BillTrendRowVO;

/**
 * eb_bill_list Mapper。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Mapper
public interface BillListMapper extends UpMapper<BillList> {

	Page<BillList> selectFinancePage(Page<BillList> page, @Param("q") BillListFinanceQuery q);

	BigDecimal sumNum(@Param("q") BillListFinanceQuery q, @Param("types") int types);

	/**
	 * 时间区间内、按类型与分类子树过滤的金额合计（对齐 PHP {@code BillDao::getSum} 核心条件）。
	 */
	BigDecimal sumNumInRange(@Param("entid") long entid, @Param("types") int types, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end, @Param("cateIds") List<Integer> cateIds);

	/**
	 * 按 DATE_FORMAT 分组聚合（对齐 PHP {@code BillDao::getTrend}）。
	 */
	List<BillTrendRowVO> selectTrendAgg(@Param("entid") long entid, @Param("types") int types,
			@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
			@Param("cateIds") List<Integer> cateIds, @Param("pattern") String pattern);

	/**
	 * 某分类子树下金额合计（用于排行单行）。
	 */
	BigDecimal sumNumSubtree(@Param("entid") long entid, @Param("types") int types, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end, @Param("cateIds") List<Integer> cateIds);

}
