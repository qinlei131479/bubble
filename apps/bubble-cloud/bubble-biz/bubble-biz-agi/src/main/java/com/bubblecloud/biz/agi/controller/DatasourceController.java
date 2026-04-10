package com.bubblecloud.biz.agi.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.agi.api.entity.DatasourceTable;
import com.bubblecloud.agi.api.entity.DatasourceTableField;
import com.bubblecloud.biz.agi.service.DatasourceTableFieldService;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.base.Req;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.bubblecloud.agi.api.entity.Datasource;
import com.bubblecloud.agi.api.dto.DatasourceTestDTO;
import com.bubblecloud.agi.api.vo.DatasourceTestResultVO;
import com.bubblecloud.agi.api.vo.TableInfoVO;
import com.bubblecloud.biz.agi.service.DatasourceService;
import com.bubblecloud.biz.agi.service.DatasourceTableService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.bubblecloud.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据源
 *
 * @author Rampart
 * @date 2026-02-13 16:45:56
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/datasource")
@Tag(description = "datasource", name = "数据源管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class DatasourceController {

	private final DatasourceService datasourceService;
	private final DatasourceTableService datasourceTableService;
	private final DatasourceTableFieldService datasourceTableFieldService;

	/**
	 * 分页查询
	 *
	 * @param pg  分页对象
	 * @param req 数据源
	 * @return
	 */
	@Operation(summary = "分页查询", description = "分页查询")
	@GetMapping("/page")
	@HasPermission("agi_datasource_view")
	public R<Page<Datasource>> page(@ParameterObject Pg pg, @ParameterObject Datasource req) {
		pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(datasourceService.findPg(pg, req));
	}


	/**
	 * 通过条件查询数据源
	 *
	 * @param req 查询条件
	 * @return R  对象列表
	 */
	@Operation(summary = "通过条件查询", description = "通过条件查询对象")
	@GetMapping("/details")
//	@HasPermission("agi_datasource_view")
	public R<List<Datasource>> details(@ParameterObject Datasource req) {
		List<Datasource> list = datasourceService.list(Wrappers.query(req));
		if (CollUtil.isNotEmpty(list)) {
			list.forEach(datasource -> datasource.setTableNames(datasourceTableService.list(Wrappers.lambdaQuery(DatasourceTable.class)
							.eq(DatasourceTable::getDsId, datasource.getId())
							.select(DatasourceTable::getTableName))
					.stream()
					.map(DatasourceTable::getTableName)
					.toList()));
		}
		return R.ok(list);
	}

	/**
	 * 新增数据源
	 *
	 * @param req 数据源
	 * @return R
	 */
	@Operation(summary = "新增数据源", description = "新增数据源")
	@SysLog("新增数据源")
	@PostMapping
	@HasPermission("agi_datasource_add")
	public R<Datasource> create(@RequestBody @Validated({Req.Create.class}) Datasource req) {
		// 使用insert方法确保ID被正确设置
		datasourceService.insert(req);

		// 同步表结构
		if (req.getTableNames() != null && !req.getTableNames().isEmpty()) {
			datasourceService.syncTables(req.getId(), req.getTableNames());
		}

		// 返回新创建的数据源（包含ID）
		return R.ok(req);
	}

	/**
	 * 修改数据源
	 *
	 * @param req 数据源
	 * @return R
	 */
	@Operation(summary = "修改数据源", description = "修改数据源")
	@SysLog("修改数据源")
	@PutMapping
	@HasPermission("agi_datasource_edit")
	public R update(@RequestBody @Validated({Req.Update.class}) Datasource req) {
		datasourceService.update(req);

		// 编辑模式下，同步表结构（先删除旧的表结构和字段，再新增）
		if (req.getTableNames() != null && !req.getTableNames().isEmpty()) {
			datasourceService.syncTables(req.getId(), req.getTableNames());
		}

		return R.ok(true);
	}

	/**
	 * 通过id删除数据源
	 *
	 * @param ids id列表
	 * @return R
	 */
	@Operation(summary = "通过id删除数据源", description = "通过id删除数据源")
	@SysLog("通过id删除数据源")
	@DeleteMapping
	@HasPermission("agi_datasource_del")
	public R removeById(@RequestBody Long[] ids) {
		return R.ok(datasourceService.removeBatchByIds(CollUtil.toList(ids)));
	}

	/**
	 * 测试数据源连接
	 *
	 * @param dto 测试连接参数
	 * @return R 测试结果
	 */
	@Operation(summary = "测试数据源连接", description = "测试数据源连接")
	@PostMapping("/test")
	public R<DatasourceTestResultVO> test(@RequestBody DatasourceTestDTO dto) {
		return R.ok(datasourceService.testConnection(dto));
	}

	/**
	 * 获取数据库表列表
	 *
	 * @param dto 数据源配置
	 * @return R 表名列表
	 */
	@Operation(summary = "获取数据库表列表", description = "获取数据库表列表")
	@PostMapping("/tables")
	public R<List<TableInfoVO>> getTables(@RequestBody DatasourceTestDTO dto) {
		return R.ok(datasourceService.getTableInfo(dto));
	}

	/**
	 * 导出excel 表格
	 *
	 * @param req 查询条件
	 * @param ids 导出指定ID
	 * @return excel 文件流
	 */
	@Operation(summary = "导出", description = "导出")
	@ResponseExcel
	@GetMapping("/export")
	@HasPermission("agi_datasource_export")
	public List<Datasource> exportExcel(Datasource req, Long[] ids) {
		return datasourceService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), Datasource::getId, ids));
	}


	/**
	 * 通过条件查询数据源授权
	 *
	 * @param req 查询条件
	 * @return R  对象列表
	 */
	@Operation(summary = "通过条件查询", description = "通过条件查询对象")
	@GetMapping("/detailsTable")
	public R<List<DatasourceTable>> detailsTable(@ParameterObject DatasourceTable req) {
		return R.ok(datasourceTableService.list(Wrappers.query(req)));
	}


	/**
	 * 修改数据源授权
	 *
	 * @param req 数据源授权
	 * @return R
	 */
	@Operation(summary = "修改数据源授权", description = "修改数据源授权")
	@SysLog("修改数据源授权")
	@PutMapping("/updateTable")
	@HasPermission("agi_datasource_edit")
	public R updateTable(@RequestBody @Validated({Req.Update.class}) DatasourceTable req) {
		return R.ok(datasourceTableService.update(req));
	}

	/**
	 * 通过条件查询数据源表字段
	 *
	 * @param req 查询条件
	 * @return R  对象列表
	 */
	@Operation(summary = "通过条件查询", description = "通过条件查询对象")
	@GetMapping("/detailsTableField")
	public R<List<DatasourceTableField>> detailsTableField(@ParameterObject DatasourceTableField req) {
		return R.ok(datasourceTableFieldService.list(Wrappers.query(req)));
	}

	/**
	 * 修改数据源表字段
	 *
	 * @param req 数据源表字段
	 * @return R
	 */
	@Operation(summary = "修改数据源表字段", description = "修改数据源表字段")
	@SysLog("修改数据源表字段")
	@PutMapping("/updateTableField")
	@HasPermission("agi_datasource_edit")
	public R updateTableField(@RequestBody @Validated({Req.Update.class}) DatasourceTableField req) {
		return R.ok(datasourceTableFieldService.update(req));
	}
}