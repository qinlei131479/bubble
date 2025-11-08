package com.bubblecloud.common.mybatis.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bubblecloud.common.core.entity.Req;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.mapper.UpMapper;

import java.util.List;

/**
 * 管理员service
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
public interface UpService<T> extends IService<T> {
	/**
	 * 检查字段重复 -- 创建模式
	 * 
	 * @param req：请求参数
	 * @param fieldAndNames：字段名称
	 * @return
	 */
	R checkFieldRepeatCreate(T req, String... fieldAndNames);

	/**
	 * 检查字段重复 -- 创建模式
	 * 
	 * @param req
	 * @param eqNameList
	 * @param fieldAndNames
	 * @return
	 */
	R checkFieldRepeatCreate(T req, List<String> eqNameList, String... fieldAndNames);

	/**
	 * 检查字段重复 -- 修改模式
	 * 
	 * @param req
	 * @param fieldAndNames
	 * @return
	 */
	R checkFieldRepeatUpdate(T req, String... fieldAndNames);

	/**
	 * 检查字段重复 -- 修改模式
	 * 
	 * @param req
	 * @param eqNameList
	 * @param fieldAndNames
	 * @return
	 */
	R checkFieldRepeatUpdate(T req, List<String> eqNameList, String... fieldAndNames);

	/**
	 * 检查字段重复（包含eq相等字段）
	 * 
	 * @param req
	 * @param isCreate
	 * @param eqList
	 * @param fieldAndNames：字段名称对，使用冒号分割
	 * @return
	 */
	R checkFieldRepeat(T req, boolean isCreate, List<String> eqList, String... fieldAndNames);

	/**
	 * 分页查询
	 * 
	 * @param page
	 * @param req
	 * @return
	 */
	Page<T> findPg(Page page, T req);

	/**
	 * 列表查询，不支持排序和条数限制
	 *
	 * @param req：查询条件
	 * @return
	 */
	List<T> findList(T req);

	/**
	 * 列表查询，支持排序和条数限制，不查询总数
	 *
	 * @param page：排序和条数限制
	 * @param req：查询条件
	 * @return
	 */
	List<T> findList(Page page, T req);

	/**
	 * 修改
	 *
	 * @param req
	 * @return
	 */
	R update(T req);

	/**
	 * 修改
	 * 
	 * @param mapper
	 * @param req
	 * @return
	 */
	R update(UpMapper mapper, Req req);

	/**
	 * 添加
	 *
	 * @param req
	 * @return
	 */
	R create(T req);

	/**
	 * 添加，返回创建对象
	 *
	 * @param mapper
	 * @param req
	 * @return
	 */
	R create(UpMapper mapper, Req req);

	/**
	 * 添加，不返回创建对象，直接写入（不自动生成id）
	 * 
	 * @param req
	 * @return
	 */
	R insert(T req);

	/**
	 * 添加，不返回创建对象
	 *
	 * @param mapper
	 * @param req
	 * @return
	 */
	void createVoid(UpMapper mapper, Req req);

	/**
	 * 删除
	 *
	 * @param req：请求参数
	 * @return
	 */
	R delete(T req);

	/**
	 * 删除
	 *
	 * @param id
	 * @return
	 */
	R deleteById(Long id);
}
