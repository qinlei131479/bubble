package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.AssessScoreService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.AssessConfigSaveDTO;
import com.bubblecloud.oa.api.entity.AssessScore;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绩效配置（对齐 PHP {@code ent/assess} 配置接口）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/assess")
@Tag(name = "绩效配置")
public class AssessConfigController {

	private final AssessScoreService assessScoreService;

	@GetMapping("/score_config")
	@Operation(summary = "获取积分配置")
	public R<SimplePageVO> scoreConfig(@ParameterObject Pg<AssessScore> pg) {
		return R.phpOk(assessScoreService.pageScoreConfig(pg));
	}

	@PostMapping("/score_config")
	@Operation(summary = "保存积分配置")
	public R<String> saveScoreConfig(@RequestBody AssessConfigSaveDTO dto) {
		assessScoreService.saveScoreConfig(dto);
		return R.phpOk("common.operation.succ");
	}

	@GetMapping("/examine_config")
	@Operation(summary = "获取审核配置")
	public R<Object> examineConfig() {
		return R.phpOk(assessScoreService.getExamineConfig());
	}

}
