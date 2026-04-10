package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.ApproveHolidayTypeService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.PojoConvertUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.ApproveHolidayTypeSaveDTO;
import com.bubblecloud.oa.api.entity.ApproveHolidayType;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.approve.HolidayTypeSelectItemVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import cn.hutool.core.util.StrUtil;

/**
 * 审批假期类型（对齐 PHP {@code ent/approve/holiday_type}）。
 *
 * @author qinlei
 * @date 2026/4/2 15:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/approve/holiday_type")
@Tag(name = "审批假期类型")
public class ApproveHolidayTypeController {

	private final ApproveHolidayTypeService approveHolidayTypeService;

	@GetMapping("/list")
	@Operation(summary = "假期类型列表")
	public R<ListCountVO<ApproveHolidayType>> index(@ParameterObject Pg pg,
			@RequestParam(required = false) String name) {
		ApproveHolidayType q = new ApproveHolidayType();
		q.setNameLike(name);
		Page<ApproveHolidayType> res = approveHolidayTypeService.findPg(pg, q);
		return R.phpOk(ListCountVO.of(res.getRecords(), res.getTotal()));
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "假期类型详情")
	public R<ApproveHolidayType> info(@PathVariable Long id) {
		return R.phpOk(approveHolidayTypeService.getById(id));
	}

	@PostMapping
	@Operation(summary = "保存假期类型")
	public R<String> create(@RequestBody ApproveHolidayTypeSaveDTO dto) {
		ApproveHolidayType obj = PojoConvertUtil.convertPojo(dto, ApproveHolidayType.class);
		approveHolidayTypeService.create(obj);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改假期类型")
	public R<String> update(@PathVariable Long id, @RequestBody ApproveHolidayTypeSaveDTO dto) {
		ApproveHolidayType obj = PojoConvertUtil.convertPojo(dto, ApproveHolidayType.class);
		obj.setId(id);
		approveHolidayTypeService.update(obj);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除假期类型")
	public R<String> removeById(@PathVariable Long id) {
		approveHolidayTypeService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/select")
	@Operation(summary = "假期类型下拉")
	public R<List<HolidayTypeSelectItemVO>> select() {
		return R.phpOk(approveHolidayTypeService.getSelectList(OaSecurityUtil.currentUserId()));
	}

}
