package com.bubblecloud.oa.api.vo.client;

import lombok.Data;

/**
 * 付款提醒详情占位。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
public class ClientRemindInfoVO {

	private Long id;

	private Integer eid;

	private Integer cid;

	private Integer types;

	private Integer remindId;

}
