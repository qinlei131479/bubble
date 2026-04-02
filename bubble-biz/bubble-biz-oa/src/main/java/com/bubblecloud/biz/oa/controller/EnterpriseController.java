package com.bubblecloud.biz.oa.controller;

import java.util.Map;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.EnterpriseService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.EnterpriseUpdateDTO;
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseEntInfoVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseMessageListVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseQuantityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业管理（对齐 PHP {@code ent/company}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/company")
@Tag(name = "企业人事")
public class EnterpriseController {

	private final EnterpriseService enterpriseService;

	@GetMapping("/user-card/page")
	@Operation(summary = "员工档案分页（占位）")
	public R<SimplePageVO> page(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return R.phpOk(SimplePageVO.empty(current, size));
	}

	@GetMapping("/info")
	@Operation(summary = "当前企业详情")
	public R<EnterpriseEntInfoVO> details(@RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(enterpriseService.getEntAndUserInfo(entid));
	}

	@PutMapping("/info")
	@Operation(summary = "修改当前企业详情")
	public R<String> update(@RequestParam(defaultValue = "1") Long entid, @RequestBody EnterpriseUpdateDTO dto) {
		Enterprise obj = PojoConvertUtil.convertPojo(dto, Enterprise.class);
		obj.setId(entid);
		enterpriseService.update(obj);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@GetMapping("/quantity/{type}")
	@Operation(summary = "获取统计数量")
	public R<EnterpriseQuantityVO> quantity(@PathVariable String type, @RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(enterpriseService.getQuantity(type, entid));
	}

	@GetMapping("/message")
	@Operation(summary = "消息中心列表（工作台系统通知）")
	public R<EnterpriseMessageListVO> messageList(@RequestParam Map<String, String> query) {
		return R.phpOk(new EnterpriseMessageListVO());
	}

}
