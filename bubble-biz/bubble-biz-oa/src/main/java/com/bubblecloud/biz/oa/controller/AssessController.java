package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AssessService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.hr.AssessAppealDTO;
import com.bubblecloud.oa.api.dto.hr.AssessCensusDTO;
import com.bubblecloud.oa.api.dto.hr.AssessDeleteDTO;
import com.bubblecloud.oa.api.dto.hr.AssessEvalDTO;
import com.bubblecloud.oa.api.dto.hr.AssessSaveDTO;
import com.bubblecloud.oa.api.dto.hr.AssessTargetEvalDTO;
import com.bubblecloud.oa.api.entity.Assess;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.hr.AssessCensusVO;
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
 * 绩效考核（对齐 PHP {@code ent/assess}）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/assess")
@Tag(name = "绩效考核")
public class AssessController {

	private final AssessService assessService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "绩效考核列表（个人）")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject Assess query) {
		Page<Assess> res = assessService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@GetMapping("/index")
	@Operation(summary = "获取绩效考核列表（工作台）")
	public R<SimplePageVO> index(@ParameterObject Pg pg, @ParameterObject Assess query) {
		Page<Assess> res = assessService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@GetMapping("/list")
	@Operation(summary = "人事绩效考核列表")
	public R<SimplePageVO> list(@ParameterObject Pg pg, @ParameterObject Assess query) {
		Page<Assess> res = assessService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "获取绩效考核详情")
	public R<Assess> info(@PathVariable Long id) {
		return R.phpOk(assessService.getById(id));
	}

	@PostMapping({ "/create", "/target" })
	@Operation(summary = "创建绩效考核")
	public R<String> create(@RequestBody AssessSaveDTO dto) {
		Assess obj = PojoConvertUtil.convertPojo(dto, Assess.class);
		assessService.create(obj);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@PostMapping("/update/{id}")
	@Operation(summary = "修改绩效考核")
	public R<String> update(@PathVariable Long id, @RequestBody AssessSaveDTO dto) {
		Assess obj = PojoConvertUtil.convertPojo(dto, Assess.class);
		obj.setId(id);
		assessService.update(obj);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@PutMapping("/self_eval/{id}")
	@Operation(summary = "绩效考核自评")
	public R<String> selfEval(@PathVariable Long id, @RequestBody AssessEvalDTO dto) {
		assessService.selfEval(id, dto);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PutMapping("/superior_eval/{id}")
	@Operation(summary = "上级评价")
	public R<String> superiorEval(@PathVariable Long id, @RequestBody AssessEvalDTO dto) {
		assessService.superiorEval(id, dto);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PutMapping("/examine_eval/{id}")
	@Operation(summary = "上上级审核")
	public R<String> examineEval(@PathVariable Long id, @RequestBody AssessEvalDTO dto) {
		assessService.examineEval(id, dto);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@GetMapping("/show/{id}")
	@Operation(summary = "启用/停用绩效考核")
	public R<String> show(@PathVariable Long id, @RequestParam(required = false, defaultValue = "1") Integer status) {
		assessService.enableAssess(id, status);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@GetMapping("/explain/{id}")
	@Operation(summary = "获取绩效其他信息")
	public R<Assess> explain(@PathVariable Long id) {
		return R.phpOk(assessService.getById(id));
	}

	@PostMapping("/census")
	@Operation(summary = "考核统计图")
	public R<AssessCensusVO> census(@RequestBody AssessCensusDTO dto) {
		return R.phpOk(assessService.census(dto));
	}

	@PostMapping("/census_bar")
	@Operation(summary = "人事考核统计图")
	public R<AssessCensusVO> censusBar(@RequestBody AssessCensusDTO dto) {
		return R.phpOk(assessService.censusBar(dto));
	}

	@PutMapping("/eval")
	@Operation(summary = "绩效指标自评")
	public R<String> evalTarget(@RequestBody AssessTargetEvalDTO dto) {
		assessService.evalTarget(dto);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@GetMapping("/score/{id}")
	@Operation(summary = "绩效评分记录")
	public R<List<Object>> scoreRecord(@PathVariable Long id) {
		return R.phpOk(assessService.scoreRecord(id));
	}

	@GetMapping("/del_form/{id}")
	@Operation(summary = "绩效删除表单")
	public R<Assess> deleteForm(@PathVariable Long id) {
		return R.phpOk(assessService.getById(id));
	}

	@DeleteMapping("/delete/{id}")
	@Operation(summary = "绩效删除")
	public R<String> delete(@PathVariable Long id, @RequestBody AssessDeleteDTO body) {
		assessService.deleteAssess(id, body == null ? null : body.getMark());
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/del_record")
	@Operation(summary = "绩效删除记录")
	public R<List<Object>> deleteRecord(@RequestParam(required = false) Long entid) {
		return R.phpOk(assessService.deleteRecord(entid));
	}

	@PostMapping("/appeal/{id}")
	@Operation(summary = "绩效申诉/驳回")
	public R<String> appeal(@PathVariable Long id, @RequestBody AssessAppealDTO dto) {
		assessService.appealOrReject(id, dto);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@GetMapping("/abnormal")
	@Operation(summary = "绩效未创建列表")
	public R<List<Assess>> abnormal(@RequestParam(required = false) Long entid,
			@RequestParam(required = false) Long planId) {
		return R.phpOk(assessService.abnormalList(entid, planId));
	}

	@GetMapping("/is_abnormal")
	@Operation(summary = "绩效是否存在未创建")
	public R<Boolean> isAbnormal(@RequestParam(required = false) Long entid,
			@RequestParam(required = false) Long planId) {
		return R.phpOk(assessService.isAbnormal(entid, planId));
	}

}
