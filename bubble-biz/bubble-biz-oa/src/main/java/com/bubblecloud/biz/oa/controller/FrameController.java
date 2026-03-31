package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.security.OaCurrentUser;
import com.bubblecloud.biz.oa.service.FrameService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.dto.FrameSaveDTO;
import com.bubblecloud.oa.api.dto.FrameUpdateDTO;
import com.bubblecloud.oa.api.vo.frame.FrameAdminBriefVO;
import com.bubblecloud.oa.api.vo.frame.FrameAuthTreeNodeVO;
import com.bubblecloud.oa.api.vo.frame.FrameDepartmentTreeNodeVO;
import com.bubblecloud.oa.api.vo.frame.FrameDetailVO;
import com.bubblecloud.oa.api.vo.frame.FrameFormDataVO;
import com.bubblecloud.oa.api.vo.frame.FrameScopeItemVO;
import com.bubblecloud.oa.api.vo.frame.FrameUserTreeNodeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
 * 组织架构（对齐 PHP {@code ent/config/frame}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/config/frame")
@Tag(name = "组织架构")
public class FrameController {

	private final FrameService frameService;

	@GetMapping
	@Operation(summary = "部门树列表")
	public PhpResponse<List<FrameDepartmentTreeNodeVO>> index(@RequestParam(defaultValue = "1") int is_show,
			@RequestParam(defaultValue = "1") int entid) {
		return PhpResponse.ok(frameService.departmentTreeList(is_show, entid));
	}

	@GetMapping("/tree")
	@Operation(summary = "权限/范围用部门树")
	public PhpResponse<List<FrameAuthTreeNodeVO>> tree(@RequestParam(defaultValue = "0") int role,
			@RequestParam(defaultValue = "0") int scope, @RequestParam(defaultValue = "1") int entid) {
		Long uid = currentUserId();
		if (uid == null) {
			return PhpResponse.failed("未登录");
		}
		return PhpResponse.ok(frameService.getTree(uid, entid, role == 1, scope == 1));
	}

	@GetMapping("/user")
	@Operation(summary = "部门人员树")
	public PhpResponse<List<FrameUserTreeNodeVO>> userTree(@RequestParam(defaultValue = "0") int role,
			@RequestParam(defaultValue = "0") int leave, @RequestParam(defaultValue = "1") int entid) {
		Long uid = currentUserId();
		if (uid == null) {
			return PhpResponse.failed("未登录");
		}
		return PhpResponse.ok(frameService.getUserTree(uid, entid, role == 1, leave == 1));
	}

	@GetMapping("/create")
	@Operation(summary = "新增部门表单")
	public PhpResponse<FrameFormDataVO> create(@RequestParam(defaultValue = "1") int entid) {
		return PhpResponse.ok(frameService.getFormData(entid, 0));
	}

	@PostMapping
	@Operation(summary = "保存部门")
	public PhpResponse<String> store(@RequestBody FrameSaveDTO dto) {
		frameService.createDepartment(dto);
		return PhpResponse.ok("保存成功");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑部门表单")
	public PhpResponse<FrameFormDataVO> edit(@PathVariable long id, @RequestParam(defaultValue = "1") int entid) {
		return PhpResponse.ok(frameService.getFormData(entid, id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新部门")
	public PhpResponse<String> update(@PathVariable long id, @RequestParam(defaultValue = "1") int entid,
			@RequestBody FrameUpdateDTO dto) {
		frameService.updateDepartment(id, entid, dto);
		return PhpResponse.ok("修改成功");
	}

	@GetMapping("/{id}/info")
	@Operation(summary = "部门详情")
	public PhpResponse<FrameDetailVO> info(@PathVariable long id, @RequestParam(defaultValue = "1") int entid) {
		return PhpResponse.ok(frameService.departmentInfo(id, entid));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除部门")
	public PhpResponse<String> destroy(@PathVariable long id, @RequestParam(defaultValue = "1") int entid) {
		frameService.deleteDepartment(id, entid);
		return PhpResponse.ok("删除成功");
	}

	@GetMapping("/users/{frameId}")
	@Operation(summary = "部门负责人/成员")
	public PhpResponse<List<FrameAdminBriefVO>> frameUsers(@PathVariable int frameId,
			@RequestParam(defaultValue = "1") int entid) {
		return PhpResponse.ok(frameService.getFrameUsers(frameId, entid));
	}

	@GetMapping("/scope")
	@Operation(summary = "管理范围部门")
	public PhpResponse<List<FrameScopeItemVO>> scope(@RequestParam(defaultValue = "1") int entid) {
		Long uid = currentUserId();
		if (uid == null) {
			return PhpResponse.failed("未登录");
		}
		return PhpResponse.ok(frameService.scopeFrames(uid, entid));
	}

	private static Long currentUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof OaCurrentUser u)) {
			return null;
		}
		return u.getId();
	}

}
