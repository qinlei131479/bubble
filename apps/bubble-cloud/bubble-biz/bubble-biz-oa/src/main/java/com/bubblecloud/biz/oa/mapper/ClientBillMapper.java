package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.ClientBill;

/**
 * eb_client_bill Mapper。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Mapper
public interface ClientBillMapper extends UpMapper<ClientBill> {

	Page<ClientBill> selectUnInvoicedPage(Page<ClientBill> page, @Param("entid") int entid, @Param("eid") int eid,
			@Param("invoiceId") Integer invoiceId);

}
