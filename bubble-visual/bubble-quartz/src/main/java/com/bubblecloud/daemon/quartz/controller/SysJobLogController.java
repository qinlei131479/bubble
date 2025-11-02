package com.bubblecloud.daemon.quartz.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.daemon.quartz.entity.SysJobLog;
import com.bubblecloud.daemon.quartz.service.SysJobLogService;
import com.bubblecloud.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * @author frwcloud
 * <p>
 * 定时任务执行日志表
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sys-job-log")
@Tag(description = "sys-job-log", name = "定时任务日志")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysJobLogController {

	private final SysJobLogService sysJobLogService;

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param sysJobLog 定时任务执行日志表
	 * @return
	 */
	@GetMapping("/page")
	@Operation(description = "分页定时任务日志查询")
	public R getSysJobLogPage(Page page, SysJobLog sysJobLog) {
		return R.ok(sysJobLogService.page(page, Wrappers.query(sysJobLog)));
	}

	@DeleteMapping
	@Operation(description = "批量删除日志")
	public R deleteLogs(@RequestBody Long[] ids) {
		return R.ok(sysJobLogService.removeBatchByIds(CollUtil.toList(ids)));
	}

}
