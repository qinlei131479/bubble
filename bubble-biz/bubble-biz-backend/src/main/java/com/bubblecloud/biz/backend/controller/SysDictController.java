package com.bubblecloud.biz.backend.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.api.backend.entity.SysDict;
import com.bubblecloud.api.backend.entity.SysDictItem;
import com.bubblecloud.biz.backend.service.SysDictItemService;
import com.bubblecloud.biz.backend.service.SysDictService;
import com.bubblecloud.common.core.constant.CacheConstants;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.log.annotation.SysLog;
import com.bubblecloud.common.security.annotation.Inner;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author lengleng
 * @since 2019-03-19
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dict")
@Tag(description = "dict", name = "字典管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysDictController {

	private final SysDictService sysDictService;

	private final SysDictItemService sysDictItemService;

	/**
	 * 通过ID查询字典信息
	 * @param id ID
	 * @return 字典信息
	 */
	@Operation(summary = "通过ID查询字典信息", description = "通过ID查询字典信息")
	@GetMapping("/details/{id}")
	public R<SysDict> getById(@PathVariable Long id) {
		return R.ok(sysDictService.getById(id));
	}

	/**
	 * 查询字典信息
	 * @param query 查询信息
	 * @return 字典信息
	 */
	@Operation(summary = "查询字典信息", description = "查询字典信息")
	@GetMapping("/details")
	public R<SysDict> getDetails(@ParameterObject SysDict query) {
		return R.ok(sysDictService.getOne(Wrappers.query(query), false));
	}

	/**
	 * 分页查询字典信息
	 * @param page 分页对象
	 * @return 分页对象
	 */
	@Operation(summary = "分页查询", description = "分页查询")
	@GetMapping("/page")
	public R<Page<SysDict>> getDictPage(@ParameterObject Page page, @ParameterObject SysDict sysDict) {
		return R.ok(sysDictService.page(page,
				Wrappers.<SysDict>lambdaQuery()
					.eq(StrUtil.isNotBlank(sysDict.getSystemFlag()), SysDict::getSystemFlag, sysDict.getSystemFlag())
					.like(StrUtil.isNotBlank(sysDict.getDictType()), SysDict::getDictType, sysDict.getDictType())));
	}

	/**
	 * 添加字典
	 * @param sysDict 字典信息
	 * @return success、false
	 */
	@Operation(summary = "添加字典", description = "添加字典")
	@SysLog("添加字典")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_dict_add')")
	public R<SysDict> save(@Valid @RequestBody SysDict sysDict) {
		sysDictService.save(sysDict);
		return R.ok(sysDict);
	}

	/**
	 * 删除字典，并且清除字典缓存
	 * @param ids ID
	 * @return R
	 */
	@Operation(summary = "删除字典询", description = "删除字典")
	@SysLog("删除字典")
	@DeleteMapping
	@PreAuthorize("@pms.hasPermission('sys_dict_del')")
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R removeById(@RequestBody Long[] ids) {
		return R.ok(sysDictService.removeDictByIds(ids));
	}

	/**
	 * 修改字典
	 * @param sysDict 字典信息
	 * @return success/false
	 */
	@Operation(summary = "修改字典", description = "修改字典")
	@PutMapping
	@SysLog("修改字典")
	@PreAuthorize("@pms.hasPermission('sys_dict_edit')")
	public R updateById(@Valid @RequestBody SysDict sysDict) {
		return sysDictService.updateDict(sysDict);
	}

	/**
	 * 分页查询
	 * @param name 名称或者字典项
	 * @return
	 */
	@Operation(summary = "查询字典列表", description = "查询字典列表")
	@GetMapping("/list")
	public R<List<SysDict>> getDictList(String name) {
		return R.ok(sysDictService.list(Wrappers.<SysDict>lambdaQuery()
			.like(StrUtil.isNotBlank(name), SysDict::getDictType, name)
			.or()
			.like(StrUtil.isNotBlank(name), SysDict::getDescription, name)));
	}

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param sysDictItem 字典项
	 * @return
	 */
	@Operation(summary = "分页查询字典项", description = "分页查询字典项")
	@GetMapping("/item/page")
	public R<Page<SysDictItem>> getSysDictItemPage(@ParameterObject Page page, @ParameterObject SysDictItem sysDictItem) {
		return R.ok(sysDictItemService.page(page, Wrappers.query(sysDictItem)));
	}

	/**
	 * 通过id查询字典项
	 * @param id id
	 * @return R
	 */
	@Operation(summary = "通过id查询字典项", description = "通过id查询字典项询")
	@GetMapping("/item/details/{id}")
	public R<SysDictItem> getDictItemById(@PathVariable("id") Long id) {
		return R.ok(sysDictItemService.getById(id));
	}

	/**
	 * 查询字典项详情
	 * @param query 查询条件
	 * @return R
	 */
	@Operation(summary = "查询字典项详情", description = "查询字典项详情")
	@GetMapping("/item/details")
	public R<SysDictItem> getDictItemDetails(@ParameterObject SysDictItem query) {
		return R.ok(sysDictItemService.getOne(Wrappers.query(query), false));
	}

	/**
	 * 新增字典项
	 * @param sysDictItem 字典项
	 * @return R
	 */
	@Operation(summary = "新增字典项", description = "新增字典项")
	@SysLog("新增字典项")
	@PostMapping("/item")
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R save(@RequestBody SysDictItem sysDictItem) {
		return R.ok(sysDictItemService.save(sysDictItem));
	}

	/**
	 * 修改字典项
	 * @param sysDictItem 字典项
	 * @return R
	 */
	@Operation(summary = "修改字典项", description = "修改字典项")
	@SysLog("修改字典项")
	@PutMapping("/item")
	public R updateById(@RequestBody SysDictItem sysDictItem) {
		return sysDictItemService.updateDictItem(sysDictItem);
	}

	/**
	 * 通过id删除字典项
	 * @param id id
	 * @return R
	 */
	@Operation(summary = "通过id删除字典项", description = "通过id删除字典项")
	@SysLog("删除字典项")
	@DeleteMapping("/item/{id}")
	public R removeDictItemById(@PathVariable Long id) {
		return sysDictItemService.removeDictItem(id);
	}

	/**
	 * 同步缓存字典
	 * @return R
	 */
	@Operation(summary = "同步缓存字典", description = "同步缓存字典")
	@SysLog("同步字典")
	@PutMapping("/sync")
	public R sync() {
		return sysDictService.syncDictCache();
	}

	@Operation(summary = "导出字典", description = "导出字典")
	@ResponseExcel
	@GetMapping("/export")
	public List<SysDictItem> export(SysDictItem sysDictItem) {
		return sysDictItemService.list(Wrappers.query(sysDictItem));
	}

	/**
	 * 通过字典类型查找字典
	 * @param type 类型
	 * @return 同类型字典
	 */
	@Operation(summary = "通过字典类型查找字典", description = "通过字典类型查找字典")
	@GetMapping("/type/{type}")
	@Cacheable(value = CacheConstants.DICT_DETAILS, key = "#type", unless = "#result.data.isEmpty()")
	public R<List<SysDictItem>> getDictByType(@PathVariable String type) {
		return R.ok(sysDictItemService.list(Wrappers.<SysDictItem>query().lambda().eq(SysDictItem::getDictType, type)));
	}

	/**
	 * 通过字典类型查找字典 (针对feign调用) TODO: 兼容性方案，代码重复
	 * @param type 类型
	 * @return 同类型字典
	 */
	@Inner
	@Operation(summary = "通过字典类型查找字典 (针对feign调用) ", description = "通过字典类型查找字典 (针对feign调用) ")
	@GetMapping("/remote/type/{type}")
	@Cacheable(value = CacheConstants.DICT_DETAILS, key = "#type", unless = "#result.data.isEmpty()")
	public R<List<SysDictItem>> getRemoteDictByType(@PathVariable String type) {
		return R.ok(sysDictItemService.list(Wrappers.<SysDictItem>query().lambda().eq(SysDictItem::getDictType, type)));
	}

}
