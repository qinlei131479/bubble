package com.bubblecloud.biz.oa.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bubblecloud.biz.oa.service.CompanyCardService;
import com.bubblecloud.biz.oa.service.FrameService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.vo.company.CompanyCardListDataVO;
import com.bubblecloud.oa.api.vo.frame.FrameDepartmentTreeNodeVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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

/**
 * 员工档案（对齐 PHP {@code ent/company/card}）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/company/card")
@Tag(name = "员工档案")
public class CompanyCardController {

	private final CompanyCardService companyCardService;

	private final FrameService frameService;

	@PostMapping
	@Operation(summary = "企业成员列表")
	public R<CompanyCardListDataVO> list(@RequestParam(defaultValue = "1") long entid, @RequestBody JsonNode body) {
		return R.phpOk(companyCardService.list(entid, body));
	}

	@GetMapping("/import/temp")
	@Operation(summary = "获取导入模板")
	public R<Map<String, Object>> importTemplate() {
		return R.phpOk(companyCardService.importTemplate());
	}

	@GetMapping("/tree")
	@Operation(summary = "组织架构树（同 frame 列表形态）")
	public R<List<FrameDepartmentTreeNodeVO>> tree(@RequestParam(defaultValue = "1") long entid) {
		return R.phpOk(frameService.departmentTreeList(1, entid));
	}

	@PostMapping("/save/{type}")
	@Operation(summary = "创建保存员工档案")
	public R<String> save(@RequestParam(defaultValue = "1") long entid, @PathVariable int type,
			@RequestBody JsonNode body) {
		companyCardService.saveCard(entid, type, body);
		return R.phpOk("common.insert.succ");
	}

	@PostMapping("/import")
	@Operation(summary = "导入用户档案")
	public R<String> importCards(@RequestParam(defaultValue = "1") long entid, @RequestBody JsonNode body) {
		return R.phpOk(companyCardService.importCards(entid, body));
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "获取企业用户名片")
	public R<JsonNode> info(@RequestParam(defaultValue = "1") long entid, @PathVariable long id) {
		return R.phpOk(companyCardService.cardDetail(entid, id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改保存员工档案")
	public R<String> update(@RequestParam(defaultValue = "1") long entid, @PathVariable long id,
			@RequestBody JsonNode body) {
		companyCardService.updateCard(entid, id, body);
		return R.phpOk("common.update.succ");
	}

	@PostMapping("/entry/{id}")
	@Operation(summary = "员工入职")
	public R<String> entry(@RequestParam(defaultValue = "1") long entid, @PathVariable long id) {
		companyCardService.entry(entid, id);
		return R.phpOk("common.update.succ");
	}

	@GetMapping("/formal/{id}")
	@Operation(summary = "员工转正表单")
	public R<JsonNode> formalForm(@RequestParam(defaultValue = "1") long entid, @PathVariable long id) {
		return R.phpOk(companyCardService.formalForm(entid, id));
	}

	@PutMapping("/be_formal/{id}")
	@Operation(summary = "员工转正")
	public R<String> beFormal(@RequestParam(defaultValue = "1") long entid, @PathVariable long id,
			@RequestBody JsonNode body) {
		companyCardService.beFormal(entid, id, body);
		return R.phpOk("common.update.succ");
	}

	@PostMapping("/quit/{id}")
	@Operation(summary = "员工离职")
	public R<String> quit(@RequestParam(defaultValue = "1") long entid, @PathVariable long id,
			@RequestBody JsonNode body) {
		companyCardService.quit(entid, id, body);
		return R.phpOk("common.update.succ");
	}

	@GetMapping("/change")
	@Operation(summary = "人事异动列表")
	public R<Map<String, Object>> change(@RequestParam(defaultValue = "1") long entid,
			@RequestParam(required = false) Long card_id, @RequestParam(required = false) Long uid) {
		Long filter = card_id != null ? card_id : uid;
		return R.phpOk(Map.of("list", companyCardService.changeList(entid, filter)));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除档案")
	public R<String> destroy(@RequestParam(defaultValue = "1") long entid, @PathVariable long id) {
		companyCardService.deleteCard(entid, id);
		return R.phpOk("删除成功");
	}

	@DeleteMapping("/batch")
	@Operation(summary = "批量删除档案")
	public R<String> batchDestroy(@RequestParam(defaultValue = "1") long entid, @RequestBody JsonNode body) {
		List<Long> ids = readLongList(body.get("id"));
		companyCardService.deleteBatch(entid, ids);
		return R.phpOk("删除成功");
	}

	@PostMapping("/batch")
	@Operation(summary = "批量设置成员进入部门")
	public R<String> batchSetFrame(@RequestParam(defaultValue = "1") long entid, @RequestBody JsonNode body) {
		List<Integer> frameIds = readIntList(body.get("frame_id"));
		List<Long> userIds = readLongList(body.get("user_id"));
		int mastartId = body.path("mastart_id").asInt(0);
		if (frameIds.isEmpty()) {
			throw new IllegalArgumentException("至少选择一个部门进行设置");
		}
		if (userIds.isEmpty()) {
			throw new IllegalArgumentException("至少选择一个用户进行设置");
		}
		companyCardService.batchSetFrame(entid, frameIds, userIds, mastartId);
		return R.phpOk("批量加入成功");
	}

	@GetMapping("/perfect/{id}")
	@Operation(summary = "邀请完善信息")
	public R<Map<String, Object>> sendPerfect(@RequestParam(defaultValue = "1") long entid, @PathVariable long id) {
		Long creator = OaSecurityUtil.currentUserId();
		if (creator == null) {
			creator = 0L;
		}
		return R.phpOk(companyCardService.sendPerfect(entid, creator, id));
	}

	@GetMapping("/interview")
	@Operation(summary = "邀请面试")
	public R<Map<String, Object>> sendInterview(@RequestParam(defaultValue = "1") long entid) {
		return R.phpOk(companyCardService.sendInterview(entid));
	}

	private static List<Long> readLongList(JsonNode node) {
		List<Long> list = new ArrayList<>();
		if (node == null || !node.isArray()) {
			return list;
		}
		ArrayNode arr = (ArrayNode) node;
		for (JsonNode x : arr) {
			if (x.isInt() || x.isLong()) {
				list.add(x.asLong());
			}
			else if (x.isTextual()) {
				list.add(Long.parseLong(x.asText().trim()));
			}
		}
		return list;
	}

	private static List<Integer> readIntList(JsonNode node) {
		List<Integer> list = new ArrayList<>();
		if (node == null || !node.isArray()) {
			return list;
		}
		for (JsonNode x : node) {
			if (x.isInt() || x.isLong()) {
				list.add(x.asInt());
			}
			else if (x.isTextual()) {
				list.add(Integer.parseInt(x.asText().trim()));
			}
		}
		return list;
	}

}
