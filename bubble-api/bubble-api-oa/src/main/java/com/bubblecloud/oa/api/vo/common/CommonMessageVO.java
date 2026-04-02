package com.bubblecloud.oa.api.vo.common;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.oa.api.entity.EnterpriseMessageNotice;
import lombok.Data;

/**
 * GET /ent/common/message：list + messageNum。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
public class CommonMessageVO {

	private List<EnterpriseMessageNotice> list = Collections.emptyList();

	private Long messageNum;

	public CommonMessageVO() {
		this.messageNum = 0L;
	}

}
