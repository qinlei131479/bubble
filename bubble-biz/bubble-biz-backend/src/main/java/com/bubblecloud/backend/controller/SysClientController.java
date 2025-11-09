package com.bubblecloud.backend.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.api.backend.entity.SysOauthClientDetails;
import com.bubblecloud.backend.service.SysOauthClientDetailsService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.log.annotation.SysLog;
import com.bubblecloud.common.security.annotation.Inner;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lengleng
 * @since 2018-05-15
 */
@RestController
@AllArgsConstructor
@RequestMapping("/client")
@Tag(description = "client终端说明", name = "客户端管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysClientController {

	private final SysOauthClientDetailsService clientDetailsService;

	/**
	 * 通过ID查询
	 * @param clientId clientId
	 * @return SysOauthClientDetails
	 */
	@Operation(summary = "按clientId查询", description = "按clientID查询说明")
	@GetMapping("/{clientId}")
	public R<SysOauthClientDetails> getByClientId(@PathVariable String clientId) {
		SysOauthClientDetails details = clientDetailsService
			.getOne(Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId));
		return R.ok(details);
	}

	/**
	 * 简单分页查询
	 * @param page 分页对象
	 * @param sysOauthClientDetails 系统终端
	 * @return
	 */
	@Operation(summary = "分页查询", description = "分页查询说明")
	@GetMapping("/page")
	public R<Page<SysOauthClientDetails>> getOauthClientDetailsPage(@ParameterObject Page page,
			@ParameterObject SysOauthClientDetails sysOauthClientDetails) {
		LambdaQueryWrapper<SysOauthClientDetails> wrapper = Wrappers.<SysOauthClientDetails>lambdaQuery()
			.like(StrUtil.isNotBlank(sysOauthClientDetails.getClientId()), SysOauthClientDetails::getClientId,
					sysOauthClientDetails.getClientId())
			.like(StrUtil.isNotBlank(sysOauthClientDetails.getClientSecret()), SysOauthClientDetails::getClientSecret,
					sysOauthClientDetails.getClientSecret());
		return R.ok(clientDetailsService.page(page, wrapper));
	}

	/**
	 * 添加
	 * @param clientDetails 实体
	 * @return success/false
	 */
	@Operation(summary = "添加终端", description = "添加终端说明")
	@SysLog("添加终端")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_client_add')")
	public R add(@Valid @RequestBody SysOauthClientDetails clientDetails) {
		return R.ok(clientDetailsService.saveClient(clientDetails));
	}

	/**
	 * 删除
	 * @param ids ID 列表
	 * @return success/false
	 */
	@Operation(summary = "删除终端", description = "删除终端说明")
	@SysLog("删除终端")
	@DeleteMapping
	@PreAuthorize("@pms.hasPermission('sys_client_del')")
	public R removeById(@RequestBody Long[] ids) {
		clientDetailsService.removeBatchByIds(CollUtil.toList(ids));
		return R.ok();
	}

	/**
	 * 编辑
	 * @param clientDetails 实体
	 * @return success/false
	 */
	@Operation(summary = "编辑终端", description = "编辑终端说明")
	@SysLog("编辑终端")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('sys_client_edit')")
	public R update(@Valid @RequestBody SysOauthClientDetails clientDetails) {
		return R.ok(clientDetailsService.updateClientById(clientDetails));
	}

	@Operation(summary = "按clientId查询", description = "按clientId查询说明",hidden = true)
	@Inner
	@GetMapping("/getClientDetailsById/{clientId}")
	public R<SysOauthClientDetails> getClientDetailsById(@PathVariable String clientId) {
		return R.ok(clientDetailsService.getOne(
				Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId), false));
	}


	/**
	 * 同步缓存字典
	 * @return R
	 */
	@Operation(summary = "同步终端", description = "同步终端说明")
	@SysLog("同步终端")
	@PutMapping("/sync")
	public R sync() {
		return clientDetailsService.syncClientCache();
	}

	/**
	 * 导出所有客户端
	 * @return excel
	 */
	@Operation(summary = "导出excel", description = "导出excel说明")
	@ResponseExcel
	@SysLog("导出excel")
	@GetMapping("/export")
	public List<SysOauthClientDetails> export(SysOauthClientDetails sysOauthClientDetails) {
		return clientDetailsService.list(Wrappers.query(sysOauthClientDetails));
	}

}
