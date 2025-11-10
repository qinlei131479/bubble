package com.bubblecloud.biz.backend.controller;

import cn.hutool.core.lang.tree.Tree;
import com.bubblecloud.api.backend.entity.SysDept;
import com.bubblecloud.api.backend.vo.DeptExcelVO;
import com.bubblecloud.biz.backend.service.SysDeptService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 部门管理 前端控制器
 * </p>
 *
 * @author lengleng
 * @since 2018-01-20
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dept")
@Tag(description = "dept", name = "部门管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysDeptController {

	private final SysDeptService sysDeptService;

	/**
	 * 通过ID查询
	 *
	 * @param id ID
	 * @return SysDept
	 */
	@Operation(summary = "通过ID查询", description = "")
	@GetMapping("/{id}")
	public R<SysDept> getById(@PathVariable Long id) {
		return R.ok(sysDeptService.getById(id));
	}

	/**
	 * 查询全部部门
	 */
	@Operation(summary = "分页查询", description = "分页查询说明")
	@GetMapping("/list")
	public R<List<SysDept>> list() {
		return R.ok(sysDeptService.list());
	}

	/**
	 * 返回树形菜单集合
	 *
	 * @param deptName 部门名称
	 * @return 树形菜单
	 */
	@Operation(summary = "树形结构查询", description = "")
	@GetMapping(value = "/tree")
	public R<List<Tree<Long>>> getTree(String deptName) {
		return R.ok(sysDeptService.selectTree(deptName));
	}

	/**
	 * 添加
	 *
	 * @param sysDept 实体
	 * @return success/false
	 */
	@Operation(summary = "添加部门", description = "")
	@SysLog("添加部门")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_dept_add')")
	public R add(@Valid @RequestBody SysDept sysDept) {
		return R.ok(sysDeptService.save(sysDept));
	}

	/**
	 * 删除
	 *
	 * @param id ID
	 * @return success/false
	 */
	@Operation(summary = "删除部门", description = "")
	@SysLog("删除部门")
	@DeleteMapping("/{id}")
	@PreAuthorize("@pms.hasPermission('sys_dept_del')")
	public R removeById(@PathVariable Long id) {
		return R.ok(sysDeptService.removeDeptById(id));
	}

	/**
	 * 编辑
	 *
	 * @param sysDept 实体
	 * @return success/false
	 */
	@Operation(summary = "编辑部门", description = "")
	@SysLog("编辑部门")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('sys_dept_edit')")
	public R update(@Valid @RequestBody SysDept sysDept) {
		sysDept.setUpdateTime(LocalDateTime.now());
		return R.ok(sysDeptService.updateById(sysDept));
	}

	/**
	 * 查询子级列表
	 *
	 * @return 返回子级
	 */
	@Operation(summary = "查询子级列表", description = "")
	@GetMapping(value = "/getDescendantList/{deptId}")
	public R<List<SysDept>> getDescendantList(@PathVariable Long deptId) {
		return R.ok(sysDeptService.listDescendant(deptId));
	}

	/**
	 * 导出部门
	 *
	 * @return
	 */
	@Operation(summary = "导出部门", description = "")
	@ResponseExcel
	@GetMapping("/export")
	public List<DeptExcelVO> export() {
		return sysDeptService.listExcelVo();
	}

	/**
	 * 导入部门
	 *
	 * @param excelVOList
	 * @param bindingResult
	 * @return
	 */
	@Operation(summary = "导入部门", description = "")
	@PostMapping("import")
	public R importDept(@RequestExcel List<DeptExcelVO> excelVOList, BindingResult bindingResult) {

		return sysDeptService.importDept(excelVOList, bindingResult);
	}

}
