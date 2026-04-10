package com.bubblecloud.oa.api.vo.enterprise;

import java.util.Collections;
import java.util.List;

import lombok.Data;

/**
 * GET /ent/company/message 消息中心占位。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
public class EnterpriseMessageListVO {

	private List<EnterpriseMessageItemVO> list = Collections.emptyList();

	private Integer count;

	public EnterpriseMessageListVO() {
		this.count = 0;
	}

}
