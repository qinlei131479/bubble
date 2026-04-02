package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.FrameService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
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
	public R<List<FrameDepartmentTreeNodeVO>> list(@RequestParam(defaultValue = "1") Integer is_show,
												   @RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(frameService.departmentTreeList(is_show, entid));
	}

	@GetMapping("/tree")
	@Operation(summary = "权限/范围用部门树")
	public R<List<FrameAuthTreeNodeVO>> tree(@RequestParam(defaultValue = "0") Integer role,
											 @RequestParam(defaultValue = "0") Integer scope, @RequestParam(defaultValue = "1") Long entid) {
		Long uid = OaSecurityUtil.currentUserId();
		return R.phpOk(frameService.getTree(uid, entid, role == 1, scope == 1));
	}

	@GetMapping("/user")
	@Operation(summary = "部门人员树")
	public R<List<FrameUserTreeNodeVO>> userTree(@RequestParam(defaultValue = "0") Integer role,
												 @RequestParam(defaultValue = "0") Integer leave, @RequestParam(defaultValue = "1") Long entid) {
		Long uid = OaSecurityUtil.currentUserId();
		return R.phpOk(frameService.getUserTree(uid, entid, role == 1, leave == 1));
	}

	@GetMapping("/create")
	@Operation(summary = "新增部门表单")
	public R<FrameFormDataVO> createForm(@RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(frameService.getFormData(entid, 0L));
	}

	@PostMapping
	@Operation(summary = "保存部门")
	public R<String> create(@RequestBody FrameSaveDTO dto) {
		frameService.createDepartment(dto);
		return R.phpOk("保存成功");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑部门表单")
	public R<FrameFormDataVO> details(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(frameService.getFormData(entid, id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新部门")
	public R<String> update(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid,
							@RequestBody FrameUpdateDTO dto) {
		frameService.updateDepartment(id, entid, dto);
		return R.phpOk("修改成功");
	}

	@GetMapping("/{id}/info")
	@Operation(summary = "部门详情")
	public R<FrameDetailVO> info(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(frameService.departmentInfo(id, entid));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除部门")
	public R<String> removeById(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid) {
		frameService.deleteDepartment(id, entid);
		return R.phpOk("删除成功");
	}

	@GetMapping("/users/{frameId}")
	@Operation(summary = "部门负责人/成员")
	public R<List<FrameAdminBriefVO>> frameUsers(@PathVariable Integer frameId,
												 @RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(frameService.getFrameUsers(frameId, entid));
	}

	@GetMapping("/scope")
	@Operation(summary = "管理范围部门")
	public R<List<FrameScopeItemVO>> scope(@RequestParam(defaultValue = "1") Long entid) {
		Long uid = OaSecurityUtil.currentUserId();
		return R.phpOk(frameService.scopeFrames(uid, entid));
	}

}
