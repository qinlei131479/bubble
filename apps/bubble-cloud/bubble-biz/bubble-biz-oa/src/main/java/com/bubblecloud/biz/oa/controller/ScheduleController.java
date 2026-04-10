package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.ScheduleApiService;
import com.bubblecloud.biz.oa.service.ScheduleTypeService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.ScheduleDeleteDTO;
import com.bubblecloud.oa.api.dto.ScheduleIndexQueryDTO;
import com.bubblecloud.oa.api.dto.ScheduleReplySaveDTO;
import com.bubblecloud.oa.api.dto.ScheduleStatusUpdateDTO;
import com.bubblecloud.oa.api.dto.ScheduleStoreDTO;
import com.bubblecloud.oa.api.dto.ScheduleTypeSaveDTO;
import com.bubblecloud.oa.api.dto.ScheduleUpdateDTO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleCalendarCountItemVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleRecordVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleTypeVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;

/**
 * 日程（PHP ent/schedule）。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/schedule")
@Tag(name = "日程")
public class ScheduleController {

	private final ScheduleApiService scheduleApiService;

	private final ScheduleTypeService scheduleTypeService;

	private final ObjectMapper objectMapper;

	@GetMapping("/types")
	@Operation(summary = "日程类型列表")
	public R<List<ScheduleTypeVO>> types() {
		return R.phpOk(scheduleTypeService.listForUser(OaSecurityUtil.currentUserId()));
	}

	@GetMapping("/type/create")
	@Operation(summary = "新建日程类型表单")
	public R<OaElFormVO> createTypeForm() {
		return R.phpOk(scheduleTypeService.createForm(objectMapper));
	}

	@PostMapping("/type/save")
	@Operation(summary = "新建日程类型")
	public R<String> saveType(@RequestBody(required = false) ScheduleTypeSaveDTO dto) {
		try {
			if (dto == null) {
				return R.phpFailed("参数错误");
			}
			scheduleTypeService.saveType(OaSecurityUtil.currentUserId(), dto);
			return R.phpOk("添加成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/type/edit/{id}")
	@Operation(summary = "修改日程类型表单")
	public R<OaElFormVO> editTypeForm(@PathVariable Long id) {
		try {
			return R.phpOk(scheduleTypeService.editForm(id, OaSecurityUtil.currentUserId(), objectMapper));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/type/update/{id}")
	@Operation(summary = "修改日程类型")
	public R<String> updateType(@PathVariable Long id, @RequestBody(required = false) ScheduleTypeSaveDTO dto) {
		try {
			if (dto == null) {
				return R.phpFailed("参数错误");
			}
			scheduleTypeService.updateType(id, OaSecurityUtil.currentUserId(), dto);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/type/delete/{id}")
	@Operation(summary = "删除日程类型")
	public R<String> deleteType(@PathVariable Long id) {
		try {
			scheduleTypeService.deleteType(id, OaSecurityUtil.currentUserId());
			return R.phpOk("删除成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PostMapping("/index")
	@Operation(summary = "日程列表")
	public R<List<ScheduleRecordVO>> index(@RequestBody(required = false) ScheduleIndexQueryDTO dto) {
		if (dto == null) {
			dto = new ScheduleIndexQueryDTO();
		}
		return R.phpOk(scheduleApiService.scheduleIndex(OaSecurityUtil.currentUserId(), dto));
	}

	@PostMapping("/store")
	@Operation(summary = "新建日程")
	public R<String> store(@RequestBody(required = false) ScheduleStoreDTO body,
			@RequestParam(defaultValue = "1") Integer entid) {
		try {
			if (body == null) {
				return R.phpFailed("参数错误");
			}
			scheduleApiService.saveSchedule(OaSecurityUtil.currentUserId(), entid, body);
			return R.phpOk("添加成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/update/{id}")
	@Operation(summary = "修改日程")
	public R<String> update(@PathVariable Long id, @RequestBody(required = false) ScheduleUpdateDTO body,
			@RequestParam(defaultValue = "1") Integer entid) {
		try {
			if (body == null) {
				return R.phpFailed("参数错误");
			}
			scheduleApiService.updateSchedule(OaSecurityUtil.currentUserId(), entid, id, body);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/status/{id}")
	@Operation(summary = "修改日程状态")
	public R<String> status(@PathVariable Long id, @RequestBody(required = false) ScheduleStatusUpdateDTO dto) {
		try {
			if (dto == null) {
				dto = new ScheduleStatusUpdateDTO();
			}
			dto.setId(id);
			scheduleApiService.updateStatus(OaSecurityUtil.currentUserId(), dto);
			return R.phpOk("操作成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "日程详情")
	public R<JsonNode> info(@PathVariable Long id, @RequestParam(required = false) String start_time,
			@RequestParam(required = false) String end_time) {
		try {
			return R.phpOk(scheduleApiService.scheduleInfo(OaSecurityUtil.currentUserId(), id, start_time, end_time));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/delete/{id}")
	@Operation(summary = "删除日程")
	public R<String> delete(@PathVariable Long id, @RequestBody(required = false) ScheduleDeleteDTO body) {
		try {
			scheduleApiService.deleteSchedule(OaSecurityUtil.currentUserId(), id, body);
			return R.phpOk("删除成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PostMapping("/count")
	@Operation(summary = "日程日历统计")
	public R<List<ScheduleCalendarCountItemVO>> count(@RequestBody(required = false) ScheduleIndexQueryDTO dto,
			@RequestParam(defaultValue = "1") Integer entid) {
		if (dto == null) {
			dto = new ScheduleIndexQueryDTO();
		}
		return R.phpOk(scheduleApiService.scheduleCount(OaSecurityUtil.currentUserId(), entid, dto));
	}

	@GetMapping("/replys")
	@Operation(summary = "日程评价列表")
	public R<JsonNode> replys(@RequestParam(required = false) Long pid) {
		if (ObjectUtil.isNull(pid) || pid <= 0) {
			return R.phpOk(objectMapper.createArrayNode());
		}
		return R.phpOk(scheduleApiService.replys(pid));
	}

	@PostMapping("/reply/save")
	@Operation(summary = "保存日程评价")
	public R<String> saveReply(@RequestBody(required = false) ScheduleReplySaveDTO body,
			@RequestParam(defaultValue = "1") Integer entid) {
		try {
			scheduleApiService.saveScheduleReply(OaSecurityUtil.currentUserId(), entid,
					body == null ? new ScheduleReplySaveDTO() : body);
			return R.phpOk("保存成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/reply/del/{id}")
	@Operation(summary = "删除日程评价")
	public R<String> delReply(@PathVariable Long id) {
		try {
			scheduleApiService.deleteScheduleReply(OaSecurityUtil.currentUserId(), id);
			return R.phpOk(OaConstants.DELETE_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
