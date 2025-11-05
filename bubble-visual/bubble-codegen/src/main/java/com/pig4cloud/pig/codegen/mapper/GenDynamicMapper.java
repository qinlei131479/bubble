package com.pig4cloud.pig.codegen.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 动态查询Mapper接口
 *
 * @author qinlei
 * @date 2025/05/31
 */
@Mapper
public interface GenDynamicMapper {

	/**
	 * 动态SQL查询
	 * @param sq SQL查询语句
	 * @return 查询结果列表，每个结果以LinkedHashMap形式存储
	 */
	@InterceptorIgnore(tenantLine = "true")
	List<LinkedHashMap<String, Object>> dynamicQuerySql(@Param("value") String sq);

}
