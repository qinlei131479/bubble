package com.bubblecloud.common.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.core.entity.Req;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author Rampart Qin
 * @date Created in 2022/4/16 3:09 下午
 */
@Mapper
public interface UpMapper<T extends Req> extends BaseMapper<T> {
	/**
	 * 分页查询
	 * 
	 * @param page：分页参数
	 * @param req：请求参数
	 * @return 返回分页数据
	 */
	Page<T> findPg(Page page, @Param("query") T req);

	/**
	 * 更新(使用自定义sql，id命名为updateCustom，避免与mysqlplus的命名冲突）
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(T req);

	/**
	 * 更新逻辑删除的删除时间
	 *
	 * @param req
	 * @return
	 */
	int updateDeleteTime(T req);

	/**
	 * 插入或更新
	 *
	 * @param req
	 * @return
	 */
	int createUpdate(T req);

	/**
	 * 查询列表，不支持排序和条数限制
	 *
	 * @param req：查询条件
	 * @return
	 */
	List<T> findList(@Param("query") T req);

	/**
	 * 列表查询，支持排序和条数限制，不查询总数
	 *
	 * @param page：排序和条数限制
	 * @param req：查询条件
	 * @return
	 */
	List<T> findList(Page page, @Param("query") T req);
}
