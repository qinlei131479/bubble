package com.bubblecloud.oa.api.vo.common;

import java.util.Collections;
import java.util.List;

import lombok.Data;

/**
 * GET /ent/common/message 消息角标占位。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
public class CommonMessageVO {

	private List<CommonMessageItemVO> list = Collections.emptyList();

	private Integer messageNum;

	public CommonMessageVO() {
		this.messageNum = 0;
	}

}
