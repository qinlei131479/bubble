package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.JobAnalysisService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.jobanalysis.JobAnalysisUpdateDTO;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.jobanalysis.JobAnalysisListItemVO;
import com.bubblecloud.oa.api.vo.jobanalysis.JobAnalysisMineVO;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作分析（对齐 PHP {@code ent/company/job_analysis}）。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/company/job_analysis")
@Tag(name = "工作分析")
public class JobAnalysisController {

	private final JobAnalysisService jobAnalysisService;

	@GetMapping
	@Operation(summary = "工作分析列表")
	public R<ListCountVO<JobAnalysisListItemVO>> index(@RequestParam(defaultValue = "1") Long entid,
			@RequestParam(name = "frame_id", required = false) Integer frameId,
			@RequestParam(required = false) String name, @RequestParam(required = false) List<Integer> types,
			@RequestParam(name = "job_id", required = false) Integer jobId,
			@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "15") Integer limit) {
		Long cur = OaSecurityUtil.currentUserId();
		long curId = ObjectUtil.defaultIfNull(cur, 0L);
		int pg = ObjectUtil.defaultIfNull(page, 1);
		int lim = ObjectUtil.defaultIfNull(limit, 15);
		return R.phpOk(jobAnalysisService.list(entid, curId, frameId, name, types, jobId, pg, lim));
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "工作分析详情")
	public R<Object> info(@RequestParam(defaultValue = "1") Long entid, @PathVariable Long id) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		try {
			return R.phpOk(jobAnalysisService.info(entid, id));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/mine")
	@Operation(summary = "我的工作分析")
	public R<JobAnalysisMineVO> mine(@RequestParam(defaultValue = "1") Long entid) {
		Long cur = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(cur)) {
			return R.phpFailed("未登录");
		}
		return R.phpOk(jobAnalysisService.mine(entid, cur));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改工作分析")
	public R<String> update(@RequestParam(defaultValue = "1") Long entid, @PathVariable Long id,
			@Valid @RequestBody JobAnalysisUpdateDTO dto) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		try {
			jobAnalysisService.update(entid, id, dto);
			return R.phpOk(OaConstants.UPDATE_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
