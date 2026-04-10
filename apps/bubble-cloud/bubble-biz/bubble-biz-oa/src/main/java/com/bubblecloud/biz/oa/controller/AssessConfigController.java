package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.OaAssessConfigService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.hr.AssessScoreConfigSaveDTO;
import com.bubblecloud.oa.api.vo.hr.AssessScoreConfigVO;
import com.bubblecloud.oa.api.vo.hr.AssessVerifyConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绩效配置（对齐 PHP {@code ent/assess/score}、{@code ent/assess/verify}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/assess")
@Tag(name = "绩效配置")
public class AssessConfigController {

	private final OaAssessConfigService oaAssessConfigService;

	@GetMapping("/score")
	@Operation(summary = "获取积分配置及说明")
	public R<AssessScoreConfigVO> getScore(@RequestParam(required = false, defaultValue = "1") Long entid) {
		return R.phpOk(oaAssessConfigService.getScoreConfig(entid));
	}

	@PostMapping("/score")
	@Operation(summary = "保存积分配置及说明")
	public R<String> saveScore(@RequestParam(required = false, defaultValue = "1") Long entid,
			@RequestBody AssessScoreConfigSaveDTO dto) {
		oaAssessConfigService.saveScoreConfig(entid, dto);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@GetMapping("/verify")
	@Operation(summary = "获取审核配置及人员")
	public R<AssessVerifyConfigVO> getVerify() {
		return R.phpOk(oaAssessConfigService.getVerifyConfig());
	}

	/**
	 * 兼容旧路径（部分网关或历史前端仍使用 score_config）。
	 */
	@GetMapping("/score_config")
	@Operation(summary = "获取积分配置及说明（兼容路径）")
	public R<AssessScoreConfigVO> getScoreLegacy(@RequestParam(required = false, defaultValue = "1") Long entid) {
		return getScore(entid);
	}

	@PostMapping("/score_config")
	@Operation(summary = "保存积分配置（兼容路径）")
	public R<String> saveScoreLegacy(@RequestParam(required = false, defaultValue = "1") Long entid,
			@RequestBody AssessScoreConfigSaveDTO dto) {
		return saveScore(entid, dto);
	}

	@GetMapping("/examine_config")
	@Operation(summary = "获取审核配置（兼容路径）")
	public R<AssessVerifyConfigVO> examineConfigLegacy() {
		return getVerify();
	}

}
