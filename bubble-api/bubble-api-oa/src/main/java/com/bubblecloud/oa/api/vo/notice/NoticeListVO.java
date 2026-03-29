package com.bubblecloud.oa.api.vo.notice;

import java.util.Collections;
import java.util.List;

import lombok.Data;

/**
 * GET /ent/notice/list 占位。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
public class NoticeListVO {

	private List<NoticeItemVO> list = Collections.emptyList();

	private Integer count;

	public NoticeListVO() {
		this.count = 0;
	}

}
