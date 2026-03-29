package com.bubblecloud.oa.api.vo.company;

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
public class CompanyMessageListVO {

	private List<CompanyMessageItemVO> list = Collections.emptyList();

	private Integer count;

	public CompanyMessageListVO() {
		this.count = 0;
	}

}
