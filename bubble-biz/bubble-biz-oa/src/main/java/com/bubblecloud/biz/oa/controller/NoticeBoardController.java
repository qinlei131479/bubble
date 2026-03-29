package com.bubblecloud.biz.oa.controller;

import java.util.Map;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.notice.NoticeBoardListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知公告列表（PHP ent/notice/list）。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
@RestController
@RequestMapping("/ent/notice")
@Tag(name = "通知公告")
public class NoticeBoardController {

	@GetMapping("/list")
	@Operation(summary = "通知公告列表")
	public PhpResponse<NoticeBoardListVO> list(@RequestParam Map<String, String> query) {
		NoticeBoardListVO vo = new NoticeBoardListVO();
		vo.setList(java.util.Collections.emptyList());
		vo.setCount(0);
		return PhpResponse.ok(vo);
	}

}
