package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.DailyReportService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.DailyReplyDTO;
import com.bubblecloud.oa.api.dto.DailySaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.EnterpriseUserDaily;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * 工作汇报（PHP ent/daily）。
 *
 * @author qinlei
 * @date 2026/3/28 14:00
 */
@RestController
@RequestMapping("/ent/daily")
@RequiredArgsConstructor
@Tag(name = "日报汇报")
public class DailyController {

	private final DailyReportService dailyReportService;

	private final AdminService adminService;

	private final ObjectMapper objectMapper;

	@GetMapping({ "", "/" })
	@Operation(summary = "日报列表")
	public R<ListCountVO<EnterpriseUserDaily>> index(@ParameterObject Pg<EnterpriseUserDaily> pg,
			@RequestParam(required = false) Integer types, @RequestParam(required = false) String time,
			@RequestParam(required = false) Long user_id, @RequestParam(defaultValue = "1") Long entid) {
		Long uid = OaSecurityUtil.currentUserId();
		return R.phpOk(dailyReportService.list(pg, types, time, user_id == null ? uid : user_id, entid));
	}

	@PostMapping({ "", "/" })
	@Operation(summary = "保存日报")
	public R<String> store(@RequestBody(required = false) DailySaveDTO body,
			@RequestParam(defaultValue = "1") Long entid) {
		try {
			if (body == null) {
				return R.phpFailed("参数错误");
			}
			Long aid = OaSecurityUtil.currentUserId();
			Admin a = adminService.getById(aid);
			String uuid = a == null ? "" : a.getUid();
			body.setEntid(entid);
			dailyReportService.save(aid, uuid, body);
			return R.phpOk(OaConstants.INSERT_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "日报详情")
	public R<JsonNode> edit(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid) {
		try {
			return R.phpOk(dailyReportService.editDetail(id, entid));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改日报")
	public R<String> update(@PathVariable Long id, @RequestBody(required = false) DailySaveDTO body) {
		try {
			if (body == null) {
				return R.phpFailed("参数错误");
			}
			dailyReportService.update(OaSecurityUtil.currentUserId(), id, body);
			return R.phpOk(OaConstants.UPDATE_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除日报")
	public R<String> destroy(@PathVariable Long id) {
		dailyReportService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/users")
	@Operation(summary = "下级汇报人员（占位空列表）")
	public R<JsonNode> users() {
		return R.phpOk(objectMapper.createArrayNode());
	}

	@PostMapping("/reply")
	@Operation(summary = "日报回复")
	public R<String> reply(@RequestBody(required = false) DailyReplyDTO body) {
		try {
			if (body == null) {
				return R.phpFailed("参数错误");
			}
			Admin a = adminService.getById(OaSecurityUtil.currentUserId());
			String uuid = a == null ? "" : a.getUid();
			dailyReportService.replySave(uuid, body);
			return R.phpOk("回复成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/reply/{id}/{daily_id}")
	@Operation(summary = "删除日报回复")
	public R<String> deleteReply(@PathVariable Long id, @PathVariable("daily_id") Long dailyId) {
		try {
			Admin a = adminService.getById(OaSecurityUtil.currentUserId());
			String uuid = a == null ? "" : a.getUid();
			dailyReportService.replyDelete(id, dailyId, uuid);
			return R.phpOk("删除成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/report_member")
	@Operation(summary = "默认汇报人")
	public R<JsonNode> reportMember() {
		return R.phpOk(dailyReportService.reportMembers(OaSecurityUtil.currentUserId()));
	}

	@GetMapping("/submit_statistics")
	@Operation(summary = "汇报提交统计（占位）")
	public R<JsonNode> submitStatistics() {
		return R.phpOk(objectMapper.createObjectNode());
	}

	@GetMapping("/statistics")
	@Operation(summary = "汇报统计（占位）")
	public R<JsonNode> statistics() {
		return R.phpOk(objectMapper.createObjectNode());
	}

	@GetMapping("/submit_list")
	@Operation(summary = "提交列表（占位）")
	public R<JsonNode> submitList() {
		return R.phpOk(objectMapper.createArrayNode());
	}

	@GetMapping("/no_submit_list")
	@Operation(summary = "未提交列表（占位）")
	public R<JsonNode> noSubmitList() {
		return R.phpOk(objectMapper.createArrayNode());
	}

	@GetMapping("/schedule_record/{type}")
	@Operation(summary = "日报待办回显（占位）")
	public R<JsonNode> scheduleRecord(@PathVariable Integer type) {
		return R.phpOk(objectMapper.createArrayNode());
	}

	@GetMapping("/report_list")
	@Operation(summary = "汇报人查看列表（占位）")
	public R<JsonNode> reportList() {
		return R.phpOk(objectMapper.createArrayNode());
	}

	@GetMapping("/export")
	@Operation(summary = "导出（占位）")
	public R<JsonNode> export() {
		return R.phpOk(objectMapper.createArrayNode());
	}

}
