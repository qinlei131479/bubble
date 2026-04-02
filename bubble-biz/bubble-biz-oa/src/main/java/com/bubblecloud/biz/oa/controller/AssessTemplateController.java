package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AssessTemplateService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.AssessTemplateSaveDTO;
import com.bubblecloud.oa.api.entity.AssessTemplate;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绩效考核模板（对齐 PHP {@code ent/assess/template}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/assess/template")
@Tag(name = "绩效考核模板")
public class AssessTemplateController {

	private final AssessTemplateService assessTemplateService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "考核模板列表")
	public R<SimplePageVO> page(@ParameterObject Pg<AssessTemplate> pg, @ParameterObject AssessTemplate query) {
		return R.phpOk(assessTemplateService.pageTemplate(pg, query));
	}

	@PostMapping
	@Operation(summary = "创建考核模板")
	public R<String> create(@RequestBody AssessTemplateSaveDTO dto) {
		assessTemplateService.createTemplate(dto);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取考核模板详情")
	public R<AssessTemplate> details(@PathVariable Long id) {
		return R.phpOk(assessTemplateService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改考核模板")
	public R<String> update(@PathVariable Long id, @RequestBody AssessTemplateSaveDTO dto) {
		assessTemplateService.updateTemplate(id, dto);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除考核模板")
	public R<String> removeById(@PathVariable Long id) {
		assessTemplateService.removeTemplate(id);
		return R.phpOk("common.delete.succ");
	}

	@PostMapping("/favorite/{id}")
	@Operation(summary = "收藏/取消收藏模板")
	public R<String> toggleFavorite(@PathVariable Long id) {
		assessTemplateService.toggleFavorite(id);
		return R.phpOk("common.operation.succ");
	}

	@PostMapping("/cover/{id}")
	@Operation(summary = "设置模板封面")
	public R<String> setCover(@PathVariable Long id, @RequestParam String cover) {
		assessTemplateService.setCover(id, cover);
		return R.phpOk("common.operation.succ");
	}

}
