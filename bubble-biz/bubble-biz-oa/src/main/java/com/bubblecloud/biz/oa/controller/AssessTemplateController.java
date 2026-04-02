package com.bubblecloud.biz.oa.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AssessTemplateService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
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

import java.util.stream.Collectors;

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

	@GetMapping({"", "/page"})
	@Operation(summary = "考核模板列表")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject AssessTemplate query) {
		Page<AssessTemplate> res = assessTemplateService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@PostMapping
	@Operation(summary = "创建考核模板")
	public R<String> create(@RequestBody AssessTemplateSaveDTO dto) {
		AssessTemplate obj = PojoConvertUtil.convertPojo(dto, AssessTemplate.class);
		if (CollUtil.isNotEmpty(dto.getTargetIds())) {
			obj.setTargetIds(dto.getTargetIds().stream()
					.map(String::valueOf).collect(Collectors.joining(",", "[", "]")));
		}
		assessTemplateService.create(obj);
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
		AssessTemplate obj = PojoConvertUtil.convertPojo(dto, AssessTemplate.class);
		if (CollUtil.isNotEmpty(dto.getTargetIds())) {
			obj.setTargetIds(dto.getTargetIds().stream()
					.map(String::valueOf).collect(Collectors.joining(",", "[", "]")));
		}
		obj.setId(id);
		assessTemplateService.update(obj);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除考核模板")
	public R<String> removeById(@PathVariable Long id) {
		assessTemplateService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@PostMapping("/favorite/{id}")
	@Operation(summary = "收藏/取消收藏模板")
	public R<String> toggleFavorite(@PathVariable Long id) {
		assessTemplateService.toggleFavorite(id);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PostMapping("/cover/{id}")
	@Operation(summary = "设置模板封面")
	public R<String> setCover(@PathVariable Long id, @RequestParam String cover) {
		assessTemplateService.setCover(id, cover);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

}
