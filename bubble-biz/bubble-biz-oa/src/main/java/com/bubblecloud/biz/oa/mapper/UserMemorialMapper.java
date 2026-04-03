package com.bubblecloud.biz.oa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.UserMemorial;

/**
 * eb_user_memorial Mapper。
 *
 * @author qinlei
 * @date 2026/4/2 10:00
 */
@Mapper
public interface UserMemorialMapper extends UpMapper<UserMemorial> {

	/**
	 * 按用户与月份查询备忘录（用于分组列表）。
	 */
	List<UserMemorial> selectByUidAndMonth(@Param("uid") String uid, @Param("ym") String ym, @Param("pid") Integer pid,
			@Param("titleSearch") String titleSearch);

	/**
	 * 去重月份分页（yyyy-MM），降序。
	 */
	List<String> selectMonthsPage(@Param("uid") String uid, @Param("pid") Integer pid,
			@Param("titleSearch") String titleSearch, @Param("offset") long offset, @Param("limit") int limit);

	/**
	 * 去重月份总数。
	 */
	long countDistinctMonth(@Param("uid") String uid, @Param("pid") Integer pid,
			@Param("titleSearch") String titleSearch);

}
