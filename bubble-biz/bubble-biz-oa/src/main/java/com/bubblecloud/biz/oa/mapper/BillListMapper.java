package com.bubblecloud.biz.oa.mapper;

import java.math.BigDecimal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.dto.finance.BillListFinanceQuery;
import com.bubblecloud.oa.api.entity.BillList;

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

}
