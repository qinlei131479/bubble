package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.BillCategory;

/**
 * eb_bill_category Mapper。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Mapper
public interface BillCategoryMapper extends UpMapper<BillCategory> {

}
