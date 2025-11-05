package com.pig4cloud.pig.admin.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.admin.api.entity.SysDept;
import com.pig4cloud.pig.admin.api.vo.DeptExcelVo;
import com.pig4cloud.pig.common.core.util.R;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * 部门管理服务接口
 *
 * @author lengleng
 * @since 2018-01-20
 */
public interface SysDeptService extends IService<SysDept> {

	/**
	 * 查询部门树菜单
	 * @param deptName 部门名称
	 * @return 部门树结构
	 */
	List<Tree<Long>> getDeptTree(String deptName);

	/**
	 * 根据部门ID删除部门
	 * @param id 要删除的部门ID
	 * @return 删除操作是否成功，成功返回true，失败返回false
	 */
	Boolean removeDeptById(Long id);

	/**
	 * 导出部门Excel数据列表
	 * @return 部门Excel数据列表
	 */
	List<DeptExcelVo> exportDepts();

	/**
	 * 导入部门数据
	 * @param excelVOList 部门Excel数据列表
	 * @param bindingResult 数据校验结果
	 * @return 导入结果
	 */
	R importDept(List<DeptExcelVo> excelVOList, BindingResult bindingResult);

	/**
	 * 获取指定部门的所有后代部门列表
	 * @param deptId 部门ID
	 * @return 后代部门列表，如果不存在则返回空列表
	 */
	List<SysDept> listDescendants(Long deptId);

}
